package com.productfinder.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.productfinder.data.network.Resource
import com.productfinder.data.repo.Repository
import com.productfinder.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.http.HTTP_BAD_REQUEST
import javax.inject.Inject

@HiltViewModel
open class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository, application: Application
) : BaseViewModel(application) {
    val productDetailsResult = MutableLiveData<Resource<Product>>()


    fun getProductDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productDetailsResult.postValue(Resource.Loading())
            if (isInternetAvailable()) {
                try {
                    val apiResult = repository.remote.getProductDetails(
                        id
                    )
                    if (apiResult.isSuccessful) {
                        val body = apiResult.body()
                        if (body != null) {
                            insertProducts(body) // save locally
                            productDetailsResult.postValue(Resource.Success(body))
                        } else {
                            productDetailsResult.postValue(Resource.Error(getErrorMessage()))
                        }
                    } else if (apiResult.code() == HTTP_BAD_REQUEST) {
                        productDetailsResult.postValue(Resource.Error(badRequestMsg))
                    }
                    else {
                        val errMsg = getErrorMessage(apiResult.errorBody()?.charStream())
                        productDetailsResult.postValue(Resource.Error(errMsg.toString()))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    productDetailsResult.postValue(Resource.Error(getErrorMessage()))
                }
            } else {
                if (!isProductOfflineAvailable(id))
                    productDetailsResult.postValue(Resource.Error("No Internet Connection."))
            }

        }
    }

    private suspend fun isProductOfflineAvailable(id: String): Boolean {
        val product = repository.local.getRecentProductDetails(id)
        val isProductAvailable = product != null

        if (isProductAvailable) {
            productDetailsResult.postValue(Resource.Success(product!!))
        }
        return isProductAvailable
    }

    private fun insertProducts(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecentProducts(product)
    }
}

