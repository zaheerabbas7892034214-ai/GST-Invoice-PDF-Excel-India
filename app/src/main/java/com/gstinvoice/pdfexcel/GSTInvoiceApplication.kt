package com.gstinvoice.pdfexcel

import android.app.Application
import androidx.room.Room
import com.gstinvoice.pdfexcel.billing.BillingManager
import com.gstinvoice.pdfexcel.data.AppDatabase
import com.gstinvoice.pdfexcel.data.repository.InvoiceRepository
import com.gstinvoice.pdfexcel.util.ExportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class GSTInvoiceApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "gst_invoice_database"
        ).build()
    }

    val invoiceRepository: InvoiceRepository by lazy {
        InvoiceRepository(
            applicationContext,
            database.invoiceDao(),
            database.lineItemDao()
        )
    }

    val billingManager: BillingManager by lazy {
        BillingManager(
            applicationContext,
            database.purchaseDao(),
            applicationScope
        )
    }

    val exportManager: ExportManager by lazy {
        ExportManager(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        invoiceRepository.release()
        billingManager.endConnection()
    }
}
