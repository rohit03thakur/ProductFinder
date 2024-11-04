package com.productfinder.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.productfinder.data.network.ResponseWrapper
import java.io.Reader

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val noInternetMsg = "No Internet Connection."
    val badRequestMsg = "Something went wrong, Please try after some time..."
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun isInternetAvailable(): Boolean {
        return hasInternetConnection()
    }

    fun getErrorMessage(): String {
        return "No Data Available"
    }

    fun getErrorMessage(errorBody: Reader?): String? {
        val type = object : TypeToken<ResponseWrapper<String>>() {}.type
        val gson = Gson()
        val errorResponse: ResponseWrapper<String>? = gson.fromJson(errorBody, type)
        return errorResponse?.message
    }
}