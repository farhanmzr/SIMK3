package com.aksantara.simk3.utils

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun getCurrentDateTime(): String {
        val localeID = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", localeID)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentTime(): String {
        val localeID = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("HH:mm", localeID)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDate(): String {
        val localeID = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", localeID)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDateFormat(): String {
        val localeID = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", localeID)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDateDay(): String {
        val localeID = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("dd", localeID)
        val date = Date()
        return dateFormat.format(date)
    }
}