package com.aksantara.simk3.view.main.inventory.tambahapar

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentTambahAparBinding
import com.aksantara.simk3.databinding.ItemDialogBerhasilTambahAparBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.*
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryActivity
import com.aksantara.simk3.view.main.inventory.tambahapar.dialogmediaapar.DialogMediaAparFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
class TambahAparFragment : Fragment() {

    private lateinit var binding : FragmentTambahAparBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private lateinit var aparData: Apar

    private var departemenId: String? = null
    private var aparId: String? = null
    private var unit: String? = null
    private var qrAparPicture: String? = null
    private var locationStorageId: String? = null

    private lateinit var progressDialog : Dialog
    private lateinit var datePicker : DatePickerHelper
    private lateinit var datePicker2 : DatePickerHelper

    companion object {
        const val DEPARTEMEN_ID = "departemen_id"
        const val TOTAL_APAR = "totalApar"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTambahAparBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        getUserData()

        binding.apply {
            etMedia.setOnClickListener {
                val mDialogMediaApar = DialogMediaAparFragment()
                val mFragmentManager = childFragmentManager
                mDialogMediaApar.show(
                    mFragmentManager,
                    mDialogMediaApar::class.java.simpleName
                )
            }
            if (unit == P2K3){
                etUnitDepartemen.setOnClickListener {
                    val mPopupDepartemen = PopupDepartemenFragment()
                    val mFragmentManager = childFragmentManager
                    mPopupDepartemen.show(
                        mFragmentManager,
                        mPopupDepartemen::class.java.simpleName
                    )
                }
            }
            etTanggalPembelian.setOnClickListener {
                openDialog()
            }
            etTanggalKadaluwarsa.setOnClickListener {
                openDialogKadaluwarsa()
            }
            btnTambahApar.setOnClickListener {
                if (validateInput()){
                    addApar()
                }
            }
        }
    }

    internal var optionDialogDepartemen: PopupDepartemenFragment.OnOptionDialogListener =
        object : PopupDepartemenFragment.OnOptionDialogListener {
            override fun onOptionChosen(category: String?, id: String?) {
                binding.etUnitDepartemen.setText(category)
                mainViewModel.setDepartemenData(id.toString()).observe(viewLifecycleOwner) { dataDepartemen ->
                    if (dataDepartemen != null) {
                        departemenId = dataDepartemen.departemenId
                    }
                }
        }
    }

    private fun initView() {
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        datePicker = DatePickerHelper(requireContext())
        datePicker2 = DatePickerHelper(requireContext())
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        if (unit != P2K3){
            val bundle = arguments
            if (bundle != null) {
                departemenId = bundle.getString(DEPARTEMEN_ID)
            }
        }

        binding.icBack.setOnClickListener {
            bottomNav.visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun addApar() {
        progressDialog.show()
        val lokasi = binding.etLokasi.text.toString().filter { !it.isWhitespace() }
        val unitName = binding.etUnitDepartemen.text.toString().filter { !it.isWhitespace() }
//        val date = DateHelper.getCurrentDate().filter { !it.isWhitespace() }
        val db = FirebaseFirestore.getInstance()
        val documentId = db.collection("apar").document()
        aparId = unitName + documentId.id
        locationStorageId = String.format("$unitName%s", String.format(lokasi))

        UploadQrCode()
    }

    private fun UploadQrCode() {
        val bitmap = generateQRCode(aparId.toString())
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
            locationStorageId.toString()
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                qrAparPicture = downloadUrl.toString()
                uploadDataApar()
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

    private fun uploadDataApar() {

        aparData = Apar(
            aparId = aparId,
            qrAparPicture = qrAparPicture,
            departemenId = departemenId,
            departemenNama = binding.etUnitDepartemen.text.toString(),
            media = binding.etMedia.text.toString(),
            tipe = binding.etTipe.text.toString(),
            kapasitas = binding.etKapasitas.text.toString(),
            lokasiApar = binding.etLokasi.text.toString(),
            datePembelian = binding.etTanggalPembelian.text.toString(),
            dateKadaluwarsa = binding.etTanggalKadaluwarsa.text.toString(),
            dateTerakhirInspeksi = "-",
            statusKondisiTerakhir = "-",
            statusApar = "-",
            createdAt = DateHelper.getCurrentDate(),
            statusKadaluwarsa = false,
            locationStorageId = locationStorageId,
            timeStamp = Timestamp(Date()),
            statusDeletedApar = false,
            reasonDeletedApar = "-"
        )
        uploadApar(aparData)
    }

    private fun uploadApar(aparData: Apar) {
        mainViewModel.uploadDataApar(aparData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                progressDialog.dismiss()
                val builder = AlertDialog.Builder(requireContext())
                val binding: ItemDialogBerhasilTambahAparBinding =
                    ItemDialogBerhasilTambahAparBinding.inflate(LayoutInflater.from(context))
                builder.setView(binding.root)
                val dialog = builder.create()
                dialog.show()
                binding.btnBackHome.setOnClickListener {
                    dialog.dismiss()
                    val intent =
                        Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_USER, usersDataProfile)
                    startActivity(intent)
                    requireActivity().finish()
                }
                binding.btnCekDataApar.setOnClickListener {
                    dialog.dismiss()
                    val intent =
                        Intent(requireActivity(), DetailInventoryActivity::class.java)
                    intent.putExtra(DetailInventoryActivity.EXTRA_APAR, aparData)
                    startActivity(intent)
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 250
        val height = 250
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
            Log.d(TAG, "generateQRCode: ${e.message}")
        }
        return bitmap
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
            pembelian == kadaluwarsa -> {
                showSnakbar("Tanggal Pembelian Tidak Boleh Sama dengan Kadaluwarsa.")
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
                val mon = month
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
                val mon = month
                val date = "${dayStr} ${getMonthString(mon)} ${year}"
                binding.etTanggalKadaluwarsa.setText(date)
            }
        })
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    if (usersDataProfile.departemen != P2K3){
                        binding.etUnitDepartemen.apply {
                            isEnabled = false
                            isFocusable = false
                            setText(usersDataProfile.departemen)
                        }
                    }
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