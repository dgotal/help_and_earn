package com.d1v0r.help_and_earn.firebase

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseDateFormatter {
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    fun dateToString(date: Date): String {
        return simpleDateFormat.format(date)
    }

    fun stringToDate(string: String?): Date? {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return string?.let { dateFormat.parse(it) }
    }

}