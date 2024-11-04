package com.productfinder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.productfinder.data.network.Resource
import com.productfinder.databinding.FragmentSearchResultBinding
import com.productfinder.ui.adapter.ProductListAdapter
import com.productfinder.viewmodel.ProductsSearchListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsSearchResultFragment : Fragment() {

    private val viewModel: ProductsSearchListViewModel by viewModels()
    private var _binding: FragmentSearchResultBinding? = null
    private val args by navArgs<ProductsSearchResultFragmentArgs>()
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerSearchResults()
        val query = args.searchKeyword
        if (viewModel.productResult.value == null) {
            viewModel.searchProduct(query)
        }

    }


    private fun observerSearchResults() {
        viewModel.productResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvSearchResult.adapter =
                        ProductListAdapter(it.data?.products!!) { product ->
                            val action =
                                ProductsSearchResultFragmentDirections.actionSearchResultFragmentToProductDetailsFragment(
                                    product.id.toString()
                                )

                            findNavController().navigate(action)
                        }

                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoDataAvailable.text = it.message
                    binding.tvNoDataAvailable.visibility = View.VISIBLE
                }
                else -> {

                }
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}