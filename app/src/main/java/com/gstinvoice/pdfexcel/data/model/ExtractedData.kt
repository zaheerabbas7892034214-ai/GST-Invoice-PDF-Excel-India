package com.gstinvoice.pdfexcel.data.model

data class ExtractedInvoiceData(
    val invoiceNumber: String? = null,
    val invoiceDate: String? = null,
    val supplierName: String? = null,
    val supplierGstin: String? = null,
    val buyerGstin: String? = null,
    val lineItems: List<ExtractedLineItem> = emptyList()
)

data class ExtractedLineItem(
    val description: String? = null,
    val hsnSac: String? = null,
    val quantity: Double? = null,
    val rate: Double? = null,
    val taxableValue: Double? = null,
    val cgstRate: Double? = null,
    val cgstAmount: Double? = null,
    val sgstRate: Double? = null,
    val sgstAmount: Double? = null,
    val igstRate: Double? = null,
    val igstAmount: Double? = null,
    val totalAmount: Double? = null
)

sealed class ExtractionResult {
    data class Success(val data: ExtractedInvoiceData) : ExtractionResult()
    data class Error(val message: String) : ExtractionResult()
    object Loading : ExtractionResult()
}
