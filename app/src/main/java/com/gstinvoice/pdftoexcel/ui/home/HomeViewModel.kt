package com.gstinvoice.pdftoexcel.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gstinvoice.pdftoexcel.data.database.RecentFileEntity
import com.gstinvoice.pdftoexcel.data.repository.InvoiceRepository
import com.gstinvoice.pdftoexcel.utils.BillingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: InvoiceRepository,
    val billingManager: BillingManager
) : ViewModel() {
    
    val recentFiles = repository.getRecentFiles()
    val isPremiumUnlocked = billingManager.isPremiumUnlocked
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun deleteRecentFile(fileId: Long) {
        viewModelScope.launch {
            repository.deleteRecentFile(fileId)
        }
    }
    
    sealed class HomeUiState {
        object Idle : HomeUiState()
        object Loading : HomeUiState()
        data class Error(val message: String) : HomeUiState()
    }
}

class HomeViewModelFactory(
    private val repository: InvoiceRepository,
    private val billingManager: BillingManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
