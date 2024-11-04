package com.productfinder.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.productfinder.data.LocalDataSource
import com.productfinder.data.RemoteDataSource
import com.productfinder.data.network.Resource
import com.productfinder.data.repo.Repository
import com.productfinder.models.Product
import com.productfinder.utils.Constants
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import resourceEq
import retrofit2.Response

@ExperimentalCoroutinesApi
class ProductDetailsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRemoteDataSource: RemoteDataSource

    @Mock
    private lateinit var mockLocalDataSource: LocalDataSource

    @InjectMocks
    private lateinit var repository: Repository

    lateinit var viewModel: ProductDetailsViewModel

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var connectivityManager: ConnectivityManager
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(application.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)

        viewModel = ProductDetailsViewModel(repository, application)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `test getProductDetails with internet available and successful API response`() =
        runBlocking {
            // Given a product ID
            val productId = 12345

            // Given a mock product returned from the API
            val mockProduct = Product(
                "Brand",
                "Category",
                "Description",
                10.0,
                productId,
                listOf("image1.jpg", "image2.jpg"),
                "100",
                4.5f,
                100,
                "thumbnail.jpg",
                "Test Product"
            )
            // Given internet is available
            mockIsInterNetAvailableTrue()
            val mockApiResponse = Response.success(mockProduct)
            Mockito.`when`(repository.remote.getProductDetails(productId.toString()))
                .thenReturn(mockApiResponse)


            // When calling getProductDetails with the given product ID
            viewModel.getProductDetails(productId.toString())
            testScheduler.advanceUntilIdle()

            val expectedValue = Resource.Success(mockProduct)
            val actualValue: Resource<Product> = viewModel.productDetailsResult.value!!
            assertThat(actualValue, resourceEq(expectedValue))
        }

    @Test
    fun `test getProductDetails with internet not available and product available offline`() =
        runBlocking {
            // Given a product ID
            val productId = 12345

            // Given a mock product available offline
            val mockProduct = Product(
                "Brand",
                "Category",
                "Description",
                10.0,
                productId,
                listOf("image1.jpg", "image2.jpg"),
                "100",
                4.5f,
                100,
                "thumbnail.jpg",
                "Test Product"
            )
            Mockito.`when`(repository.local.getRecentProductDetails(productId.toString()))
                .thenReturn(mockProduct)

            // Given internet is not available
            mockIsInterNetAvailableFalse()

            // Given a mock observer to observe the result


            // When calling getProductDetails with the given product ID
            viewModel.getProductDetails(productId.toString())
            testScheduler.advanceUntilIdle()
            val expectedResource = Resource.Success(mockProduct).data?.id.toString()

            // Verify that localDataSource.getRecentProductDetails() is called
            assertEquals(productId.toString(), expectedResource)
        }

    @Test
    fun `test getProductDetails with internet not available and product not available offline`() =
        runBlocking {
            // Given a product ID
            val productId = "12345"

            mockIsInterNetAvailableFalse()

            // When calling getProductDetails with the given product ID
            viewModel.getProductDetails(productId)

            testScheduler.advanceUntilIdle()
            // Then the productDetailsResult should be updated with Resource.Error

            val expectedValue = Constants.NO_INTERNET_AVAILABLE
            val actualValue = viewModel.productDetailsResult.value?.message

            assertEquals(actualValue, expectedValue)

        }

    @Test
    fun `test getProductDetail should post Resource Error when API response is unsuccessful`() =
        runBlocking {
            // Given
            val productId = "12345"
            mockIsInterNetAvailableTrue()
            val mockResponse =
                Response.error<Product>(400, okhttp3.ResponseBody.create(null, ""))
            Mockito.`when`(repository.remote.getProductDetails(productId)).thenReturn(mockResponse)

            // When
            viewModel.getProductDetails(productId)
            testScheduler.advanceUntilIdle()

            // Then
            val expectedValue = Constants.BAD_REQUEST_MESSAGE
            val actualValue = viewModel.productDetailsResult.value?.message!!
            assertEquals(actualValue, expectedValue)
        }

    private fun mockIsInterNetAvailableTrue() {
        val networkCapabilities = Mockito.mock(NetworkCapabilities::class.java)
        Mockito.`when`(connectivityManager.activeNetwork)
            .thenReturn(Mockito.mock(Network::class.java))
        Mockito.`when`(connectivityManager.getNetworkCapabilities(Mockito.any(Network::class.java)))
            .thenReturn(networkCapabilities)
        Mockito.`when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            .thenReturn(true)
    }

    private fun mockIsInterNetAvailableFalse() {
        Mockito.`when`(connectivityManager.activeNetwork).thenReturn(null)
        Mockito.`when`(application.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(Mockito.mock(ConnectivityManager::class.java))
    }
}
