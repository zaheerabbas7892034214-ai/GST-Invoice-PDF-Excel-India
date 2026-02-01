package com.gstinvoice.pdftoexcel.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY conversionDate DESC LIMIT 20")
    fun getAllRecentFiles(): Flow<List<RecentFileEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(file: RecentFileEntity)
    
    @Query("DELETE FROM recent_files WHERE id = :fileId")
    suspend fun deleteRecentFile(fileId: Long)
    
    @Query("DELETE FROM recent_files")
    suspend fun deleteAllRecentFiles()
}
