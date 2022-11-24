package com.aksantara.simk3.view.main.profile.editakun

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentEditAkunBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.main.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class EditAkunFragment : Fragment() {

    private lateinit var binding : FragmentEditAkunBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users

    private var uriImagePath: Uri? = null
    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        getUserData()

        val getImage =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uriImage ->
                if (uriImage?.path != null) {
                    uriImagePath = uriImage
                    binding.imgProfile.setImageURI(uriImagePath)
                }
            }

        binding.apply {
            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnChangePicture.setOnClickListener {
                getImage.launch(arrayOf("image/*"))
            }
            btnSimpanData.setOnClickListener {
                if (validateInput()){
                    updateProfileData()
                }
            }
        }

    }

    private fun validateInput(): Boolean {
        val name = binding.etNama.text.toString().trim()

        return when {

            name.isEmpty() -> {
                showSnakbar("Nama Tidak Boleh Kosong.")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun updateProfileData() {
        progressDialog.show()
        binding.apply {
            usersDataProfile.nama = etNama.text.toString()
            usersDataProfile.email = etEmail.text.toString()
            usersDataProfile.departemen = etDepartemen.text.toString()
        }

        if (uriImagePath != null) {
            uploadProfilePicture()
        } else {
            uploadData()
        }
    }

    private fun uploadData() {
        mainViewModel.editDataUser(usersDataProfile).observe(viewLifecycleOwner) { newUserData ->
            if (newUserData != null) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Profile berhasil diperbaharui.",
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
                    "Profil gagal diperbaharui.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadProfilePicture() {
        mainViewModel.uploadImages(
            uriImagePath!!,
            "ImagesProfileUser",
            "${usersDataProfile.userId.toString()}${usersDataProfile.nama}",
            "profilePicture"
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                usersDataProfile.userPicture = downloadUrl.toString()
                uploadData()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Update Profile Failed",
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
                    initView(usersDataProfile)
                }
            }
    }

    private fun initView(usersDataProfile: Users) {
        binding.apply {
            imgProfile.loadImage(usersDataProfile.userPicture)
            etNama.setText(usersDataProfile.nama)
            etDepartemen.setText(usersDataProfile.departemen)
            etDepartemen.setOnClickListener {
                Toast.makeText(requireContext(), "Unit Departemen tidak dapat diubah.", Toast.LENGTH_SHORT).show()
            }
            etEmail.setText(usersDataProfile.email)
            etEmail.setOnClickListener {
                Toast.makeText(requireContext(), "Email tidak dapat diubah.", Toast.LENGTH_SHORT).show()
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