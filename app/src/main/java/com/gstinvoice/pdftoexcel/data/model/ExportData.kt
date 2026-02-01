package com.gstinvoice.pdftoexcel.data.model

data class ExportData(
    val headers: List<String>,
    val rows: List<List<String>>
)

fun GSTInvoiceData.toExportData(): ExportData {
    val headers = listOf(
        "Invoice Number",
        "Invoice Date",
        "GSTIN",
        "Supplier Name",
        "Supplier Address",
        "Buyer Name",
        "Buyer GSTIN",
        "Buyer Address",
        "Item Description",
        "HSN Code",
        "Quantity",
        "Unit",
        "Rate",
        "Amount",
        "Tax Rate",
        "CGST",
        "SGST",
        "IGST",
        "Total Amount"
    )
    
    val rows = mutableListOf<List<String>>()
    
    if (items.isEmpty()) {
        rows.add(listOf(
            invoiceNumber,
            invoiceDate,
            gstin,
            supplierName,
            supplierAddress,
            buyerName,
            buyerGSTIN,
            buyerAddress,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            cgst,
            sgst,
            igst,
            totalAmount
        ))
    } else {
        items.forEachIndexed { index, item ->
            rows.add(listOf(
                if (index == 0) invoiceNumber else "",
                if (index == 0) invoiceDate else "",
                if (index == 0) gstin else "",
                if (index == 0) supplierName else "",
                if (index == 0) supplierAddress else "",
                if (index == 0) buyerName else "",
                if (index == 0) buyerGSTIN else "",
                if (index == 0) buyerAddress else "",
                item.description,
                item.hsnCode,
                item.quantity,
                item.unit,
                item.rate,
                item.amount,
                item.taxRate,
                if (index == 0) cgst else "",
                if (index == 0) sgst else "",
                if (index == 0) igst else "",
                if (index == items.size - 1) totalAmount else ""
            ))
        }
    }
    
    return ExportData(headers, rows)
}
