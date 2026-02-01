package com.gstinvoice.pdftoexcel.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gstinvoice.pdftoexcel.data.model.GSTInvoiceData
import com.gstinvoice.pdftoexcel.utils.DataCleaner
import kotlinx.coroutines.tasks.await
import java.io.IOException

class PDFProcessor(private val context: Context) {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT)
    
    suspend fun processPDF(uri: Uri): Result<GSTInvoiceData> {
        return try {
            val text = extractTextFromPDF(uri)
            if (text.isNotEmpty()) {
                val data = parseGSTData(text)
                Result.success(data)
            } else {
                // Try OCR if text extraction failed
                processWithOCR(uri)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun extractTextFromPDF(uri: Uri): String {
        val stringBuilder = StringBuilder()
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                val pageCount = pdfRenderer.pageCount
                
                for (i in 0 until pageCount.coerceAtMost(10)) { // Process first 10 pages max
                    pdfRenderer.openPage(i).use { page ->
                        // PdfRenderer doesn't directly extract text, we'll rely on OCR
                        // This is a placeholder for actual text extraction
                    }
                }
                pdfRenderer.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
    
    private suspend fun processWithOCR(uri: Uri): Result<GSTInvoiceData> {
        return try {
            val text = performOCR(uri)
            val data = parseGSTData(text)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun performOCR(uri: Uri): String {
        val stringBuilder = StringBuilder()
        
        context.contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val pageCount = pdfRenderer.pageCount
            
            for (i in 0 until pageCount.coerceAtMost(10)) {
                pdfRenderer.openPage(i).use { page ->
                    val bitmap = Bitmap.createBitmap(
                        page.width * 2,
                        page.height * 2,
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    
                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    val result = textRecognizer.process(inputImage).await()
                    stringBuilder.append(result.text)
                    stringBuilder.append("\n")
                    
                    bitmap.recycle()
                }
            }
            pdfRenderer.close()
        }
        
        return stringBuilder.toString()
    }
    
    private fun parseGSTData(text: String): GSTInvoiceData {
        val cleanedText = DataCleaner.cleanText(text)
        
        // Extract invoice number
        val invoiceNumber = extractPattern(cleanedText, 
            "(?i)invoice\\s*(?:no|number|#)?\\s*:?\\s*([A-Z0-9/-]+)")
        
        // Extract date
        val invoiceDate = extractPattern(cleanedText,
            "(?i)(?:invoice\\s*)?date\\s*:?\\s*([0-9]{1,2}[-/.][0-9]{1,2}[-/.][0-9]{2,4})")
        
        // Extract GSTIN
        val gstin = extractPattern(cleanedText,
            "(?i)gstin\\s*:?\\s*([0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1})")
        
        // Extract supplier name
        val supplierName = extractPattern(cleanedText,
            "(?i)(?:supplier|seller|from)\\s*(?:name)?\\s*:?\\s*([A-Za-z\\s]+)")
        
        // Extract amounts
        val totalAmount = extractPattern(cleanedText,
            "(?i)(?:total|grand\\s*total|net\\s*amount)\\s*:?\\s*₹?\\s*([0-9,]+\\.?[0-9]*)")
        
        val taxableValue = extractPattern(cleanedText,
            "(?i)(?:taxable\\s*value|subtotal)\\s*:?\\s*₹?\\s*([0-9,]+\\.?[0-9]*)")
        
        val cgst = extractPattern(cleanedText,
            "(?i)cgst\\s*:?\\s*₹?\\s*([0-9,]+\\.?[0-9]*)")
        
        val sgst = extractPattern(cleanedText,
            "(?i)sgst\\s*:?\\s*₹?\\s*([0-9,]+\\.?[0-9]*)")
        
        val igst = extractPattern(cleanedText,
            "(?i)igst\\s*:?\\s*₹?\\s*([0-9,]+\\.?[0-9]*)")
        
        return GSTInvoiceData(
            invoiceNumber = DataCleaner.normalizeInvoiceNumber(invoiceNumber),
            invoiceDate = DataCleaner.normalizeDate(invoiceDate),
            gstin = DataCleaner.normalizeGSTIN(gstin),
            supplierName = supplierName.trim(),
            taxableValue = DataCleaner.normalizeCurrency(taxableValue),
            cgst = DataCleaner.normalizeCurrency(cgst),
            sgst = DataCleaner.normalizeCurrency(sgst),
            igst = DataCleaner.normalizeCurrency(igst),
            totalAmount = DataCleaner.normalizeCurrency(totalAmount)
        )
    }
    
    private fun extractPattern(text: String, pattern: String): String {
        val regex = Regex(pattern)
        val match = regex.find(text)
        return match?.groupValues?.getOrNull(1)?.trim() ?: ""
    }
    
    fun release() {
        textRecognizer.close()
    }
}
