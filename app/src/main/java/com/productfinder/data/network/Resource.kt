package com.productfinder.data.network

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val messageId: Int = -1  // for sending string id as a message
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}


