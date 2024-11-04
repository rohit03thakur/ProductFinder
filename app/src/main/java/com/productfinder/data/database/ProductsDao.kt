package com.productfinder.data.database

import androidx.room.*
import com.productfinder.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentProducts(product: Product)


    @Query("SELECT * FROM products_table ORDER BY id ASC")
    fun getRecentProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products_table WHERE id = :id")
    fun getRecentProductDetails(id: String): Product?

    @Delete
    suspend fun deleteRecentProduct(productEntity: Product)

    @Query("DELETE FROM products_table")
    suspend fun deleteAllRecentProducts()

}