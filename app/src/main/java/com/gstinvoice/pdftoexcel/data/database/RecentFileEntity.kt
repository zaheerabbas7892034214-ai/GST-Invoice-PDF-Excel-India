package com.gstinvoice.pdftoexcel.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val conversionDate: Long,
    val rowCount: Int
)
