package com.d1v0r.help_and_earn.model

data class Parent(
    var id: String = "",
    var username: String = "",
    var fullName: String = "",
    var role: String = "Parent",
    var email: String = "",
    var password: String = "",
    var imagePath: String = ""
) {
    constructor() : this("", "", "", "Parent", "", "")
}