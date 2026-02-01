package com.gstinvoice.pdftoexcel.utils

import android.content.Context
import android.net.Uri
import com.gstinvoice.pdftoexcel.data.model.ExportData
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException

object ExportUtils {
    
    fun exportToCSV(context: Context, data: ExportData, uri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val csvBuilder = StringBuilder()
                
                // Write headers
                csvBuilder.append(data.headers.joinToString(",") { escapeCSV(it) })
                csvBuilder.append("\n")
                
                // Write rows
                data.rows.forEach { row ->
                    csvBuilder.append(row.joinToString(",") { escapeCSV(it) })
                    csvBuilder.append("\n")
                }
                
                outputStream.write(csvBuilder.toString().toByteArray())
                outputStream.flush()
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    
    fun exportToExcel(context: Context, data: ExportData, uri: Uri): Result<Unit> {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("GST Invoice Data")
            
            // Create header style
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                borderBottom = BorderStyle.THIN
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                alignment = HorizontalAlignment.CENTER
            }
            
            val headerFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 11
            }
            headerStyle.setFont(headerFont)
            
            // Create data style
            val dataStyle = workbook.createCellStyle().apply {
                borderBottom = BorderStyle.THIN
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
            }
            
            // Write headers
            val headerRow = sheet.createRow(0)
            data.headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }
            
            // Write data rows
            data.rows.forEachIndexed { rowIndex, rowData ->
                val row = sheet.createRow(rowIndex + 1)
                rowData.forEachIndexed { cellIndex, cellValue ->
                    val cell = row.createCell(cellIndex)
                    cell.setCellValue(cellValue)
                    cell.cellStyle = dataStyle
                }
            }
            
            // Auto-size columns
            data.headers.indices.forEach { i ->
                sheet.autoSizeColumn(i)
                // Add some extra width for padding
                val currentWidth = sheet.getColumnWidth(i)
                sheet.setColumnWidth(i, (currentWidth * 1.1).toInt().coerceAtMost(15000))
            }
            
            // Write to output stream
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                workbook.write(outputStream)
                outputStream.flush()
            }
            
            workbook.close()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
