package com.productfinder.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.productfinder.databinding.SliderImageBinding
import com.productfinder.utils.Constants.Companion.INTRO_STRING_OBJECT
import com.productfinder.utils.ImageLoaderUtils


class ImageSliderFragment : Fragment() {
    private var _binding: SliderImageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SliderImageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey(INTRO_STRING_OBJECT) }?.apply {
            val url = getString(INTRO_STRING_OBJECT)
            ImageLoaderUtils.loadImage(url, binding.ivSliderImage)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}