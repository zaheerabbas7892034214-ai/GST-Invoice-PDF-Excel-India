package com.gstinvoice.pdftoexcel.ui.export

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdftoexcel.data.model.GSTInvoiceData
import com.gstinvoice.pdftoexcel.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExportViewModel(
    private val repository: InvoiceRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ExportUiState>(ExportUiState.Idle)
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()
    
    fun exportToCSV(data: GSTInvoiceData, uri: Uri) {
        _uiState.value = ExportUiState.Loading
        
        viewModelScope.launch {
            val result = repository.exportToCSV(data, uri)
            result.onSuccess {
                _uiState.value = ExportUiState.Success("CSV exported successfully")
            }.onFailure { error ->
                _uiState.value = ExportUiState.Error(error.message ?: "Failed to export CSV")
            }
        }
    }
    
    fun exportToExcel(data: GSTInvoiceData, uri: Uri) {
        _uiState.value = ExportUiState.Loading
        
        viewModelScope.launch {
            val result = repository.exportToExcel(data, uri)
            result.onSuccess {
                _uiState.value = ExportUiState.Success("Excel exported successfully")
            }.onFailure { error ->
                _uiState.value = ExportUiState.Error(error.message ?: "Failed to export Excel")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ExportUiState.Idle
    }
    
    sealed class ExportUiState {
        object Idle : ExportUiState()
        object Loading : ExportUiState()
        data class Success(val message: String) : ExportUiState()
        data class Error(val message: String) : ExportUiState()
    }
}

class ExportViewModelFactory(
    private val repository: InvoiceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
