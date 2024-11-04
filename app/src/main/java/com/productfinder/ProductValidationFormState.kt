package com.productfinder

data class ProductValidationFormState(
    val searchError: Int = -1,
    val isDataValid: Boolean = false
)