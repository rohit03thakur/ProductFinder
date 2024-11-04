package com.productfinder.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.productfinder.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(Constants.PRODUCT_TABLE)
data class Product(
    val brand: String,
    val category: String,
    val description: String,
    val discountPercentage: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val images: List<String>,
    val price: String,
    val rating: Float,
    val stock: Int,
    val thumbnail: String,
    val title: String
) : Parcelable {
    fun discountedPrice(): Double {
        val discountPrice = price.toDouble() * (discountPercentage) / 100
        return price.toDouble() - discountPrice
    }
}