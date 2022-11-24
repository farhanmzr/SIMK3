package com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogSheetInspeksiBinding
import com.aksantara.simk3.utils.AppConstants.DALAMLEMARI
import com.aksantara.simk3.utils.AppConstants.DALAMLEMARI_TIDAKTERKUNCI
import com.aksantara.simk3.utils.AppConstants.DILANTAI
import com.aksantara.simk3.utils.AppConstants.MENGGANTUNG_DINDING
import com.aksantara.simk3.utils.AppConstants.PEMASANGAN_APAR
import com.aksantara.simk3.utils.AppConstants.TIDAK_SESUAI_STANDAR
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogPemasanganAparFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null

    private var optionPemasanganApar: OnOptionPemasanganApar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogSheetInspeksiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            icBack.setOnClickListener { dismiss() }
            rbChoice3.visibility = View.VISIBLE
            rbChoice4.visibility = View.VISIBLE
            rbChoice5.visibility = View.VISIBLE
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_pemasangan_apar)
            tvTitleInspeksi.text = PEMASANGAN_APAR
            rbChoice1.text = MENGGANTUNG_DINDING
            rbChoice2.text = DALAMLEMARI_TIDAKTERKUNCI
            rbChoice3.text = DALAMLEMARI
            rbChoice4.text = DILANTAI
            rbChoice5.text = TIDAK_SESUAI_STANDAR
            linearImage.visibility = View.GONE
            linearAddPhoto.visibility = View.VISIBLE
        }

        binding.linearAddPhoto.setOnClickListener {
            if (binding.rbChoice5.isChecked){
                val permissionGranted = requestCameraPermission()
                if (permissionGranted) {
                    // Open the camera interface
                    openCameraInterface()
                }
            }
        }

        binding.btnLanjutkan.setOnClickListener {
            val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
            if (checkedRadioButtonId != 1) {
                when (checkedRadioButtonId) {
                    R.id.rb_choice_1 -> kategori = binding.rbChoice1.text.toString().trim()
                    R.id.rb_choice_2 -> kategori = binding.rbChoice2.text.toString().trim()
                    R.id.rb_choice_3 -> kategori = binding.rbChoice3.text.toString().trim()
                    R.id.rb_choice_4 -> kategori = binding.rbChoice4.text.toString().trim()
                    R.id.rb_choice_5 -> kategori = binding.rbChoice5.text.toString().trim()
                }
                optionPemasanganApar?.onOptionChosen(kategori, imageUri)
                dialog?.dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                openCameraInterface()
            }
            else{
                // Permission was denied
                showAlert("Camera permission was denied. Unable to take a picture.");
            }
        }
    }

    private fun openCameraInterface() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description")
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        // Create camera intent
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        // Launch intent
        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Callback from camera intent
        if (resultCode == Activity.RESULT_OK){
            // Set image captured to image view
            binding.linearImage.visibility = View.VISIBLE
            binding.linearAddPhoto.visibility = View.GONE
            binding.imgPhotos.setImageURI(imageUri)
            Log.e("imageuri", imageUri.toString())
            binding.icDeleteImage.setOnClickListener {
                binding.imgPhotos.setImageURI(null)
                imageUri = null
                binding.linearImage.visibility = View.GONE
                binding.linearAddPhoto.visibility = View.VISIBLE
            }
        }
        else {
            // Failed to take picture
            showAlert("Failed to take camera picture")
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = ContextCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.CAMERA)

                // Display permission dialog
                requestPermissions(permission, CAMERA_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is InspeksiFragment) {
            val inspeksi = fragment
            this.optionPemasanganApar = inspeksi.optionPemasanganApar
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionPemasanganApar = inspeksi.optionPemasanganApar
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionPemasanganApar = null
    }

    interface OnOptionPemasanganApar {
        fun onOptionChosen(kategori: String?, imageUri: Uri?)
    }
}