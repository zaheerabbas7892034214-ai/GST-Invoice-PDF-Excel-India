package com.gstinvoice.pdfexcel.data.dao

import androidx.room.*
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LineItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItem(lineItem: LineItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItems(lineItems: List<LineItemEntity>)

    @Update
    suspend fun updateLineItem(lineItem: LineItemEntity)

    @Delete
    suspend fun deleteLineItem(lineItem: LineItemEntity)

    @Query("SELECT * FROM line_items WHERE invoiceId = :invoiceId ORDER BY lineNumber ASC")
    fun getLineItemsByInvoiceId(invoiceId: Long): Flow<List<LineItemEntity>>

    @Query("SELECT * FROM line_items WHERE invoiceId = :invoiceId ORDER BY lineNumber ASC")
    suspend fun getLineItemsByInvoiceIdSync(invoiceId: Long): List<LineItemEntity>

    @Query("DELETE FROM line_items WHERE invoiceId = :invoiceId")
    suspend fun deleteLineItemsByInvoiceId(invoiceId: Long)

    @Query("SELECT COUNT(*) FROM line_items WHERE invoiceId = :invoiceId")
    suspend fun getLineItemCount(invoiceId: Long): Int
}
