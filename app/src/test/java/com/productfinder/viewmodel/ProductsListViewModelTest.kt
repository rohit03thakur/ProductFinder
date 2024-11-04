package com.productfinder.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.productfinder.ProductValidationFormState
import com.productfinder.R
import com.productfinder.data.LocalDataSource
import com.productfinder.data.RemoteDataSource
import com.productfinder.data.network.Resource
import com.productfinder.data.repo.Repository
import com.productfinder.getOrAwaitValue
import com.productfinder.models.Product
import com.productfinder.utils.Constants
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

@ExperimentalCoroutinesApi
class ProductsListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Mock
    private lateinit var mockRemoteDataSource: RemoteDataSource

    @Mock
    private lateinit var mockLocalDataSource: LocalDataSource

    @InjectMocks
    private lateinit var repository: Repository

    lateinit var viewModel: ProductsListViewModel

    @Mock
    private lateinit var application: Application

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ProductsListViewModel(repository, application)

        Dispatchers.setMain(testDispatcher)
    }


    @Test
    fun `test checkValidation sets error message when search query is empty`() {
        // Arrange
        val searchQuery = ""
        val expectedFormState =
            ProductValidationFormState(searchError = R.string.search_validation_error_msg)

        // Act
        viewModel.checkValidation(searchQuery)

        // Assert
        val actualFormState = viewModel.searchFormState.getOrAwaitValue()
        assertEquals(expectedFormState, actualFormState)
    }

    @Test
    fun `test checkValidation sets isDataValid true when search query is valid`() {
        // Arrange
        val searchQuery = "iPhone"
        val expectedFormState = ProductValidationFormState(isDataValid = true)

        // Act
        viewModel.checkValidation(searchQuery)

        // Assert
        val actualFormState = viewModel.searchFormState.getOrAwaitValue()
        assertEquals(expectedFormState, actualFormState)
    }

    @Test
    fun `test getRecentProducts with non-empty list`() = runBlocking {
        // Arrange
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
        Mockito.`when`(repository.local.getRecentProducts()).thenReturn(flowOf(productList))


        // Act
        viewModel.getRecentProducts()
        testScheduler.advanceUntilIdle()

        // Assert

        val expectedValue = Resource.Success(productList)
        val actualValue: Resource<List<Product>> = viewModel.recentProductsResult.value!!

        MatcherAssert.assertThat(actualValue, resourceEq(expectedValue))
    }

    @Test
    fun `test getRecentProducts with empty list`() = runBlocking {
        // Arrange
        val productList = emptyList<Product>()
        Mockito.`when`(repository.local.getRecentProducts()).thenReturn(flowOf(productList))

        // Act
        viewModel.getRecentProducts()
        testScheduler.advanceUntilIdle()

        // Assert
        val expectedValue = Constants.No_RECENT_PRODUCT_MSG
        val actualValue = viewModel.recentProductsResult.value?.message!!

        // Clean up the observer
        assertEquals(actualValue, expectedValue)
    }
}
