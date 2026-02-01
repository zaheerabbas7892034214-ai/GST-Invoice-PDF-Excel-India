package com.gstinvoice.pdfexcel.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gstinvoice.pdfexcel.data.dao.InvoiceDao
import com.gstinvoice.pdfexcel.data.dao.LineItemDao
import com.gstinvoice.pdfexcel.data.dao.PurchaseDao
import com.gstinvoice.pdfexcel.data.entity.InvoiceEntity
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import com.gstinvoice.pdfexcel.data.entity.PurchaseEntity

@Database(
    entities = [
        InvoiceEntity::class,
        LineItemEntity::class,
        PurchaseEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao
    abstract fun lineItemDao(): LineItemDao
    abstract fun purchaseDao(): PurchaseDao
}
