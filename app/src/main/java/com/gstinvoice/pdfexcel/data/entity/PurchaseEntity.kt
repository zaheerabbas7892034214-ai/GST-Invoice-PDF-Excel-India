package com.gstinvoice.pdfexcel.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gstinvoice.pdfexcel.data.Converters
import java.util.Date

@Entity(tableName = "purchases")
@TypeConverters(Converters::class)
data class PurchaseEntity(
    @PrimaryKey
    val productId: String,
    val purchaseToken: String,
    val purchaseTime: Date,
    val acknowledged: Boolean = false,
    val orderId: String?,
    val autoRenewing: Boolean? = null,
    val retryCount: Int = 0,
    val lastRetryTime: Date? = null
)
