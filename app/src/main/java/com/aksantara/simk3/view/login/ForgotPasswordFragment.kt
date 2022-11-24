package com.aksantara.simk3.view.login

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentForgotPasswordBinding
import com.aksantara.simk3.databinding.FragmentLoginBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding : FragmentForgotPasswordBinding

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var users: Users

    private lateinit var progressDialog : Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        binding.apply {
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnForgotPassword.setOnClickListener {
                if (validateEmail()){
                    forgotPassword()
                }
            }
        }
    }

    private fun forgotPassword() {
        progressDialog.show()

        val email = binding.etEmail.text.toString().trim()

        loginViewModel.forgotPasswordUser(email).observe(viewLifecycleOwner) { usersData ->
            if (usersData != null && usersData.errorMessage == null) {
                if (usersData.isResetPassword == true){
                    progressDialog.dismiss()
                    Toast.makeText(requireActivity(), "Link reset password telah dikirimkan ke email. Silahkan untuk membuka email anda.", Toast.LENGTH_LONG).show()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), "Gagal mereset password", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateEmail(): Boolean {

        val email = binding.etEmail.text.toString().trim()

        return when {
            !email.contains("@") && !email.contains(".") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            !email.contains("@") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            !email.contains(".") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            email.contains(" ") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            email.length < 6 -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            else -> {
                true
            }
        }

    }

}