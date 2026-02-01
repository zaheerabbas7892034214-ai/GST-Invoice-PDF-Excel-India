package com.gstinvoice.pdfexcel.presentation.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingResult
import com.gstinvoice.pdfexcel.GSTInvoiceApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaywallViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GSTInvoiceApplication
    private val billingManager = app.billingManager

    val isPremium: StateFlow<Boolean> = billingManager.isPremium
    val billingConnectionState = billingManager.billingConnectionState

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult.asStateFlow()

    fun launchPurchaseFlow(activity: Activity) {
        viewModelScope.launch {
            val result = billingManager.launchPurchaseFlow(activity)
            if (result == null) {
                _purchaseResult.value = PurchaseResult.Error("Billing not available")
            }
        }
    }

    fun restorePurchases() {
        billingManager.restorePurchases()
    }

    fun clearPurchaseResult() {
        _purchaseResult.value = null
    }

    sealed class PurchaseResult {
        object Success : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}
