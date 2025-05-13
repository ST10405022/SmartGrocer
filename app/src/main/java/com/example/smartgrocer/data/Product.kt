package com.example.smartgrocer.data

data class Product(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var inStock: Boolean = false,
)