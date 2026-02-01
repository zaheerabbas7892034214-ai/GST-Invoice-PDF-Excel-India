package com.gstinvoice.pdfexcel.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.gstinvoice.pdfexcel.data.dao.PurchaseDao
import com.gstinvoice.pdfexcel.data.entity.PurchaseEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class BillingManager(
    private val context: Context,
    private val purchaseDao: PurchaseDao,
    private val scope: CoroutineScope
) {
    companion object {
        const val PRODUCT_ID_PRO = "gst_pro_unlock"
        const val MAX_RETRY_COUNT = 5
    }

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _billingConnectionState = MutableStateFlow(BillingConnectionState.DISCONNECTED)
    val billingConnectionState: StateFlow<BillingConnectionState> = _billingConnectionState.asStateFlow()

    private var billingClient: BillingClient? = null
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingConnectionState.value = BillingConnectionState.CONNECTED
                    queryPurchases()
                    retryUnacknowledgedPurchases()
                } else {
                    _billingConnectionState.value = BillingConnectionState.ERROR
                }
            }

            override fun onBillingServiceDisconnected() {
                _billingConnectionState.value = BillingConnectionState.DISCONNECTED
                // Retry connection
                scope.launch {
                    delay(5000)
                    startConnection()
                }
            }
        })
    }

    fun queryPurchases() {
        if (billingClient?.isReady != true) {
            return
        }

        scope.launch {
            val purchasesResult = billingClient?.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

            purchasesResult?.purchasesList?.forEach { purchase ->
                handlePurchase(purchase)
            }

            // Check if user has premium
            val hasPremium = purchasesResult?.purchasesList?.any {
                it.products.contains(PRODUCT_ID_PRO) && it.purchaseState == Purchase.PurchaseState.PURCHASED
            } ?: false

            _isPremium.value = hasPremium
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        scope.launch {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Save to database
                val purchaseEntity = PurchaseEntity(
                    productId = purchase.products.first(),
                    purchaseToken = purchase.purchaseToken,
                    purchaseTime = Date(purchase.purchaseTime),
                    acknowledged = purchase.isAcknowledged,
                    orderId = purchase.orderId,
                    retryCount = 0
                )
                purchaseDao.insertPurchase(purchaseEntity)

                // Acknowledge if not already acknowledged
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }

                _isPremium.value = true
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        scope.launch {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            val result = withContext(Dispatchers.IO) {
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
            }

            if (result?.responseCode == BillingClient.BillingResponseCode.OK) {
                // Update database
                val entity = purchaseDao.getPurchaseByProductId(purchase.products.first())
                entity?.let {
                    purchaseDao.updatePurchase(it.copy(acknowledged = true))
                }
            } else {
                // Schedule retry
                val entity = purchaseDao.getPurchaseByProductId(purchase.products.first())
                entity?.let {
                    if (it.retryCount < MAX_RETRY_COUNT) {
                        purchaseDao.updatePurchase(
                            it.copy(
                                retryCount = it.retryCount + 1,
                                lastRetryTime = Date()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun retryUnacknowledgedPurchases() {
        scope.launch {
            val unacknowledgedPurchases = purchaseDao.getUnacknowledgedPurchases()
            
            for (purchaseEntity in unacknowledgedPurchases) {
                if (purchaseEntity.retryCount >= MAX_RETRY_COUNT) {
                    continue
                }

                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchaseEntity.purchaseToken)
                    .build()

                val result = withContext(Dispatchers.IO) {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
                }

                if (result?.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchaseDao.updatePurchase(purchaseEntity.copy(acknowledged = true))
                } else {
                    purchaseDao.updatePurchase(
                        purchaseEntity.copy(
                            retryCount = purchaseEntity.retryCount + 1,
                            lastRetryTime = Date()
                        )
                    )
                }
            }
        }
    }

    suspend fun launchPurchaseFlow(activity: Activity): BillingResult? {
        if (billingClient?.isReady != true) {
            return null
        }

        val productDetailsParams = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_ID_PRO)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(productDetailsParams))
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient?.queryProductDetails(params)
        }

        val productDetails = productDetailsResult?.productDetailsList?.firstOrNull()
            ?: return null

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    fun restorePurchases() {
        queryPurchases()
    }

    fun endConnection() {
        billingClient?.endConnection()
    }

    enum class BillingConnectionState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
}
