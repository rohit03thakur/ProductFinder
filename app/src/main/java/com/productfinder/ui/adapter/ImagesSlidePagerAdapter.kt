package com.productfinder.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.productfinder.ui.fragment.ImageSliderFragment
import com.productfinder.utils.Constants.Companion.INTRO_STRING_OBJECT

class ImagesSlidePagerAdapter(
    fragment: Fragment, private val imagesUrl: List<String>?
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = imagesUrl?.size ?: 0

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageSliderFragment()

        fragment.arguments = Bundle().apply {
            putString(INTRO_STRING_OBJECT, imagesUrl?.get(position))
        }
        return fragment
    }
}