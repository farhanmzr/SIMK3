package com.aksantara.simk3.view.main.inventory.detailinventory.editapar

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentEditAparBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.utils.*
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.inventory.tambahapar.dialogmediaapar.DialogMediaAparFragment
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


@ExperimentalCoroutinesApi
class EditAparFragment : Fragment() {

    //aparId dan pictureLawas dihapus diganti baru ketika lokasi ikut diubah
    private lateinit var binding : FragmentEditAparBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var aparData: Apar

    private var unit: String? = null
    private var location: String? = null
    private var locationStorageId: String? = null

    private lateinit var progressDialog : Dialog
    private lateinit var datePicker : DatePickerHelper
    private lateinit var datePicker2 : DatePickerHelper

    companion object {
        const val EXTRA_APAR_DATA = "extra_apar_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditAparBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            aparData = bundle.getParcelable<Apar>(EXTRA_APAR_DATA) as Apar
        }
        initView()

        binding.apply {
            etMedia.setOnClickListener {
                val mDialogMediaApar = DialogMediaAparFragment()
                val mFragmentManager = childFragmentManager
                mDialogMediaApar.show(
                    mFragmentManager,
                    mDialogMediaApar::class.java.simpleName
                )
            }
            etTanggalPembelian.setOnClickListener {
                openDialog()
            }
            etTanggalKadaluwarsa.setOnClickListener {
                openDialogKadaluwarsa()
            }
            btnEditApar.setOnClickListener {
                if (validateInput()){
                    editApar()
                }
            }
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }
    }

    private fun editApar() {
        progressDialog.show()

        binding.apply {
            aparData.departemenNama = etUnitDepartemen.text.toString()
            aparData.media = etMedia.text.toString()
            aparData.tipe = etTipe.text.toString()
            aparData.kapasitas = etKapasitas.text.toString()
            aparData.datePembelian = etTanggalPembelian.text.toString()
            aparData.dateKadaluwarsa = etTanggalKadaluwarsa.text.toString()
        }

        val valueLoc = binding.etLokasi.text.toString()
        if (aparData.lokasiApar != valueLoc){
            Log.e("locnotSame", "true")
            location = valueLoc
            val lokasi = binding.etLokasi.text.toString().filter { !it.isWhitespace() }
            val unitName = binding.etUnitDepartemen.text.toString().filter { !it.isWhitespace() }
            aparData.locationStorageId = String.format("$unitName%s", String.format(lokasi))
            aparData.lokasiApar = location
            DeleteQrPicture(aparData.qrAparPicture)
        } else {
            Log.e("locnotSame", "false")
            aparData.lokasiApar = binding.etLokasi.text.toString()
            UpdateApar()
        }
    }

    private fun DeleteQrPicture(qrAparPicture: String?) {
        mainViewModel.deleteQrPictureApar(qrAparPicture.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    UploadQrCode()
                    Log.d("statusDeleted", status)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UploadQrCode() {
        val bitmap = generateQRCode(aparData.aparId.toString())
        val file = File(requireContext().cacheDir,"CUSTOM NAME") //Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()

        val uri = file.toUri()
        mainViewModel.uploadImages(
            uri,
            "QRCODE",
            binding.etUnitDepartemen.text.toString(),
            aparData.locationStorageId.toString()
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                aparData.qrAparPicture = downloadUrl.toString()
                UpdateApar()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Upload QrCode Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
        } catch (e: WriterException) {
            Log.d(ContentValues.TAG, "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    private fun UpdateApar() {
        mainViewModel.editDataApar(aparData).observe(viewLifecycleOwner) { newUserData ->
            if (newUserData != null) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Data APAR berhasil diperbaharui.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "APAR gagal diperbaharui.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initView(){
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        datePicker = DatePickerHelper(requireContext())
        datePicker2 = DatePickerHelper(requireContext())
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")
        if (unit != P2K3){
            binding.etUnitDepartemen.apply {
                isEnabled = false
                isFocusable = false
                setText(unit)
            }
        }

        binding.apply {
            etUnitDepartemen.setOnClickListener {
                popupSheetDepartemen()
            }
            etMedia.setText(aparData.media)
            etLokasi.setText(aparData.lokasiApar)
            etKapasitas.setText(aparData.kapasitas)
            etTipe.setText(aparData.tipe)
            etTanggalPembelian.setText(aparData.datePembelian)
            etTanggalKadaluwarsa.setText(aparData.dateKadaluwarsa)
            etUnitDepartemen.setText(aparData.departemenNama)
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

    internal var optionDialogDepartemen: PopupDepartemenFragment.OnOptionDialogListener =
        object : PopupDepartemenFragment.OnOptionDialogListener {
            override fun onOptionChosen(category: String?, id: String?) {
                binding.etUnitDepartemen.setText(category)
                mainViewModel.setDepartemenData(id.toString()).observe(viewLifecycleOwner) { dataDepartemen ->
                    if (dataDepartemen != null) {
                        aparData.departemenId = dataDepartemen.departemenId
                    }
                }
            }
        }

    internal var optionDialogListener: DialogMediaAparFragment.OnOptionDialogListener =
        object : DialogMediaAparFragment.OnOptionDialogListener {
            override fun onOptionChosen(media: String?) {
                binding.etMedia.setText(media)
            }
        }

    private fun validateInput(): Boolean {
        val media = binding.etMedia.text.toString().trim()
        val departemen = binding.etUnitDepartemen.text.toString().trim()
        val lokasi = binding.etLokasi.text.toString().trim()
        val kapasitas = binding.etKapasitas.text.toString().trim()
        val pembelian = binding.etTanggalPembelian.text.toString().trim()
        val kadaluwarsa = binding.etTanggalKadaluwarsa.text.toString().trim()

        return when {

            media.isEmpty() -> {
                showSnakbar("Media Tidak Boleh Kosong.")
                false
            }
            departemen.isEmpty() -> {
                showSnakbar("Unit Departemen Tidak Boleh Kosong.")
                false
            }
            lokasi.isEmpty() -> {
                showSnakbar("Lokasi Tidak Boleh Kosong.")
                false
            }
            kapasitas.isEmpty() -> {
                showSnakbar("Kapasitas Tidak Boleh Kosong.")
                false
            }
            pembelian.isEmpty() -> {
                showSnakbar("Tanggal Pembelian Tidak Boleh Kosong.")
                false
            }
            kadaluwarsa.isEmpty() -> {
                showSnakbar("Tanggal Kadaluwarsa Tidak Boleh Kosong.")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun openDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(datePicker: View, dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "$dayofMonth"
                val mon = month + 1
                val date = "${dayStr} ${getMonthString(mon)} ${year}"
                binding.etTanggalPembelian.setText(date)
            }
        })
    }

    private fun openDialogKadaluwarsa() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker2.setMinDate(cal.timeInMillis)
        datePicker2.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(datePicker: View, dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "$dayofMonth"
                val mon = month + 1
                val date = "${dayStr} ${getMonthString(mon)} ${year}"
                binding.etTanggalKadaluwarsa.setText(date)
            }
        })
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