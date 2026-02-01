package com.gstinvoice.pdfexcel.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "line_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class LineItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val description: String?,
    val hsnSac: String?,
    val quantity: Double?,
    val rate: Double?,
    val taxableValue: Double?,
    val cgstRate: Double?,
    val cgstAmount: Double?,
    val sgstRate: Double?,
    val sgstAmount: Double?,
    val igstRate: Double?,
    val igstAmount: Double?,
    val totalAmount: Double?,
    val lineNumber: Int = 0
)
