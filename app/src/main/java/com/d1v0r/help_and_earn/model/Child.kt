package com.d1v0r.help_and_earn.model

data class Child(
    var id: String = "",
    var username: String = "",
    var fullName: String = "",
    var role: String = "Child",
    var passcode: String = "",
    var parentId: String = "",
    var balance: Int = 0,
    var imagePath: String = ""
)