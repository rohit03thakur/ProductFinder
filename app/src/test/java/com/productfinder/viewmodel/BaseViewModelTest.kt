package com.productfinder.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class BaseViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: BaseViewModel

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(application.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)
        viewModel = BaseViewModel(application)
    }

    @Test
    fun `test hasInternetConnection returns true when connected to internet`() {
        // Arrange
        val networkCapabilities = Mockito.mock(NetworkCapabilities::class.java)
        Mockito.`when`(connectivityManager.activeNetwork)
            .thenReturn(Mockito.mock(Network::class.java))
        Mockito.`when`(connectivityManager.getNetworkCapabilities(Mockito.any(Network::class.java)))
            .thenReturn(networkCapabilities)
        Mockito.`when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            .thenReturn(true)


        // Act
        val hasInternetConnection = viewModel.isInternetAvailable()

        // Assert
        Assert.assertTrue(hasInternetConnection)
    }

    @Test
    fun `test hasInternetConnection returns false when not connected to any network`() {
        // Arrange
        Mockito.`when`(connectivityManager.activeNetwork).thenReturn(null)

        // Act
        val hasInternetConnection = viewModel.isInternetAvailable()

        // Assert
        Assertions.assertFalse(hasInternetConnection)
    }
}