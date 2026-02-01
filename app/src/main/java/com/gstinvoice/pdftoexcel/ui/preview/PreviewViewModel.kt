package com.gstinvoice.pdftoexcel.ui.preview

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdftoexcel.data.database.RecentFileEntity
import com.gstinvoice.pdftoexcel.data.model.ExportData
import com.gstinvoice.pdftoexcel.data.model.GSTInvoiceData
import com.gstinvoice.pdftoexcel.data.repository.InvoiceRepository
import com.gstinvoice.pdftoexcel.utils.BillingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreviewViewModel(
    private val repository: InvoiceRepository,
    val billingManager: BillingManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Idle)
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()
    
    val isPremiumUnlocked = billingManager.isPremiumUnlocked
    
    private var currentInvoiceData: GSTInvoiceData? = null
    private var currentFileName: String = ""
    
    fun loadPDF(uri: Uri, fileName: String) {
        currentFileName = fileName
        _uiState.value = PreviewUiState.Loading
        
        viewModelScope.launch {
            val result = repository.processPDF(uri)
            result.onSuccess { data ->
                currentInvoiceData = data
                val exportData = repository.getExportData(data)
                _uiState.value = PreviewUiState.Success(exportData)
                
                // Add to recent files
                repository.addRecentFile(
                    RecentFileEntity(
                        fileName = fileName,
                        filePath = uri.toString(),
                        conversionDate = System.currentTimeMillis(),
                        rowCount = exportData.rows.size
                    )
                )
            }.onFailure { error ->
                _uiState.value = PreviewUiState.Error(error.message ?: "Failed to process PDF")
            }
        }
    }
    
    fun getCurrentInvoiceData(): GSTInvoiceData? = currentInvoiceData
    
    sealed class PreviewUiState {
        object Idle : PreviewUiState()
        object Loading : PreviewUiState()
        data class Success(val data: ExportData) : PreviewUiState()
        data class Error(val message: String) : PreviewUiState()
    }
}

class PreviewViewModelFactory(
    private val repository: InvoiceRepository,
    private val billingManager: BillingManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreviewViewModel(repository, billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
