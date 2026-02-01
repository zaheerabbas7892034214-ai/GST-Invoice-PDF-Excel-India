package com.gstinvoice.pdfexcel.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gstinvoice.pdfexcel.data.model.ExtractedInvoiceData
import com.gstinvoice.pdfexcel.data.model.ExtractedLineItem
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class PdfExtractor(private val context: Context) {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractInvoiceData(uri: Uri): ExtractedInvoiceData {
        val text = extractTextFromPdf(uri)
        return parseInvoiceData(text)
    }

    private suspend fun extractTextFromPdf(uri: Uri): String {
        val stringBuilder = StringBuilder()

        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { fileDescriptor ->
                PdfRenderer(fileDescriptor).use { pdfRenderer ->
                    for (pageIndex in 0 until pdfRenderer.pageCount) {
                        pdfRenderer.openPage(pageIndex).use { page ->
                            val bitmap = Bitmap.createBitmap(
                                page.width * 2,
                                page.height * 2,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )

                            val inputImage = InputImage.fromBitmap(bitmap, 0)
                            val visionText = textRecognizer.process(inputImage).await()
                            stringBuilder.append(visionText.text)
                            stringBuilder.append("\n\n")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }

    private fun parseInvoiceData(text: String): ExtractedInvoiceData {
        val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Extract invoice number
        val invoiceNumber = extractInvoiceNumber(lines)

        // Extract invoice date
        val invoiceDate = extractInvoiceDate(lines)

        // Extract supplier details
        val supplierName = extractSupplierName(lines)
        val supplierGstin = extractGstin(lines, "supplier")

        // Extract buyer GSTIN
        val buyerGstin = extractGstin(lines, "buyer")

        // Extract line items
        val lineItems = extractLineItems(lines)

        return ExtractedInvoiceData(
            invoiceNumber = invoiceNumber,
            invoiceDate = invoiceDate,
            supplierName = supplierName,
            supplierGstin = supplierGstin,
            buyerGstin = buyerGstin,
            lineItems = lineItems
        )
    }

    private fun extractInvoiceNumber(lines: List<String>): String? {
        val patterns = listOf(
            Regex("invoice\\s*(?:no|number|#)\\s*:?\\s*([A-Z0-9/-]+)", RegexOption.IGNORE_CASE),
            Regex("bill\\s*(?:no|number|#)\\s*:?\\s*([A-Z0-9/-]+)", RegexOption.IGNORE_CASE),
            Regex("inv\\s*(?:no|#)\\s*:?\\s*([A-Z0-9/-]+)", RegexOption.IGNORE_CASE)
        )

        for (line in lines) {
            for (pattern in patterns) {
                val match = pattern.find(line)
                if (match != null) {
                    return match.groupValues[1].trim()
                }
            }
        }
        return null
    }

    private fun extractInvoiceDate(lines: List<String>): String? {
        val datePattern = Regex(
            "(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})|" +
                    "(\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{2,4})",
            RegexOption.IGNORE_CASE
        )

        for (line in lines) {
            if (line.contains("date", ignoreCase = true) || line.contains("dated", ignoreCase = true)) {
                val match = datePattern.find(line)
                if (match != null) {
                    return normalizeDate(match.value)
                }
            }
        }

        // If no date found with keyword, look for any date in first 10 lines
        for (i in 0 until minOf(10, lines.size)) {
            val match = datePattern.find(lines[i])
            if (match != null) {
                return normalizeDate(match.value)
            }
        }

        return null
    }

    private fun normalizeDate(dateStr: String): String {
        val formats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
            SimpleDateFormat("dd/MM/yy", Locale.ENGLISH),
            SimpleDateFormat("dd-MM-yy", Locale.ENGLISH)
        )

        for (format in formats) {
            try {
                val date = format.parse(dateStr)
                if (date != null) {
                    return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date)
                }
            } catch (e: Exception) {
                // Continue to next format
            }
        }

        return dateStr
    }

    private fun extractSupplierName(lines: List<String>): String? {
        val keywords = listOf("supplier", "seller", "vendor", "from")
        
        for (i in lines.indices) {
            val line = lines[i]
            if (keywords.any { line.contains(it, ignoreCase = true) }) {
                // Try to find name in next few lines
                for (j in i + 1 until minOf(i + 5, lines.size)) {
                    val nextLine = lines[j].trim()
                    if (nextLine.isNotEmpty() && !nextLine.contains(Regex("\\d{2}[A-Z]{5}\\d{4}[A-Z]\\d[Z][A-Z\\d]"))) {
                        return nextLine
                    }
                }
            }
        }

        // If not found, use first non-empty line
        return lines.firstOrNull { it.length > 5 && !it.contains("invoice", ignoreCase = true) }
    }

    private fun extractGstin(lines: List<String>, type: String): String? {
        val gstinPattern = Regex("\\d{2}[A-Z]{5}\\d{4}[A-Z]\\d[Z][A-Z\\d]")
        val keywords = if (type == "supplier") {
            listOf("gstin", "seller gstin", "supplier gstin", "vendor gstin")
        } else {
            listOf("buyer gstin", "customer gstin", "bill to gstin")
        }

        for (i in lines.indices) {
            val line = lines[i]
            if (keywords.any { line.contains(it, ignoreCase = true) }) {
                // Look in current and next few lines
                for (j in i until minOf(i + 3, lines.size)) {
                    val match = gstinPattern.find(lines[j])
                    if (match != null) {
                        return match.value
                    }
                }
            }
        }

        // If keywords not found, return first GSTIN found for supplier
        if (type == "supplier") {
            for (line in lines) {
                val match = gstinPattern.find(line)
                if (match != null) {
                    return match.value
                }
            }
        }

        return null
    }

    private fun extractLineItems(lines: List<String>): List<ExtractedLineItem> {
        val items = mutableListOf<ExtractedLineItem>()
        val seenDescriptions = mutableSetOf<String>()

        // Common header keywords to skip
        val headerKeywords = listOf(
            "description", "item", "particulars", "hsn", "sac", "quantity", "qty",
            "rate", "amount", "cgst", "sgst", "igst", "gst", "tax", "total"
        )

        var i = 0
        while (i < lines.size) {
            val line = lines[i]

            // Skip headers
            if (headerKeywords.any { line.contains(it, ignoreCase = true) } && 
                headerKeywords.count { line.contains(it, ignoreCase = true) } >= 2) {
                i++
                continue
            }

            // Skip footers
            if (line.contains("total", ignoreCase = true) || 
                line.contains("subtotal", ignoreCase = true) ||
                line.contains("grand", ignoreCase = true)) {
                i++
                continue
            }

            // Try to extract line item
            val item = parseLineItem(line, lines, i)
            if (item != null && item.description != null) {
                // Deduplicate
                val normalizedDesc = item.description.toLowerCase().trim()
                if (normalizedDesc !in seenDescriptions && normalizedDesc.length > 3) {
                    seenDescriptions.add(normalizedDesc)
                    items.add(item)
                }
            }

            i++
        }

        return items
    }

    private fun parseLineItem(line: String, allLines: List<String>, currentIndex: Int): ExtractedLineItem? {
        val tokens = line.split(Regex("\\s+"))
        
        // Skip if line is too short or just numbers
        if (tokens.size < 2 || tokens.all { it.matches(Regex("[\\d.,]+")) }) {
            return null
        }

        // Try to extract structured data
        val numbers = extractNumbers(line)
        
        val description = extractDescription(tokens)
        if (description.isEmpty()) {
            return null
        }

        val hsnSac = extractHsnSac(line, allLines, currentIndex)
        
        val quantity = numbers.getOrNull(0)
        val rate = numbers.getOrNull(1)
        val taxableValue = numbers.getOrNull(2)
        
        // Try to extract tax amounts
        var cgstAmount: Double? = null
        var sgstAmount: Double? = null
        var igstAmount: Double? = null
        var totalAmount: Double? = null

        if (numbers.size >= 4) {
            // Assuming format: qty, rate, taxable, cgst, sgst, total
            when {
                numbers.size == 4 -> totalAmount = numbers[3]
                numbers.size == 5 -> {
                    cgstAmount = numbers[3]
                    totalAmount = numbers[4]
                }
                numbers.size >= 6 -> {
                    cgstAmount = numbers[3]
                    sgstAmount = numbers[4]
                    totalAmount = numbers[5]
                }
            }
        }

        return ExtractedLineItem(
            description = description,
            hsnSac = hsnSac,
            quantity = quantity,
            rate = rate,
            taxableValue = taxableValue,
            cgstAmount = cgstAmount,
            sgstAmount = sgstAmount,
            igstAmount = igstAmount,
            totalAmount = totalAmount
        )
    }

    private fun extractNumbers(text: String): List<Double> {
        val numberPattern = Regex("[\\d,]+\\.?\\d*")
        return numberPattern.findAll(text)
            .mapNotNull { 
                try {
                    it.value.replace(",", "").toDoubleOrNull()
                } catch (e: Exception) {
                    null
                }
            }
            .filter { it > 0 }
            .toList()
    }

    private fun extractDescription(tokens: List<String>): String {
        // Take first few tokens that are not numbers as description
        val descTokens = tokens.takeWhile { 
            !it.matches(Regex("[\\d.,]+")) && it.length > 1
        }
        
        if (descTokens.isEmpty()) {
            // Try to find any text tokens
            val textTokens = tokens.filter { 
                !it.matches(Regex("[\\d.,]+")) && it.length > 1
            }
            return textTokens.take(3).joinToString(" ")
        }
        
        return descTokens.joinToString(" ")
    }

    private fun extractHsnSac(line: String, allLines: List<String>, currentIndex: Int): String? {
        // HSN/SAC is typically 4-8 digit number
        val hsnPattern = Regex("\\b\\d{4,8}\\b")
        
        // Check current line
        val match = hsnPattern.find(line)
        if (match != null) {
            return match.value
        }

        // Check next line if available
        if (currentIndex + 1 < allLines.size) {
            val nextMatch = hsnPattern.find(allLines[currentIndex + 1])
            if (nextMatch != null) {
                return nextMatch.value
            }
        }

        return null
    }

    fun release() {
        textRecognizer.close()
    }
}
