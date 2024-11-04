package com.productfinder.data

import com.productfinder.data.database.ProductsDao
import com.productfinder.models.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: ProductsDao
) {

    fun getRecentProducts(): Flow<List<Product>> {
        return recipesDao.getRecentProducts()
    }


    suspend fun insertRecentProducts(ProductsEntity: Product) {
        recipesDao.insertRecentProducts(ProductsEntity)
    }

    suspend fun getRecentProductDetails(id: String): Product? {
        return recipesDao.getRecentProductDetails(id)
    }


}