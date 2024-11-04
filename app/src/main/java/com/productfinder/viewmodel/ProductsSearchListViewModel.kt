package com.productfinder.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.productfinder.data.network.Resource
import com.productfinder.data.repo.Repository
import com.productfinder.models.ProductListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.http.HTTP_BAD_REQUEST
import javax.inject.Inject

@HiltViewModel
class ProductsSearchListViewModel @Inject constructor(
    private val repository: Repository, application: Application
) : BaseViewModel(application) {


    val productResult = MutableLiveData<Resource<ProductListResponse>>()


    fun searchProduct(productName: String) {
        viewModelScope.launch {
            productResult.postValue(Resource.Loading())
            if (isInternetAvailable()) {
                try {
                    val apiResult = repository.remote.searchProduct(
                        productName
                    )
                    if (apiResult.isSuccessful) {
                        val body = apiResult.body()
                        if (body != null && body.products.isNotEmpty()) {
                            productResult.postValue(Resource.Success(body))
                        } else {
                            productResult.postValue(Resource.Error(getErrorMessage()))
                        }
                    } else if (apiResult.code() == HTTP_BAD_REQUEST) {
                        productResult.postValue(Resource.Error(badRequestMsg))
                    } else {
                        val errMsg = getErrorMessage(apiResult.errorBody()?.charStream())
                        productResult.postValue(Resource.Error(errMsg.toString()))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    productResult.postValue(Resource.Error(getErrorMessage()))
                }
            } else {
                productResult.postValue(Resource.Error(noInternetMsg))
            }
        }
    }


}

