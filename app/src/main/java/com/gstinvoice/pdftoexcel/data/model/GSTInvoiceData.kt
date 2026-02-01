package com.gstinvoice.pdftoexcel.data.model

data class GSTInvoiceData(
    val invoiceNumber: String = "",
    val invoiceDate: String = "",
    val gstin: String = "",
    val supplierName: String = "",
    val supplierAddress: String = "",
    val buyerName: String = "",
    val buyerGSTIN: String = "",
    val buyerAddress: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val taxableValue: String = "",
    val cgst: String = "",
    val sgst: String = "",
    val igst: String = "",
    val totalAmount: String = ""
)

data class InvoiceItem(
    val description: String = "",
    val hsnCode: String = "",
    val quantity: String = "",
    val unit: String = "",
    val rate: String = "",
    val amount: String = "",
    val taxRate: String = ""
)
