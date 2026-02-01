package com.gstinvoice.pdfexcel.data.dao

import androidx.room.*
import com.gstinvoice.pdfexcel.data.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity)

    @Update
    suspend fun updatePurchase(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases WHERE productId = :productId")
    suspend fun getPurchaseByProductId(productId: String): PurchaseEntity?

    @Query("SELECT * FROM purchases WHERE acknowledged = 0 ORDER BY lastRetryTime ASC")
    suspend fun getUnacknowledgedPurchases(): List<PurchaseEntity>

    @Query("SELECT * FROM purchases")
    fun getAllPurchases(): Flow<List<PurchaseEntity>>

    @Delete
    suspend fun deletePurchase(purchase: PurchaseEntity)

    @Query("DELETE FROM purchases WHERE productId = :productId")
    suspend fun deletePurchaseByProductId(productId: String)
}
