package com.gstinvoice.pdfexcel.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdfexcel.GSTInvoiceApplication
import com.gstinvoice.pdfexcel.data.entity.InvoiceEntity
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GSTInvoiceApplication
    private val repository = app.invoiceRepository
    private val billingManager = app.billingManager

    val recentInvoices: StateFlow<List<InvoiceEntity>> = repository.getAllInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isPremium: StateFlow<Boolean> = billingManager.isPremium

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun deleteInvoice(invoice: InvoiceEntity) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }
}
