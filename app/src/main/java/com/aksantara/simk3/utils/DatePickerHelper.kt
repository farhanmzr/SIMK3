package com.aksantara.simk3.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import com.aksantara.simk3.R
import java.util.*

class DatePickerHelper(context: Context) {
    private var dialog: DatePickerDialog
    private var callback: Callback? = null
    private val listener =
        DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
            callback?.onDateSelected(datePicker ,dayOfMonth, monthOfYear, year)
        }
    init {
        val style = R.style.DialogTheme
        val cal = Calendar.getInstance()
        dialog = DatePickerDialog(context, style, listener,
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }
    fun showDialog(dayofMonth: Int, month: Int, year: Int, callback: Callback?) {
        this.callback = callback
        dialog.datePicker.init(year, month, dayofMonth, null)
        dialog.show()
    }
    fun setMinDate(minDate: Long) {
        dialog.datePicker.minDate = minDate
    }
    fun setMaxDate(maxDate: Long) {
        dialog.datePicker.maxDate = maxDate
    }
    interface Callback {
        fun onDateSelected(datePicker: View, dayofMonth: Int, month: Int, year: Int)
    }
}

fun getMonthString(month: Int): String {
    val result = when (month) {
        0 -> "Januari"
        1 -> "Februari"
        2 -> "Maret"
        3 -> "April"
        4 -> "Mei"
        5 -> "Juni"
        6 -> "Juli"
        7 -> "Agustus"
        8 -> "September"
        9 -> "Oktober"
        10 -> "November"
        11 -> "Desember"
        else -> {
            "Mei"
        }
    }
    return result
}