package com.productfinder.data.network

import com.productfinder.models.Product
import com.productfinder.models.ProductListResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @GET(Routs.GET_PRODUCTS)
    suspend fun searchProducts(
        @Query("q") productName: String
    ): Response<ProductListResponse>

    @GET(Routs.GET_PRODUCT_DETAILS)
    suspend fun productDetails(
        @Path("id") id: String
    ): Response<Product>
}
