package com.aksantara.simk3.view.login.register

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentRegisterBinding
import com.aksantara.simk3.databinding.ItemDialogVerifikasiEmailBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.URL_AVATAR
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.login.LoginViewModel
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding

    private lateinit var mAuth: FirebaseAuth
    private val loginViewModel: LoginViewModel by activityViewModels()

    private var departemenId: String? = null

    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        mAuth = Firebase.auth

        binding.apply {

            etDepartemen.setOnClickListener {
                popupSheetDepartemen()
            }
            tvLogin.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnRegister.setOnClickListener {
                if (validateName() && validateEmail() && validatePassword()) {
                    registerUser()
                }
            }
        }
    }

    internal var optionDialogDepartemen: PopupDepartemenFragment.OnOptionDialogListener =
        object : PopupDepartemenFragment.OnOptionDialogListener {
            override fun onOptionChosen(category: String?, id: String?) {
                val unit = category
                binding.etDepartemen.setText(unit)
                departemenId = id
            }
        }

    private fun popupSheetDepartemen() {
        val mPopupDepartemen = PopupDepartemenFragment()
        val mFragmentManager = childFragmentManager
        mPopupDepartemen.show(
            mFragmentManager,
            mPopupDepartemen::class.java.simpleName
        )
    }

    private fun registerUser() {
        progressDialog.show()

        val departemen = binding.etDepartemen.text.toString().trim()
        val nama = binding.etNama.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val role = departemen != "P2K3"

        /*create a user*/
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    val userId = user!!.uid

                    val usersData = Users(departemen = departemen,
                        departemenId = departemenId,
                        nama = nama,
                        email = email,
                        registeredAt = DateHelper.getCurrentDate(),
                        userVerification = role,
                        userId = userId,
                        userPicture = URL_AVATAR)
                    createNewUser(usersData)
                } else {
                    Toast.makeText(requireContext(), "Authentication failed: + ${task.exception?.message.toString()}",
                        Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }
    }

    private fun createNewUser(usersData: Users) {
        Log.d("createdNewUser", usersData.nama.toString())
        loginViewModel.createdNewUser(usersData).observe(viewLifecycleOwner) { newUser ->
            if (newUser.isCreated == true) {
                progressDialog.dismiss()
                Log.d(
                    ContentValues.TAG,
                    "Hello ${usersData.nama}, Your Account Successfully Created!"
                )
                val user = mAuth.currentUser
//                Preference
//                val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//                val editor = preferences.edit()
//                editor.putInt("totalShop", usersData.totalShop!!)
//                editor.apply()
                sendEmailVerification(user)
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val builder = AlertDialog.Builder(requireContext())
                    val binding: ItemDialogVerifikasiEmailBinding =
                        ItemDialogVerifikasiEmailBinding.inflate(LayoutInflater.from(context))
                    builder.setView(binding.root)
                    val dialog = builder.create()
                    dialog.show()
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(false)
                    binding.btnVerif.setOnClickListener {
                        dialog.dismiss()
                        mAuth.signOut()
                        val intent =
                            Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    Log.d(ContentValues.TAG, "Email sent.")
                }
            }
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        return when {
            password.length < 6 -> {
                showSnakbar("Password Tidak Boleh Kurang dari 6 Huruf.")
                false
            }
            password != confirmPassword -> {
                showSnakbar("Kata Sandi Tidak Sama.")
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

    private fun validateName(): Boolean {
        val name = binding.etNama.text.toString().trim()
        val departemen = binding.etDepartemen.toString().trim()

        return when {
            departemen.isEmpty() -> {
                showSnakbar("Silahkan Pilih Unit Terlebih Dahulu.")
                false
            }

            name.length < 3 -> {
                showSnakbar("Nama Tidak Boleh Kurang dari 3 huruf.")
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