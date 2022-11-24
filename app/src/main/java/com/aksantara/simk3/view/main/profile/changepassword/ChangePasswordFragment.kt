package com.aksantara.simk3.view.main.profile.changepassword

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentChangePasswordBinding
import com.aksantara.simk3.databinding.FragmentInventoryBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ChangePasswordFragment : Fragment() {

    private lateinit var binding : FragmentChangePasswordBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private var emailMitra: String? = null

    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        getUserData()

        binding.apply {
            icBack.setOnClickListener{
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnSimpanData.setOnClickListener {
                if (validateInputPassword()) {
                    updatePasswordData()
                }
            }
        }

    }

    private fun updatePasswordData() {
        progressDialog.show()
        val recentPass = binding.etRecentPassword.text.toString().trim()
        val newPass = binding.etNewPassword.text.toString().trim()

        mainViewModel.updatePasswordUser(emailMitra.toString(), recentPass, newPass).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Kata Sandi berhasil diperbarui.",
                    Toast.LENGTH_SHORT
                ).show()
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    status,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    emailMitra = usersDataProfile.email
                }
            }
    }

    private fun validateInputPassword(): Boolean {
        val recentPass = binding.etRecentPassword.text.toString().trim()
        val newPass = binding.etNewPassword.text.toString().trim()
        val confirmPass = binding.etConfirmPassword.text.toString().trim()

        return when {
            recentPass.isEmpty() -> {
                showSnakbar("Kata Sandi Lama tidak boleh kosong.")
                false
            }
            newPass.isEmpty() -> {
                showSnakbar("Kata Sandi Baru tidak boleh kosong.")
                false
            }
            confirmPass.isEmpty() -> {
                showSnakbar("Konfirmasi Kata Sandi tidak boleh kosong.")
                false
            }
            newPass.length < 6 -> {
                showSnakbar("Minimal Kata Sandi adalah 6 huruf.")
                false
            }
            newPass != confirmPass -> {
                showSnakbar("Kata Sandi baru yang dimasukkan tidak sama.")
                false
            }
            recentPass.contains(" ") || newPass.contains(" ") || confirmPass.contains(" ") -> {
                showSnakbar("Kata Sandi tidak boleh menggunakan spasi.")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun showSnakbar(text: String) {
        val snackBarView = view?.let { Snackbar.make(it, text , Snackbar.LENGTH_LONG) }
        val view = snackBarView?.view
        val params = view?.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        view.setBackgroundResource(R.color.colorRedSnackbar)
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()
    }

}