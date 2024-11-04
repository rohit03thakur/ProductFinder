package com.productfinder.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.productfinder.ProductValidationFormState
import com.productfinder.R
import com.productfinder.data.network.Resource
import com.productfinder.data.repo.Repository
import com.productfinder.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val repository: Repository, application: Application
) : BaseViewModel(application) {
    private val msg = "Display product that you recently visited. Review them anytime."
    private val _searchForm = MutableLiveData<ProductValidationFormState>()
    val searchFormState: LiveData<ProductValidationFormState> = _searchForm

    private val _recentProductsResult = MutableLiveData<Resource<List<Product>>>()
    val recentProductsResult: LiveData<Resource<List<Product>>> get() = _recentProductsResult


    fun getRecentProducts() {
        viewModelScope.launch {
            repository.local.getRecentProducts().collect {
                withContext(Dispatchers.IO) {
                    if (it.isNotEmpty()) {
                        _recentProductsResult.postValue(Resource.Success(it))
                    } else {
                        _recentProductsResult.postValue(Resource.Error(msg))
                    }

                }

            }
        }
    }

    fun checkValidation(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            _searchForm.value =
                ProductValidationFormState(searchError = R.string.search_validation_error_msg)
        } else {
            _searchForm.value = ProductValidationFormState(isDataValid = true)
        }
    }

    fun resetValidation() {
        _searchForm.value = ProductValidationFormState()
    }


}

