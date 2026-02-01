package com.gstinvoice.pdfexcel.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdfexcel.GSTInvoiceApplication
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExportViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GSTInvoiceApplication
    private val repository = app.invoiceRepository
    private val exportManager = app.exportManager
    private val billingManager = app.billingManager

    val isPremium: StateFlow<Boolean> = billingManager.isPremium

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult.asStateFlow()

    fun exportToCSV(invoiceIds: List<Long>, outputUri: Uri) {
        viewModelScope.launch {
            if (!isPremium.value) {
                _exportResult.value = ExportResult.Error("Premium required to export")
                return@launch
            }

            _isExporting.value = true
            try {
                val invoices = loadInvoices(invoiceIds)
                val result = exportManager.exportToCSV(invoices, outputUri)
                
                if (result.isSuccess) {
                    _exportResult.value = ExportResult.Success("CSV exported successfully")
                } else {
                    _exportResult.value = ExportResult.Error(result.exceptionOrNull()?.message ?: "Export failed")
                }
            } catch (e: Exception) {
                _exportResult.value = ExportResult.Error(e.message ?: "Export failed")
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportToXLSX(invoiceIds: List<Long>, outputUri: Uri) {
        viewModelScope.launch {
            if (!isPremium.value) {
                _exportResult.value = ExportResult.Error("Premium required to export")
                return@launch
            }

            _isExporting.value = true
            try {
                val invoices = loadInvoices(invoiceIds)
                val result = exportManager.exportToXLSX(invoices, outputUri)
                
                if (result.isSuccess) {
                    _exportResult.value = ExportResult.Success("Excel exported successfully")
                } else {
                    _exportResult.value = ExportResult.Error(result.exceptionOrNull()?.message ?: "Export failed")
                }
            } catch (e: Exception) {
                _exportResult.value = ExportResult.Error(e.message ?: "Export failed")
            } finally {
                _isExporting.value = false
            }
        }
    }

    private suspend fun loadInvoices(invoiceIds: List<Long>): List<InvoiceWithItems> {
        val invoices = mutableListOf<InvoiceWithItems>()
        for (id in invoiceIds) {
            val invoice = repository.getInvoiceWithItems(id)
            if (invoice != null) {
                invoices.add(invoice)
            }
        }
        return invoices
    }

    fun clearResult() {
        _exportResult.value = null
    }

    sealed class ExportResult {
        data class Success(val message: String) : ExportResult()
        data class Error(val message: String) : ExportResult()
    }
}
