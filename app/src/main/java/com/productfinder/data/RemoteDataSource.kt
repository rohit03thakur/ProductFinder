package com.productfinder.data

import com.productfinder.data.network.ApiService
import com.productfinder.data.repo.ProductRepo
import com.productfinder.models.Product
import com.productfinder.models.ProductListResponse
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val productApi: ApiService
) : ProductRepo {

    override suspend fun searchProduct(productName: String): Response<ProductListResponse> {
        return productApi.searchProducts(productName)
    }

    override suspend fun getProductDetails(id: String): Response<Product> {
       return  productApi.productDetails(id)
    }

}