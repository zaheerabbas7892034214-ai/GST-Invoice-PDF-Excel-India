package com.gstinvoice.pdfexcel.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.gstinvoice.pdfexcel.data.entity.InvoiceEntity
import com.gstinvoice.pdfexcel.data.entity.LineItemEntity

data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val lineItems: List<LineItemEntity>
)
