package com.aksantara.simk3.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.aksantara.simk3.R

class ProgressDialogHelper {
    companion object {
        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            return dialog
        }
        fun progressDialogInspeksi(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_progress_dialog_inspeksi, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            return dialog
        }
    }
}