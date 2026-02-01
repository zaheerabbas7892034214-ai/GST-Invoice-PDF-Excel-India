package com.gstinvoice.pdfexcel.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdfexcel.GSTInvoiceApplication
import com.gstinvoice.pdfexcel.billing.BillingManager
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PreviewViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GSTInvoiceApplication
    private val repository = app.invoiceRepository
    private val billingManager = app.billingManager

    val isPremium: StateFlow<Boolean> = billingManager.isPremium

    private val _selectedInvoiceIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedInvoiceIds: StateFlow<List<Long>> = _selectedInvoiceIds.asStateFlow()

    private val _invoicesWithItems = MutableStateFlow<List<InvoiceWithItems>>(emptyList())
    val invoicesWithItems: StateFlow<List<InvoiceWithItems>> = _invoicesWithItems.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _totalLineItemsCount = MutableStateFlow(0)
    val totalLineItemsCount: StateFlow<Int> = _totalLineItemsCount.asStateFlow()

    private val _visibleLineItemsCount = MutableStateFlow(0)
    val visibleLineItemsCount: StateFlow<Int> = _visibleLineItemsCount.asStateFlow()

    companion object {
        const val FREE_TIER_LIMIT = 15
    }

    fun loadInvoices(invoiceIds: List<Long>) {
        _selectedInvoiceIds.value = invoiceIds
        viewModelScope.launch {
            val invoices = mutableListOf<InvoiceWithItems>()
            var totalItems = 0

            for (id in invoiceIds) {
                val invoice = repository.getInvoiceWithItems(id)
                if (invoice != null) {
                    invoices.add(invoice)
                    totalItems += invoice.lineItems.size
                }
            }

            _invoicesWithItems.value = invoices
            _totalLineItemsCount.value = totalItems
            updateVisibleLineItems()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun updateVisibleLineItems() {
        val isPremiumUser = isPremium.value
        val total = _totalLineItemsCount.value
        _visibleLineItemsCount.value = if (isPremiumUser) total else minOf(total, FREE_TIER_LIMIT)
    }

    fun getFilteredInvoices(): List<InvoiceWithItems> {
        val query = _searchQuery.value
        if (query.isBlank()) {
            return _invoicesWithItems.value
        }

        return _invoicesWithItems.value.filter { invoiceWithItems ->
            val invoice = invoiceWithItems.invoice
            invoice.supplierName?.contains(query, ignoreCase = true) == true ||
                    invoice.invoiceNumber?.contains(query, ignoreCase = true) == true
        }
    }

    fun getVisibleLineItems(): List<Pair<InvoiceWithItems, List<LineItemEntity>>> {
        val isPremiumUser = isPremium.value
        val filteredInvoices = getFilteredInvoices()
        
        if (isPremiumUser) {
            return filteredInvoices.map { it to it.lineItems }
        }

        // For free tier, limit to 15 items across all invoices
        val result = mutableListOf<Pair<InvoiceWithItems, List<LineItemEntity>>>()
        var itemsShown = 0

        for (invoiceWithItems in filteredInvoices) {
            if (itemsShown >= FREE_TIER_LIMIT) break

            val remainingSlots = FREE_TIER_LIMIT - itemsShown
            val itemsToShow = minOf(invoiceWithItems.lineItems.size, remainingSlots)
            
            result.add(invoiceWithItems to invoiceWithItems.lineItems.take(itemsToShow))
            itemsShown += itemsToShow
        }

        return result
    }

    fun isPaywallActive(): Boolean {
        return !isPremium.value && _totalLineItemsCount.value > FREE_TIER_LIMIT
    }
}
