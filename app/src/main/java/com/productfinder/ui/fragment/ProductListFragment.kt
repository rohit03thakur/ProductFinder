package com.productfinder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.productfinder.data.network.Resource
import com.productfinder.databinding.FragmentProductListBinding
import com.productfinder.models.Product
import com.productfinder.ui.adapter.ProductListAdapter
import com.productfinder.viewmodel.ProductsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val viewModel: ProductsListViewModel by viewModels()

    private val binding get() = _binding!!
    private lateinit var searchQuery: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchListener()
        observeRecentViewsOrder()
        observeValidation()
        viewModel.getRecentProducts()
    }

    private fun setSearchListener() {
        binding.layoutSearch.etSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Perform search when the user submits the query
                searchQuery = query
                performSearch()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

    }

    private fun performSearch() {
        viewModel.checkValidation(searchQuery)
    }

    private fun observeRecentViewsOrder() {
        viewModel.recentProductsResult.observe(this.viewLifecycleOwner) {
            if (it is Resource.Success) {
                val recentProducts = it.data
                bindAdapter(recentProducts!!)
                binding.tvNoProductsAvailable.visibility = View.GONE
            } else if (it is Resource.Error) {
                binding.tvNoProductsAvailable.text = it.message
                binding.tvNoProductsAvailable.visibility = View.VISIBLE
            }

        }
    }

    private fun bindAdapter(productList: List<Product>) {
        binding.rvProducts.adapter = ProductListAdapter(productList) { product ->
            val action =
                ProductListFragmentDirections.actionProductListFragmentToProductDetailsFragment(
                    product.id.toString()
                )

            findNavController().navigate(action)
        }
    }

    private fun observeValidation() {
        viewModel.searchFormState.observe(this.viewLifecycleOwner) {
            if (it.searchError != -1) {
                Toast.makeText(this.requireContext(), getString(it.searchError), Toast.LENGTH_SHORT)
                    .show()
            }
            if (it.isDataValid) {
                val action =
                    ProductListFragmentDirections.actionProductListFragmentToSearchResultFragment(
                        searchQuery
                    )
                findNavController().navigate(action)
                resetSearchView()
            }
        }
    }

    private fun resetSearchView() {
        binding.layoutSearch.etSearchView.clearFocus()
        binding.layoutSearch.etSearchView.setQuery("", false)
        binding.layoutSearch.etSearchView.isIconified = true
        viewModel.resetValidation()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}