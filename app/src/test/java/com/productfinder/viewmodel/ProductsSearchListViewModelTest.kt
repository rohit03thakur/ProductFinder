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
import com.productfinder.models.ProductListResponse
import com.productfinder.utils.Constants
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert
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
class ProductsSearchListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRemoteDataSource: RemoteDataSource

    @Mock
    private lateinit var mockLocalDataSource: LocalDataSource

    @InjectMocks
    private lateinit var repository: Repository

    private lateinit var viewModel: ProductsSearchListViewModel

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var connectivityManager: ConnectivityManager
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(application.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)
        viewModel = ProductsSearchListViewModel(repository, application)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `searchProduct should post Resource Error when internet is not available`() = runBlocking {
        // Arrange
        val productName = "TestProduct"
        mockIsInterNetAvailableFalse()

        // Act
        viewModel.searchProduct(productName)
        testScheduler.advanceUntilIdle()

        // Assert
        val expectedValue = Constants.NO_INTERNET_AVAILABLE
        val actualValue = viewModel.productResult.value?.message

        TestCase.assertEquals(actualValue, expectedValue)
    }

    @Test
    fun `searchProduct should post Resource Error when API response is unsuccessful`() =
        runBlocking {
            // Given
            val productName = "TestProduct"
            mockIsInterNetAvailableTrue()
            val mockResponse =
                Response.error<ProductListResponse>(400, okhttp3.ResponseBody.create(null, ""))
            Mockito.`when`(repository.remote.searchProduct(productName)).thenReturn(mockResponse)

            // When
            viewModel.searchProduct(productName)
            testScheduler.advanceUntilIdle()

            // Then
            val expectedValue = Constants.BAD_REQUEST_MESSAGE
            val actualValue = viewModel.productResult.value?.message!!
            TestCase.assertEquals(actualValue, expectedValue)
        }

    @Test
    fun `test searchProduct shows message No Data Available while Products list empty successful API response`() =
        runBlocking {
            // Arrange
            val productName = "Test Product"
            val productList = emptyList<Product>()

            mockIsInterNetAvailableTrue()

            val productListResponse = ProductListResponse(10, productList, 0, 1)
            val response = Response.success(productListResponse)
            Mockito.`when`(repository.remote.searchProduct(productName)).thenReturn(response)

            // Act
            viewModel.searchProduct(productName)

            testScheduler.advanceUntilIdle()

            // Assert

            val expectedValue = Constants.NO_DATA_AVAILABLE
            val actualValue = viewModel.productResult.value?.message!!

            TestCase.assertEquals(actualValue, expectedValue)
        }

    @Test
    fun `searchProduct should post Resource Success when internet is available API call is successful`() =
        runBlocking {
            // Arrange
            val productName = "Test Product"
            val productList = listOf(
                Product(
                    "Brand",
                    "Category",
                    "Description",
                    10.0,
                    1,
                    listOf("image1.jpg", "image2.jpg"),
                    "100",
                    4.5f,
                    100,
                    "thumbnail.jpg",
                    "Test Product"
                )
            )
            mockIsInterNetAvailableTrue()

            val productListResponse = ProductListResponse(10, productList, 0, 1)
            val response = Response.success(productListResponse)
            Mockito.`when`(repository.remote.searchProduct(productName)).thenReturn(response)

            // Act
            viewModel.searchProduct(productName)

            testScheduler.advanceUntilIdle()

            // Assert

            val expectedValue = Resource.Success(productListResponse)
            val actualValue: Resource<ProductListResponse> = viewModel.productResult.value!!

            MatcherAssert.assertThat(actualValue, resourceEq(expectedValue))
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
