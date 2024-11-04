package com.productfinder.data.repo

import com.productfinder.models.Product
import com.productfinder.models.ProductListResponse
import retrofit2.Response

interface ProductRepo {
    suspend fun searchProduct(productName: String): Response<ProductListResponse>
    suspend fun getProductDetails(id: String): Response<Product>
}