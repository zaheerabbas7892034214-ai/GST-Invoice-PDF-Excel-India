package com.gstinvoice.pdftoexcel.utils

import java.text.SimpleDateFormat
import java.util.*

object DataCleaner {
    
    fun cleanText(text: String): String {
        return text
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
    
    fun normalizeDate(date: String): String {
        if (date.isEmpty()) return ""
        
        val dateFormats = listOf(
            "dd-MM-yyyy",
            "dd/MM/yyyy",
            "dd.MM.yyyy",
            "dd-MM-yy",
            "dd/MM/yy",
            "yyyy-MM-dd",
            "dd MMM yyyy",
            "dd MMMM yyyy"
        )
        
        for (format in dateFormats) {
            try {
                val inputFormat = SimpleDateFormat(format, Locale.getDefault())
                inputFormat.isLenient = false
                val parsedDate = inputFormat.parse(date)
                if (parsedDate != null) {
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    return outputFormat.format(parsedDate)
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        return date // Return original if parsing fails
    }
    
    fun normalizeCurrency(amount: String): String {
        if (amount.isEmpty()) return ""
        
        return amount
            .replace("₹", "")
            .replace(",", "")
            .replace("\\s+".toRegex(), "")
            .trim()
    }
    
    fun normalizeGSTIN(gstin: String): String {
        if (gstin.isEmpty()) return ""
        
        val cleaned = gstin.replace("\\s+".toRegex(), "").uppercase()
        
        // Validate GSTIN format: 15 characters
        return if (cleaned.length == 15 && cleaned.matches(Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$"))) {
            cleaned
        } else {
            gstin.trim()
        }
    }
    
    fun normalizeInvoiceNumber(invoiceNumber: String): String {
        if (invoiceNumber.isEmpty()) return ""
        
        return invoiceNumber
            .replace("\\s+".toRegex(), "")
            .uppercase()
            .trim()
    }
    
    fun handleMultilineEntry(lines: List<String>): List<String> {
        val cleaned = mutableListOf<String>()
        var currentLine = ""
        
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                if (currentLine.isNotEmpty()) {
                    cleaned.add(currentLine)
                    currentLine = ""
                }
            } else {
                currentLine += if (currentLine.isEmpty()) trimmed else " $trimmed"
            }
        }
        
        if (currentLine.isNotEmpty()) {
            cleaned.add(currentLine)
        }
        
        return cleaned
    }
}
