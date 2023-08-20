package com.d1v0r.help_and_earn.model

data class Wishlist(
    var id: String = "",
    var childId: String = "",
    var itemName: String = "",
    var price: Int = 0,
    var parentId: String = "",
    var bought: Boolean = false
)