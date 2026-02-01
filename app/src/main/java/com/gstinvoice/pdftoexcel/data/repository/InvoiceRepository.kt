package com.gstinvoice.pdftoexcel.data.repository

import android.content.Context
import android.net.Uri
import com.gstinvoice.pdftoexcel.data.database.AppDatabase
import com.gstinvoice.pdftoexcel.data.database.RecentFileEntity
import com.gstinvoice.pdftoexcel.data.model.ExportData
import com.gstinvoice.pdftoexcel.data.model.GSTInvoiceData
import com.gstinvoice.pdftoexcel.data.model.toExportData
import com.gstinvoice.pdftoexcel.pdf.PDFProcessor
import com.gstinvoice.pdftoexcel.utils.ExportUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class InvoiceRepository(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val recentFileDao = database.recentFileDao()
    private val pdfProcessor = PDFProcessor(context)
    
    fun getRecentFiles(): Flow<List<RecentFileEntity>> {
        return recentFileDao.getAllRecentFiles()
    }
    
    suspend fun addRecentFile(file: RecentFileEntity) = withContext(Dispatchers.IO) {
        recentFileDao.insertRecentFile(file)
    }
    
    suspend fun deleteRecentFile(fileId: Long) = withContext(Dispatchers.IO) {
        recentFileDao.deleteRecentFile(fileId)
    }
    
    suspend fun processPDF(uri: Uri): Result<GSTInvoiceData> = withContext(Dispatchers.IO) {
        pdfProcessor.processPDF(uri)
    }
    
    suspend fun exportToCSV(data: GSTInvoiceData, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        val exportData = data.toExportData()
        ExportUtils.exportToCSV(context, exportData, uri)
    }
    
    suspend fun exportToExcel(data: GSTInvoiceData, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        val exportData = data.toExportData()
        ExportUtils.exportToExcel(context, exportData, uri)
    }
    
    fun getExportData(data: GSTInvoiceData): ExportData {
        return data.toExportData()
    }
    
    fun cleanup() {
        pdfProcessor.release()
    }
}
