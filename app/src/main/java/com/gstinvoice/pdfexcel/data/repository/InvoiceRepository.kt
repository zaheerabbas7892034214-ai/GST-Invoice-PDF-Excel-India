package com.gstinvoice.pdfexcel.data.repository

import android.content.Context
import android.net.Uri
import com.gstinvoice.pdfexcel.data.dao.InvoiceDao
import com.gstinvoice.pdfexcel.data.dao.LineItemDao
import com.gstinvoice.pdfexcel.data.entity.InvoiceEntity
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import com.gstinvoice.pdfexcel.data.model.ExtractedInvoiceData
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import com.gstinvoice.pdfexcel.util.PdfExtractor
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class InvoiceRepository(
    private val context: Context,
    private val invoiceDao: InvoiceDao,
    private val lineItemDao: LineItemDao
) {
    private val pdfExtractor = PdfExtractor(context)

    suspend fun importInvoiceFromPdf(uri: Uri, fileName: String): Result<Long> {
        return try {
            val extractedData = pdfExtractor.extractInvoiceData(uri)
            val invoiceId = saveExtractedData(extractedData, uri, fileName)
            Result.success(invoiceId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveExtractedData(
        data: ExtractedInvoiceData,
        uri: Uri,
        fileName: String
    ): Long {
        // Calculate totals
        var totalAmount = 0.0
        var totalTaxableValue = 0.0
        var totalCgst = 0.0
        var totalSgst = 0.0
        var totalIgst = 0.0

        for (item in data.lineItems) {
            totalTaxableValue += item.taxableValue ?: 0.0
            totalCgst += item.cgstAmount ?: 0.0
            totalSgst += item.sgstAmount ?: 0.0
            totalIgst += item.igstAmount ?: 0.0
            totalAmount += item.totalAmount ?: 0.0
        }

        // Parse invoice date
        val invoiceDate = data.invoiceDate?.let { parseDate(it) }

        // Create invoice entity
        val invoice = InvoiceEntity(
            invoiceNumber = data.invoiceNumber,
            invoiceDate = invoiceDate,
            supplierName = data.supplierName,
            supplierGstin = data.supplierGstin,
            buyerGstin = data.buyerGstin,
            totalAmount = totalAmount,
            totalTaxableValue = totalTaxableValue,
            totalCgst = totalCgst,
            totalSgst = totalSgst,
            totalIgst = totalIgst,
            pdfFileName = fileName,
            pdfUri = uri.toString(),
            itemCount = data.lineItems.size
        )

        // Insert invoice
        val invoiceId = invoiceDao.insertInvoice(invoice)

        // Insert line items
        val lineItems = data.lineItems.mapIndexed { index, item ->
            LineItemEntity(
                invoiceId = invoiceId,
                description = item.description,
                hsnSac = item.hsnSac,
                quantity = item.quantity,
                rate = item.rate,
                taxableValue = item.taxableValue,
                cgstRate = item.cgstRate,
                cgstAmount = item.cgstAmount,
                sgstRate = item.sgstRate,
                sgstAmount = item.sgstAmount,
                igstRate = item.igstRate,
                igstAmount = item.igstAmount,
                totalAmount = item.totalAmount,
                lineNumber = index + 1
            )
        }

        lineItemDao.insertLineItems(lineItems)

        return invoiceId
    }

    private fun parseDate(dateStr: String): Date? {
        val formats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        )

        for (format in formats) {
            try {
                return format.parse(dateStr)
            } catch (e: Exception) {
                // Continue to next format
            }
        }

        return null
    }

    fun getAllInvoices(): Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    fun getAllInvoicesWithItems(): Flow<List<InvoiceWithItems>> = invoiceDao.getAllInvoicesWithItems()

    suspend fun getInvoiceWithItems(invoiceId: Long): InvoiceWithItems? =
        invoiceDao.getInvoiceWithItems(invoiceId)

    fun searchInvoices(query: String): Flow<List<InvoiceEntity>> = invoiceDao.searchInvoices(query)

    suspend fun deleteInvoice(invoice: InvoiceEntity) {
        invoiceDao.deleteInvoice(invoice)
    }

    fun getLineItemsByInvoiceId(invoiceId: Long): Flow<List<LineItemEntity>> =
        lineItemDao.getLineItemsByInvoiceId(invoiceId)

    fun release() {
        pdfExtractor.release()
    }
}
