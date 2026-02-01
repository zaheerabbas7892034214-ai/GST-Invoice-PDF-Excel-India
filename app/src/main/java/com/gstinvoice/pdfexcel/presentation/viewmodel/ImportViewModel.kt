package com.gstinvoice.pdfexcel.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdfexcel.GSTInvoiceApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImportViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as GSTInvoiceApplication
    private val repository = app.invoiceRepository

    private val _importProgress = MutableStateFlow(0f)
    val importProgress: StateFlow<Float> = _importProgress.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importedInvoiceIds = MutableStateFlow<List<Long>>(emptyList())
    val importedInvoiceIds: StateFlow<List<Long>> = _importedInvoiceIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun importPdfs(uris: List<Uri>) {
        viewModelScope.launch {
            _isImporting.value = true
            _importProgress.value = 0f
            val invoiceIds = mutableListOf<Long>()

            uris.forEachIndexed { index, uri ->
                try {
                    val fileName = uri.lastPathSegment ?: "invoice_${System.currentTimeMillis()}.pdf"
                    val result = repository.importInvoiceFromPdf(uri, fileName)
                    
                    result.onSuccess { invoiceId ->
                        invoiceIds.add(invoiceId)
                    }.onFailure { error ->
                        _errorMessage.value = "Failed to import ${fileName}: ${error.message}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error importing PDF: ${e.message}"
                }
                
                _importProgress.value = (index + 1).toFloat() / uris.size
            }

            _importedInvoiceIds.value = invoiceIds
            _isImporting.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetImport() {
        _importedInvoiceIds.value = emptyList()
        _importProgress.value = 0f
        _errorMessage.value = null
    }
}
