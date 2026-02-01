package com.gstinvoice.pdfexcel.data.dao

import androidx.room.*
import com.gstinvoice.pdfexcel.data.entity.InvoiceEntity
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices ORDER BY importedAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceById(invoiceId: Long): InvoiceEntity?

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceWithItems(invoiceId: Long): InvoiceWithItems?

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY importedAt DESC")
    fun getAllInvoicesWithItems(): Flow<List<InvoiceWithItems>>

    @Query("SELECT * FROM invoices WHERE supplierName LIKE '%' || :query || '%' OR invoiceNumber LIKE '%' || :query || '%'")
    fun searchInvoices(query: String): Flow<List<InvoiceEntity>>
}
