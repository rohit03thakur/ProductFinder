package com.productfinder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.productfinder.data.network.Resource
import com.productfinder.databinding.FragmentProductDetailsBinding
import com.productfinder.ui.adapter.ImagesSlidePagerAdapter
import com.productfinder.viewmodel.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private var _binding: FragmentProductDetailsBinding? = null
    private val viewModel: ProductDetailsViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productId = args.productId

        observerProductDetailsResults()
        viewModel.getProductDetails(productId)

    }

    private fun observerProductDetailsResults() {
        viewModel.productDetailsResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.mainLayout.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    val productDetails = it.data
                    binding.product = productDetails
                    bindImagesSlider(productDetails?.images)

                }
                is Resource.Error -> {
                    binding.mainLayout.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoDataAvailable.text = it.message
                    binding.tvNoDataAvailable.visibility = View.VISIBLE
                }
                else -> {

                }
            }


        }
    }

    private fun bindImagesSlider(images: List<String>?) {
        val pager = binding.imgLayout.introPager
        val pagerAdapter = ImagesSlidePagerAdapter(this, images)
        pager.adapter = pagerAdapter
        TabLayoutMediator(
            binding.imgLayout.intoTabLayout, binding.imgLayout.introPager
        ) { _, _ -> }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}