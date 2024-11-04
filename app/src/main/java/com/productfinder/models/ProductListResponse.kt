package com.productfinder.models

data class ProductListResponse(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)