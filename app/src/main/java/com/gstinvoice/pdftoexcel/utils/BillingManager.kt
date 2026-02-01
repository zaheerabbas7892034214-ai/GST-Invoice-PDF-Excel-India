package com.gstinvoice.pdftoexcel.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope
) : PurchasesUpdatedListener {
    
    companion object {
        const val PREMIUM_PRODUCT_ID = "premium_unlock"
        private const val PREFS_NAME = "billing_prefs"
        private const val KEY_PREMIUM_UNLOCKED = "premium_unlocked"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isPremiumUnlocked = MutableStateFlow(prefs.getBoolean(KEY_PREMIUM_UNLOCKED, false))
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked
    
    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState
    
    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    
    init {
        setupBillingClient()
    }
    
    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        
        connectToBilling()
    }
    
    private fun connectToBilling() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryPurchases()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Retry connection
                connectToBilling()
            }
        })
    }
    
    private fun queryProductDetails() {
        scope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PREMIUM_PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            
            withContext(Dispatchers.IO) {
                billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        productDetails = productDetailsList.firstOrNull()
                    }
                }
            }
        }
    }
    
    private fun queryPurchases() {
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            
            withContext(Dispatchers.IO) {
                val purchasesResult = billingClient?.queryPurchasesAsync(params)
                purchasesResult?.let { result ->
                    if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        handlePurchases(result.purchasesList)
                    }
                }
            }
        }
    }
    
    fun launchPurchaseFlow(activity: Activity) {
        val details = productDetails
        if (details == null) {
            _billingState.value = BillingState.Error("Product not available")
            return
        }
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        _billingState.value = BillingState.Loading
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let { handlePurchases(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _billingState.value = BillingState.Cancelled
            }
            else -> {
                _billingState.value = BillingState.Error("Purchase failed: ${billingResult.debugMessage}")
            }
        }
    }
    
    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.products.contains(PREMIUM_PRODUCT_ID) && 
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
                unlockPremium()
                _billingState.value = BillingState.Success
                return
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        scope.launch {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            withContext(Dispatchers.IO) {
                billingClient?.acknowledgePurchase(params) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        unlockPremium()
                    }
                }
            }
        }
    }
    
    fun restorePurchases() {
        _billingState.value = BillingState.Loading
        queryPurchases()
        
        // Set state back to idle if no purchases found after a delay
        scope.launch {
            kotlinx.coroutines.delay(2000)
            if (_billingState.value is BillingState.Loading) {
                if (_isPremiumUnlocked.value) {
                    _billingState.value = BillingState.Success
                } else {
                    _billingState.value = BillingState.Error("No purchases found")
                }
            }
        }
    }
    
    private fun unlockPremium() {
        prefs.edit().putBoolean(KEY_PREMIUM_UNLOCKED, true).apply()
        _isPremiumUnlocked.value = true
    }
    
    fun endConnection() {
        billingClient?.endConnection()
    }
    
    sealed class BillingState {
        object Idle : BillingState()
        object Loading : BillingState()
        object Success : BillingState()
        object Cancelled : BillingState()
        data class Error(val message: String) : BillingState()
    }
}
