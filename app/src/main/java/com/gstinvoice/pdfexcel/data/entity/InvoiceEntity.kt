package com.gstinvoice.pdfexcel.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gstinvoice.pdfexcel.data.Converters
import java.util.Date

@Entity(tableName = "invoices")
@TypeConverters(Converters::class)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String?,
    val invoiceDate: Date?,
    val supplierName: String?,
    val supplierGstin: String?,
    val buyerGstin: String?,
    val totalAmount: Double,
    val totalTaxableValue: Double,
    val totalCgst: Double,
    val totalSgst: Double,
    val totalIgst: Double,
    val pdfFileName: String,
    val pdfUri: String,
    val importedAt: Date = Date(),
    val itemCount: Int = 0
)
