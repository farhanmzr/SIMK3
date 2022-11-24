package com.aksantara.simk3.view.main.home.dialogguestlogin

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogGuestLoginBinding
import com.aksantara.simk3.view.login.LoginActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogGuestLoginFragment : DialogFragment() {

    private var mView: View? = null
    private var _binding: FragmentDialogGuestLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            //initiate the binding here and pass the root to the dialog view
            _binding = FragmentDialogGuestLoginBinding.inflate(layoutInflater).apply {

                btnLogin.setOnClickListener {
                    dialog?.dismiss()
                    val intent =
                        Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            AlertDialog.Builder(this).apply {
                setView(binding.root)
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mView = null
    }

}