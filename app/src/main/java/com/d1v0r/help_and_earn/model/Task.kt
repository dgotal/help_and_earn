package com.d1v0r.help_and_earn.model

import com.google.firebase.firestore.PropertyName

data class Task(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("deadline")
    @set:PropertyName("deadline")
    var deadline: String = "",

    @get:PropertyName("coinReward")
    @set:PropertyName("coinReward")
    var coinReward: Int = 0,

    @get:PropertyName("childApproved")
    @set:PropertyName("childApproved")
    var childApproved: Boolean = false,

    @get:PropertyName("parentApproved")
    @set:PropertyName("parentApproved")
    var parentApproved: Boolean = false,

    @get:PropertyName("parentId")
    @set:PropertyName("parentId")
    var parentId: String = "",

    @get:PropertyName("childId")
    @set:PropertyName("childId")
    var childId: String = "",

    @get:PropertyName("message")
    @set:PropertyName("message")
    var message: String = "",

    @get:PropertyName("declined")
    @set:PropertyName("declined")
    var declined: Boolean = false,
)