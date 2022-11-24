package com.aksantara.simk3.view.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentLoginBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.ERROR_WRONG_PASSWORD
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.LOGIN_ERROR_NOACCOUNT
import com.aksantara.simk3.utils.AppConstants.LOGIN_ERROR_NOVERIF
import com.aksantara.simk3.utils.AppConstants.LOGIN_ERROR_WRONG_PASSWORD
import com.aksantara.simk3.utils.AppConstants.LOGIN_P2K3_NOVERIF
import com.aksantara.simk3.utils.AppConstants.PREFS_NAME
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.login.register.RegisterFragment
import com.aksantara.simk3.view.main.MainActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var users: Users

    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        initView()
    }

    private fun initView() {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = preferences.getString("email", "none")

        if (email != "none"){
            binding.etEmail.setText(email)
        }

        binding.apply {
            tvRegister.setOnClickListener {
                gotoRegister()
            }
            btnLogin.setOnClickListener {
                if(validateEmail() && validatePassword()){
                    loginUser()
                }
            }
            btnGuest.setOnClickListener {
                loginAsGuest()
            }
            tvForgotPassword.setOnClickListener {
                gotoForgotPassword()
            }
        }
    }

    private fun gotoForgotPassword() {
        val mForgotPassword = ForgotPasswordFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_login_activity,
                mForgotPassword
            )
        }
    }

    private fun loginUser() {
        progressDialog.show()

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        loginViewModel.signInWithEmail(email, password)
            .observe(viewLifecycleOwner) { userData ->
                if (userData != null && userData.errorMessage == null) {
                    // Login sukses, masuk ke Main Activity
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        progressDialog.dismiss()
                        initDataUser(userData)
                    } else {
                        progressDialog.dismiss()
                        FirebaseAuth.getInstance().signOut()
                        showSnakbar(LOGIN_ERROR_NOVERIF)
                    }
                } else if (userData.errorMessage != null) {
                    Log.e("error", userData.errorMessage.toString())
                    progressDialog.dismiss()
                    showSnakbar(userData.errorMessage.toString())
                }
            }
    }

    private fun gotoMainActivity(userData: Users) {
        if (userData.userVerification == true) {
            val intent =
                Intent(requireActivity(), MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_USER, userData)
            startActivity(intent)
            requireActivity().finish()
        } else {
            showSnakbar(LOGIN_P2K3_NOVERIF)
        }
    }

    private fun initDataUser(userData: Users) {
        loginViewModel.setUsersProfile(userData.userId.toString()).observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                users = userProfile
                //Preference
                val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val unit = userProfile.departemen
                val email = userProfile.email
                val departemenId = userProfile.departemenId
                val editor = preferences.edit()
                editor.putString("unit", unit)
                editor.putString("email", email)
                editor.putString("departemenId", departemenId)
                editor.apply()
                gotoMainActivity(userProfile)
            }
        }
    }

    private fun loginAsGuest() {
        progressDialog.show()
        loginViewModel.signInWithAnonym()
            .observe(viewLifecycleOwner) { statusLogin ->
                if (statusLogin == STATUS_SUCCESS) {
                    // Login sukses, masuk ke Main Activity
                    val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString("unit", GUEST)
                    editor.apply()
                    progressDialog.dismiss()
                    val intent =
                        Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    progressDialog.dismiss()
                    showSnakbar(statusLogin)
                }
            }
    }

//    internal var optionDialogListener: PopupDepartemenFragment.OnOptionDialogListener =
//        object : PopupDepartemenFragment.OnOptionDialogListener {
//            override fun onOptionChosen(category: String?) {
//                val unit = category
////                binding.etDepartemen.setText(unit)
//            }
//    }

    private fun gotoRegister() {
        val registerFragment = RegisterFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_login_activity,
                registerFragment
            )
        }
    }

//    private fun popupSheetDepartemen() {
//        val mPopupDepartemen = PopupDepartemenFragment()
//        val mFragmentManager = childFragmentManager
//        mPopupDepartemen.show(
//            mFragmentManager,
//            mPopupDepartemen::class.java.simpleName
//        )
//    }

//    private fun validateName(): Boolean {
//
//        val departemen = binding.etDepartemen.text.toString().trim()
//
//        return when {
//            departemen.isEmpty() -> {
//                showSnakbar("Silahkan Pilih Unit Terlebih Dahulu.")
//                false
//            }
//
//            else -> {
//                true
//            }
//        }
//    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()

        return when {
            password.isEmpty() -> {
                showSnakbar("Password Tidak Boleh Kosong")
                false
            }
            password.length < 6 -> {
                showSnakbar("Password Tidak Boleh Kurang dari 6 Huruf.")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()

        return when {
            !email.contains("@") && !email.contains(".") -> {
                showSnakbar("Email Tidak Valid.")
                false
            }
            !email.contains("@") -> {
                showSnakbar("Email Tidak Valid.")
                false
            }
            !email.contains(".") -> {
                showSnakbar("Email Tidak Valid.")
                false
            }
            email.contains(" ") -> {
                showSnakbar("Email Tidak Valid.")
                false
            }
            email.length < 6 -> {
                showSnakbar("Email Tidak Valid.")
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