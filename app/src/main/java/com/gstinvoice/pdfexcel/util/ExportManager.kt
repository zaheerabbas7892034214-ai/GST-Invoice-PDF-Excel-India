package com.gstinvoice.pdfexcel.util

import android.content.Context
import android.net.Uri
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity
import com.gstinvoice.pdfexcel.data.model.InvoiceWithItems
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream

class ExportManager(private val context: Context) {

    fun exportToCSV(invoicesWithItems: List<InvoiceWithItems>, outputUri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                val csvContent = buildCSVContent(invoicesWithItems)
                outputStream.write(csvContent.toByteArray())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun exportToXLSX(invoicesWithItems: List<InvoiceWithItems>, outputUri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                createExcelWorkbook(invoicesWithItems, outputStream)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildCSVContent(invoicesWithItems: List<InvoiceWithItems>): String {
        val sb = StringBuilder()

        // CSV Header
        sb.append("Invoice Number,Invoice Date,Supplier Name,Supplier GSTIN,Buyer GSTIN,")
        sb.append("Line No,Description,HSN/SAC,Quantity,Rate,Taxable Value,")
        sb.append("CGST Rate,CGST Amount,SGST Rate,SGST Amount,IGST Rate,IGST Amount,Total Amount\n")

        // Data rows
        for (invoiceWithItems in invoicesWithItems) {
            val invoice = invoiceWithItems.invoice
            val lineItems = invoiceWithItems.lineItems

            for ((index, item) in lineItems.withIndex()) {
                sb.append(escapeCSV(invoice.invoiceNumber ?: "")).append(",")
                sb.append(escapeCSV(invoice.invoiceDate?.toString() ?: "")).append(",")
                sb.append(escapeCSV(invoice.supplierName ?: "")).append(",")
                sb.append(escapeCSV(invoice.supplierGstin ?: "")).append(",")
                sb.append(escapeCSV(invoice.buyerGstin ?: "")).append(",")
                sb.append(index + 1).append(",")
                sb.append(escapeCSV(item.description ?: "")).append(",")
                sb.append(escapeCSV(item.hsnSac ?: "")).append(",")
                sb.append(item.quantity ?: "").append(",")
                sb.append(item.rate ?: "").append(",")
                sb.append(item.taxableValue ?: "").append(",")
                sb.append(item.cgstRate ?: "").append(",")
                sb.append(item.cgstAmount ?: "").append(",")
                sb.append(item.sgstRate ?: "").append(",")
                sb.append(item.sgstAmount ?: "").append(",")
                sb.append(item.igstRate ?: "").append(",")
                sb.append(item.igstAmount ?: "").append(",")
                sb.append(item.totalAmount ?: "").append("\n")
            }
        }

        return sb.toString()
    }

    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun createExcelWorkbook(invoicesWithItems: List<InvoiceWithItems>, outputStream: OutputStream) {
        val workbook = XSSFWorkbook()

        // Create consolidated sheet
        createConsolidatedSheet(workbook, invoicesWithItems)

        // Create individual sheets for each invoice
        for (invoiceWithItems in invoicesWithItems) {
            val sheetName = sanitizeSheetName(invoiceWithItems.invoice.invoiceNumber ?: "Invoice")
            createInvoiceSheet(workbook, invoiceWithItems, sheetName)
        }

        workbook.write(outputStream)
        workbook.close()
    }

    private fun createConsolidatedSheet(workbook: Workbook, invoicesWithItems: List<InvoiceWithItems>) {
        val sheet = workbook.createSheet("Consolidated")

        // Create header style
        val headerStyle = createHeaderStyle(workbook)

        // Create header row
        val headerRow = sheet.createRow(0)
        val headers = listOf(
            "Invoice Number", "Invoice Date", "Supplier Name", "Supplier GSTIN", "Buyer GSTIN",
            "Line No", "Description", "HSN/SAC", "Quantity", "Rate", "Taxable Value",
            "CGST Rate", "CGST Amount", "SGST Rate", "SGST Amount", "IGST Rate", "IGST Amount", "Total Amount"
        )

        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }

        // Data rows
        var rowNum = 1
        for (invoiceWithItems in invoicesWithItems) {
            val invoice = invoiceWithItems.invoice
            val lineItems = invoiceWithItems.lineItems

            for ((lineIndex, item) in lineItems.withIndex()) {
                val row = sheet.createRow(rowNum++)
                
                row.createCell(0).setCellValue(invoice.invoiceNumber ?: "")
                row.createCell(1).setCellValue(invoice.invoiceDate?.toString() ?: "")
                row.createCell(2).setCellValue(invoice.supplierName ?: "")
                row.createCell(3).setCellValue(invoice.supplierGstin ?: "")
                row.createCell(4).setCellValue(invoice.buyerGstin ?: "")
                row.createCell(5).setCellValue((lineIndex + 1).toDouble())
                row.createCell(6).setCellValue(item.description ?: "")
                row.createCell(7).setCellValue(item.hsnSac ?: "")
                row.createCell(8).setCellValue(item.quantity ?: 0.0)
                row.createCell(9).setCellValue(item.rate ?: 0.0)
                row.createCell(10).setCellValue(item.taxableValue ?: 0.0)
                row.createCell(11).setCellValue(item.cgstRate ?: 0.0)
                row.createCell(12).setCellValue(item.cgstAmount ?: 0.0)
                row.createCell(13).setCellValue(item.sgstRate ?: 0.0)
                row.createCell(14).setCellValue(item.sgstAmount ?: 0.0)
                row.createCell(15).setCellValue(item.igstRate ?: 0.0)
                row.createCell(16).setCellValue(item.igstAmount ?: 0.0)
                row.createCell(17).setCellValue(item.totalAmount ?: 0.0)
            }
        }

        // Auto-size columns
        for (i in 0 until headers.size) {
            sheet.autoSizeColumn(i)
        }
    }

    private fun createInvoiceSheet(workbook: Workbook, invoiceWithItems: InvoiceWithItems, sheetName: String) {
        val sheet = workbook.createSheet(sheetName)
        val invoice = invoiceWithItems.invoice
        val lineItems = invoiceWithItems.lineItems

        // Create header style
        val headerStyle = createHeaderStyle(workbook)
        val boldStyle = createBoldStyle(workbook)

        var rowNum = 0

        // Invoice details
        var row = sheet.createRow(rowNum++)
        var cell = row.createCell(0)
        cell.setCellValue("Invoice Number:")
        cell.cellStyle = boldStyle
        row.createCell(1).setCellValue(invoice.invoiceNumber ?: "")

        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("Invoice Date:")
        cell.cellStyle = boldStyle
        row.createCell(1).setCellValue(invoice.invoiceDate?.toString() ?: "")

        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("Supplier Name:")
        cell.cellStyle = boldStyle
        row.createCell(1).setCellValue(invoice.supplierName ?: "")

        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("Supplier GSTIN:")
        cell.cellStyle = boldStyle
        row.createCell(1).setCellValue(invoice.supplierGstin ?: "")

        row = sheet.createRow(rowNum++)
        cell = row.createCell(0)
        cell.setCellValue("Buyer GSTIN:")
        cell.cellStyle = boldStyle
        row.createCell(1).setCellValue(invoice.buyerGstin ?: "")

        // Empty row
        rowNum++

        // Line items header
        val headerRow = sheet.createRow(rowNum++)
        val headers = listOf(
            "Line No", "Description", "HSN/SAC", "Quantity", "Rate", "Taxable Value",
            "CGST Rate", "CGST Amount", "SGST Rate", "SGST Amount", "IGST Rate", "IGST Amount", "Total Amount"
        )

        headers.forEachIndexed { index, header ->
            val headerCell = headerRow.createCell(index)
            headerCell.setCellValue(header)
            headerCell.cellStyle = headerStyle
        }

        // Line items data
        for ((index, item) in lineItems.withIndex()) {
            val dataRow = sheet.createRow(rowNum++)
            dataRow.createCell(0).setCellValue((index + 1).toDouble())
            dataRow.createCell(1).setCellValue(item.description ?: "")
            dataRow.createCell(2).setCellValue(item.hsnSac ?: "")
            dataRow.createCell(3).setCellValue(item.quantity ?: 0.0)
            dataRow.createCell(4).setCellValue(item.rate ?: 0.0)
            dataRow.createCell(5).setCellValue(item.taxableValue ?: 0.0)
            dataRow.createCell(6).setCellValue(item.cgstRate ?: 0.0)
            dataRow.createCell(7).setCellValue(item.cgstAmount ?: 0.0)
            dataRow.createCell(8).setCellValue(item.sgstRate ?: 0.0)
            dataRow.createCell(9).setCellValue(item.sgstAmount ?: 0.0)
            dataRow.createCell(10).setCellValue(item.igstRate ?: 0.0)
            dataRow.createCell(11).setCellValue(item.igstAmount ?: 0.0)
            dataRow.createCell(12).setCellValue(item.totalAmount ?: 0.0)
        }

        // Auto-size columns
        for (i in 0 until headers.size) {
            sheet.autoSizeColumn(i)
        }
    }

    private fun createHeaderStyle(workbook: Workbook): CellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.index
        style.setFont(font)
        style.fillForegroundColor = IndexedColors.DARK_BLUE.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.borderBottom = BorderStyle.THIN
        style.borderTop = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN
        return style
    }

    private fun createBoldStyle(workbook: Workbook): CellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        style.setFont(font)
        return style
    }

    private fun sanitizeSheetName(name: String): String {
        // Excel sheet names can't contain: \ / ? * [ ]
        // and must be <= 31 characters
        return name.replace(Regex("[\\\\/:?*\\[\\]]"), "_")
            .take(31)
    }
}
