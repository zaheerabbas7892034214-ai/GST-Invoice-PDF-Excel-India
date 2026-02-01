package com.gstinvoice.pdftoexcel

import android.app.Application
import com.gstinvoice.pdftoexcel.data.repository.InvoiceRepository
import com.gstinvoice.pdftoexcel.utils.BillingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class GSTInvoiceApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob())
    
    val repository: InvoiceRepository by lazy {
        InvoiceRepository(this)
    }
    
    val billingManager: BillingManager by lazy {
        BillingManager(this, applicationScope)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        repository.cleanup()
        billingManager.endConnection()
    }
}
