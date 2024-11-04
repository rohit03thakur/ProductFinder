package com.productfinder.data.network

data class ResponseWrapper<T>(
    val success: Int,
    val message: T
)