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
import com.aksantara.simk3.utils.AppConstants.BERLEBIH_MERAH
import com.aksantara.simk3.utils.AppConstants.KURANG_MERAH
import com.aksantara.simk3.utils.AppConstants.SESUAI_HIJAU
import com.aksantara.simk3.utils.AppConstants.TEKANAN_TABUNG
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogTekananTabung : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private var status = false

    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null

    private var optionTekananTabung: OnOptionTekananTabung? = null

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
            rbChoice3.visibility = View.VISIBLE
            icBack.setOnClickListener { dismiss() }
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_tekanan_tabung)
            tvTitleInspeksi.text = TEKANAN_TABUNG
            rbChoice1.text = SESUAI_HIJAU
            rbChoice2.text = KURANG_MERAH
            rbChoice3.text = BERLEBIH_MERAH
            linearImage.visibility = View.GONE
            linearAddPhoto.visibility = View.VISIBLE
        }

        binding.linearAddPhoto.setOnClickListener {
            if (binding.rbChoice2.isChecked || binding.rbChoice3.isChecked){
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
                }
                optionTekananTabung?.onOptionChosen(kategori, imageUri)
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
            this.optionTekananTabung = inspeksi.optionTekananTabung
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionTekananTabung = inspeksi.optionTekananTabung
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionTekananTabung = null
    }

    interface OnOptionTekananTabung {
        fun onOptionChosen(kategori: String?, imageUri: Uri?)
    }
}