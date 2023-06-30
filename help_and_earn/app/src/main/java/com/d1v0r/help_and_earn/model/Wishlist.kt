package com.d1v0r.help_and_earn.model

data class Wishlist(
    val id: String,
    val userId: String,
    val itemName: String,
    val description: String,
    val imageUrl: String,
    val price: Double
)