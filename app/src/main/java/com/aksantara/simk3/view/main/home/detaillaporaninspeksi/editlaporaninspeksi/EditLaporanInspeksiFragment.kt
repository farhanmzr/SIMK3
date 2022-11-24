package com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.databinding.FragmentEditLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.BAIK
import com.aksantara.simk3.utils.AppConstants.BURUK
import com.aksantara.simk3.utils.AppConstants.KURANG_BAIK
import com.aksantara.simk3.utils.AppConstants.SEMPURNA
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiActivity
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiViewModel
import com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class EditLaporanInspeksiFragment : Fragment() {

    private lateinit var binding : FragmentEditLaporanInspeksiBinding

    private val laporanInspeksiViewModel: LaporanInspeksiViewModel by activityViewModels()
    private lateinit var inspeksiData: Inspeksi
    private var unit: String? = null

    private var kondisiTabungPicture: String? = null
    private var tekananTabungPicture: String? = null
    private var handlePicture: String? = null
    private var labelPicture: String? = null
    private var mulutPancarPicture: String? = null
    private var pipaPancarPicture: String? = null
    private var warnaTabungPicture: String? = null
    private var pemasanganPicture: String? = null

    private var totalKondisiBuruk = 0

    private var statusKondisiApar: String? = null
    private var statusApar: String? = null

    private var uriKondisiTabung: Uri? = null
    private var uriTekananTabung: Uri? = null
    private var uriHandle: Uri? = null
    private var uriLabel: Uri? = null
    private var uriMulutPancar: Uri? = null
    private var uriPipaPancar: Uri? = null
    private var uriWarnaTabung: Uri? = null
    private var uriPemasangan: Uri? = null

    private lateinit var progressDialog : Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditLaporanInspeksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialogInspeksi(requireContext())
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        laporanInspeksiViewModel.getInspeksiData()
            .observe(viewLifecycleOwner) { inspeksi ->
                if (inspeksi != null) {
                    inspeksiData = inspeksi
                    kondisiTabungPicture = inspeksiData.imgKondisiTabung
                    tekananTabungPicture = inspeksiData.imgTekananTabung
                    handlePicture = inspeksiData.imgHandle
                    labelPicture = inspeksiData.imgLabel
                    mulutPancarPicture = inspeksiData.imgMulutPancar
                    pipaPancarPicture = inspeksiData.imgPipaPancar
                    warnaTabungPicture = inspeksiData.imgWarnaTabung
                    pemasanganPicture = inspeksiData.imgPemasangan
                    setViewInspeksiData(inspeksiData)
                }
        }
    }

    private fun setViewInspeksiData(inspeksiData: Inspeksi) {
        binding.apply {

            etKondisiTabung.setOnClickListener {
                setDialogSheet(DialogKondisiTabungFragment())
            }
            etIsiApar.setOnClickListener {
                setDialogSheet(DialogIsiAparFragment())
            }
            etTekananTabung.setOnClickListener {
                setDialogSheet(DialogTekananTabung())
            }
            etHandle.setOnClickListener {
                setDialogSheet(DialogHandleFragment())
            }
            etLabel.setOnClickListener {
                setDialogSheet(DialogLabelFragment())
            }
            etMulutPancar.setOnClickListener {
                setDialogSheet(DialogMulutPancarFragment())
            }
            etPipaPancar.setOnClickListener {
                setDialogSheet(DialogPipaPancarFragment())
            }
            etTandaPemasangan.setOnClickListener {
                setDialogSheet(DialogTandaPemasanganFragment())
            }
            etJarakPemasangan.setOnClickListener {
                setDialogSheet(DialogJarakTandaFragment())
            }
            etJarakApar.setOnClickListener {
                setDialogSheet(DialogJarakAparFragment())
            }
            etWarnaTabung.setOnClickListener {
                setDialogSheet(DialogWarnaTabungFragment())
            }
            etPemasangan.setOnClickListener {
                setDialogSheet(DialogPemasanganAparFragment())
            }
            etPetunjukPenggunaan.setOnClickListener {
                setDialogSheet(DialogPetunjukPenggunaan())
            }
            etLabelCatatanPemeriksaan.setOnClickListener {
                setDialogSheet(DialogCatatanPemeriksaanFragment())
            }

            icBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
            tvUnitDepartemen.text = inspeksiData.departemenNama
            tvLokasiApar.text = inspeksiData.lokasiApar

            etKondisiTabung.setText(inspeksiData.kondisiTabung)
            if (inspeksiData.imgKondisiTabung != null){
                imgKondisiTabung.visibility = View.VISIBLE
            }
            etIsiApar.setText(inspeksiData.isiApar)
            etTekananTabung.setText(inspeksiData.tekananTabung)
            if (inspeksiData.imgTekananTabung != null){
                imgTekananTabung.visibility = View.VISIBLE
            }
            etHandle.setText(inspeksiData.handle)
            if (inspeksiData.imgHandle != null){
                imgHandle.visibility = View.VISIBLE
            }
            etLabel.setText(inspeksiData.label)
            if (inspeksiData.imgLabel != null){
                imgLabel.visibility = View.VISIBLE
            }
            etMulutPancar.setText(inspeksiData.mulutPancar)
            if (inspeksiData.imgMulutPancar != null){
                imgMulutPancar.visibility = View.VISIBLE
            }
            etPipaPancar.setText(inspeksiData.pipaPancar)
            if (inspeksiData.imgPipaPancar != null){
                imgPipaPancar.visibility = View.VISIBLE
            }
            etTandaPemasangan.setText(inspeksiData.tandaPemasangan)
            etJarakPemasangan.setText(inspeksiData.jarakTanda)
            etJarakApar.setText(inspeksiData.jarakApar)
            etWarnaTabung.setText(inspeksiData.warnaTabung)
            if (inspeksiData.imgWarnaTabung != null){
                imgWarnaTabung.visibility = View.VISIBLE
            }
            etPemasangan.setText(inspeksiData.pemasanganApar)
            if (inspeksiData.imgPemasangan != null){
                imgPemasangan.visibility = View.VISIBLE
            }
            etPetunjukPenggunaan.setText(inspeksiData.petunjukPenggunaan)
            etLabelCatatanPemeriksaan.setText(inspeksiData.catatanPemeriksaan)

            btnSimpanPerubahan.setOnClickListener {
                val iKondisi = inspeksiData.kondisiTabung
                val iTekanan = inspeksiData.tekananTabung
                val iWarna = inspeksiData.warnaTabung
                val iMulutpancar = inspeksiData.mulutPancar
                val iHandle = inspeksiData.handle
                val iLabel = inspeksiData.label
                val iIsiapar = inspeksiData.isiApar
                val iJarakapar = inspeksiData.jarakApar
                val iJarakpemasangan = inspeksiData.jarakTanda
                val iCatatan = inspeksiData.catatanPemeriksaan
                val iPemasangan = inspeksiData.pemasanganApar
                val iPetunjuk = inspeksiData.petunjukPenggunaan
                val iPipa = inspeksiData.pipaPancar
                val iTanda = inspeksiData.tandaPemasangan
                val kondisi = binding.etKondisiTabung.text.toString()
                val tekanan = binding.etTekananTabung.text.toString()
                val warna = binding.etWarnaTabung.text.toString()
                val mulutpancar = binding.etMulutPancar.text.toString()
                val handle = binding.etHandle.text.toString()
                val label = binding.etLabel.text.toString()
                val isiapar = binding.etIsiApar.text.toString()
                val jarakapar = binding.etJarakApar.text.toString()
                val jarakpemasangan = binding.etJarakPemasangan.text.toString()
                val labelcatatan = binding.etLabelCatatanPemeriksaan.text.toString()
                val pemasangan = binding.etPemasangan.text.toString()
                val petunjuk = binding.etPetunjukPenggunaan.text.toString()
                val pipa = binding.etPipaPancar.text.toString()
                val tanda = binding.etTandaPemasangan.text.toString()

                if (iKondisi == kondisi && iTekanan == tekanan && iWarna == warna && iMulutpancar == mulutpancar && iHandle
                    == handle && iLabel == label && iIsiapar == isiapar && iJarakapar == jarakapar && iJarakpemasangan == jarakpemasangan
                    && iCatatan == labelcatatan && iPemasangan == pemasangan && iPetunjuk == petunjuk && iPipa == pipa && iTanda == tanda){
                    Toast.makeText(
                        requireActivity(),
                        "Data Inspeksi masih sama dan tidak diperbaharui.",
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                } else {
                    editInspeksi(inspeksiData)
                }
            }

        }
    }

    private fun editInspeksi(inspeksiData: Inspeksi) {

        progressDialog.show()

        val kondisi = binding.etKondisiTabung.text.toString()
        val tekanan = binding.etTekananTabung.text.toString()
        val warna = binding.etWarnaTabung.text.toString()
        val mulutpancar = binding.etMulutPancar.text.toString()
        val handle = binding.etHandle.text.toString()
        val label = binding.etLabel.text.toString()
        val isiapar = binding.etIsiApar.text.toString()
        val jarakapar = binding.etJarakApar.text.toString()
        val jarakpemasangan = binding.etJarakPemasangan.text.toString()
        val labelcatatan = binding.etLabelCatatanPemeriksaan.text.toString()
        val pemasangan = binding.etPemasangan.text.toString()
        val petunjuk = binding.etPetunjukPenggunaan.text.toString()
        val pipa = binding.etPipaPancar.text.toString()
        val tanda = binding.etTandaPemasangan.text.toString()

        if (kondisi == AppConstants.BERLUBANG_BERKARAT){
            totalKondisiBuruk+=1
        }
        if (isiapar == AppConstants.HABIS){
            totalKondisiBuruk+=1
        }
        if (tekanan == AppConstants.KURANG_MERAH || tekanan == AppConstants.BERLEBIH_MERAH){
            totalKondisiBuruk+=1
        }
        if (handle == AppConstants.RUSAK){
            totalKondisiBuruk+=1
        }
        if (label == AppConstants.RUSAK){
            totalKondisiBuruk+=1
        }
        if (mulutpancar == AppConstants.TERSUMBAT){
            totalKondisiBuruk+=1
        }
        if (pipa == AppConstants.RETAK){
            totalKondisiBuruk+=1
        }
        if (tanda == AppConstants.TIDAK_ADA){
            totalKondisiBuruk+=1
        }
        if (jarakpemasangan == AppConstants.LEBIH_125CM){
            totalKondisiBuruk+=1
        }
        if (jarakapar == AppConstants.LEBIH_15M){
            totalKondisiBuruk+=1
        }
        if (warna == AppConstants.PUDAR){
            totalKondisiBuruk+=1
        }
        if (pemasangan == AppConstants.TIDAK_SESUAI_STANDAR){
            totalKondisiBuruk+=1
        }
        if (petunjuk == AppConstants.TIDAK_ADA){
            totalKondisiBuruk+=1
        }
        if (labelcatatan == AppConstants.TIDAK_ADA){
            totalKondisiBuruk+=1
        }
        Log.e("totalKondisiBuruk", totalKondisiBuruk.toString())
        if (totalKondisiBuruk == 0){
            statusKondisiApar = SEMPURNA
            statusApar = BAIK
        } else if (totalKondisiBuruk in 1 until 3){
            statusKondisiApar = BAIK
            statusApar = BAIK
        } else if (totalKondisiBuruk in 4 until 7){
            statusKondisiApar = KURANG_BAIK
            statusApar = KURANG_BAIK
        } else if (totalKondisiBuruk >= 7 ){
            statusKondisiApar = BURUK
            statusApar = KURANG_BAIK
        }

        val editInspeksiData = Inspeksi(
            inspeksiId = inspeksiData.inspeksiId,
            departemenId = inspeksiData.departemenId,
            aparId = inspeksiData.aparId,
            userId = inspeksiData.userId,
            userDepartemen = inspeksiData.userDepartemen,
            userPicture = inspeksiData.userPicture,
            departemenNama = inspeksiData.departemenNama,
            lokasiApar = inspeksiData.lokasiApar,
            namaPetugas = inspeksiData.namaPetugas,
            jenisApar = inspeksiData.jenisApar,
            statusKondisiApar = statusKondisiApar,
            statusApar = statusApar,
            kondisiTabung = kondisi,
            isiApar = isiapar,
            tekananTabung = tekanan,
            handle = handle,
            label = label,
            mulutPancar = mulutpancar,
            pipaPancar = pipa,
            tandaPemasangan = tanda,
            jarakTanda = jarakpemasangan,
            jarakApar = jarakapar,
            warnaTabung = warna,
            pemasanganApar = pemasangan,
            petunjukPenggunaan = petunjuk,
            catatanPemeriksaan = labelcatatan,
            createdAt = inspeksiData.createdAt,
            locationStorageId = inspeksiData.locationStorageId,
            statusDeletedInspeksi = false,
            updatedAt = DateHelper.getCurrentDate(),
            timeStamp = inspeksiData.timeStamp,
            imgHandle = handlePicture,
            imgKondisiTabung = kondisiTabungPicture,
            imgLabel = labelPicture,
            imgMulutPancar = mulutPancarPicture,
            imgPemasangan = pemasanganPicture,
            imgPipaPancar = pipaPancarPicture,
            imgTekananTabung = tekananTabungPicture,
            imgWarnaTabung = warnaTabungPicture
        )

        if (inspeksiData.statusApar != statusApar){
            if (inspeksiData.statusApar == BAIK) {
                UpdatePlusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar)
            } else {
                UpdateMinusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar)
            }
        } else {
            UpdateStatusKondisiApar(inspeksiData.aparId, statusApar, statusKondisiApar)
        }

        //update status apar juga
        if (uriKondisiTabung == null && uriTekananTabung == null && uriHandle == null && uriLabel == null
            && uriMulutPancar == null && uriPipaPancar == null && uriWarnaTabung == null && uriPemasangan == null){
            UploadDataInspeksi(editInspeksiData)
        } else {
            UploadDataInspeksiWithImage(editInspeksiData)
        }
    }

    private fun UpdateStatusKondisiApar(aparId: String?, statusApar: String?, statusKondisiApar: String?) {
        laporanInspeksiViewModel.updateStatusApar(aparId.toString(), statusApar.toString(), statusKondisiApar.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusKondisiApar", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UpdatePlusStatusAparKurangBaik(departemenId: String?, aparId: String?, statusApar: String?, statusKondisiApar: String?) {
        laporanInspeksiViewModel.updatePlusAparStatusKurangBagus(departemenId.toString(), aparId.toString(), statusApar.toString(), statusKondisiApar.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusUpdatePlusApar", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UpdateMinusStatusAparKurangBaik(departemenId: String?, aparId: String?, statusApar: String?, statusKondisiApar: String?) {
        laporanInspeksiViewModel.updateMinusAparStatusKurangBagus(departemenId.toString(), aparId.toString(), statusApar.toString(), statusKondisiApar.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusUpdateMinusApar", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UploadDataInspeksi(editInspeksiData: Inspeksi) {
        laporanInspeksiViewModel.editDataInspeksi(editInspeksiData).observe(viewLifecycleOwner) { status ->
            if (status != null) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Data Inspeksi berhasil diperbaharui.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun UploadDataInspeksiWithImage(editInspeksiData: Inspeksi) {
        laporanInspeksiViewModel.editDataInspeksi(editInspeksiData).observe(viewLifecycleOwner) { status ->
            if (status != null) {
                Log.e("UploadImg", "true")
                UploadImgKondisiTabung()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun UploadImgPemasangan() {
        if (uriPemasangan != null){
            laporanInspeksiViewModel.uploadImages(
                uriPemasangan!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_PEMASANGAN"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    pemasanganPicture = downloadUrl.toString()
                    updateImgInspeksi(pemasanganPicture.toString(), "imgPemasangan")
                    resultUpload()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Pemasangan Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            resultUpload()
        }
    }

    private fun UploadImgWarnaTabung() {
        if (uriWarnaTabung != null){
            laporanInspeksiViewModel.uploadImages(
                uriWarnaTabung!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_WARNA_TABUNG"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    warnaTabungPicture = downloadUrl.toString()
                    updateImgInspeksi(warnaTabungPicture.toString(), "imgWarnaTabung")
                    UploadImgPemasangan()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Warna Tabung Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgPemasangan()
        }
    }

    private fun UploadImgPipaPancar() {
        if (uriPipaPancar != null){
            laporanInspeksiViewModel.uploadImages(
                uriPipaPancar!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_PIPA_PANCAR"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    pipaPancarPicture = downloadUrl.toString()
                    updateImgInspeksi(pipaPancarPicture.toString(), "imgPipaPancar")
                    UploadImgWarnaTabung()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Pipa Pancar Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgWarnaTabung()
        }
    }

    private fun UploadImgMulutPancar() {
        if (uriMulutPancar != null){
            laporanInspeksiViewModel.uploadImages(
                uriMulutPancar!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_MULUT_PANCAR"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    mulutPancarPicture = downloadUrl.toString()
                    updateImgInspeksi(mulutPancarPicture.toString(), "imgMulutPancar")
                    UploadImgPipaPancar()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Mulut Pancar Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgPipaPancar()
        }
    }

    private fun UploadImgLabel() {
        if (uriLabel != null) {
            laporanInspeksiViewModel.uploadImages(
                uriLabel!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_LABEL"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    labelPicture = downloadUrl.toString()
                    updateImgInspeksi(labelPicture.toString(), "imgLabel")
                    UploadImgMulutPancar()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Label Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgMulutPancar()
        }
    }

    private fun UploadImgHandle() {
        if (uriHandle != null){
            laporanInspeksiViewModel.uploadImages(
                uriHandle!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_HANDLE"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    handlePicture = downloadUrl.toString()
                    updateImgInspeksi(handlePicture.toString(), "imgHandle")
                    UploadImgLabel()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Handle Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgLabel()
        }

    }

    private fun UploadImgTekananTabung() {
        if (uriTekananTabung != null){
            laporanInspeksiViewModel.uploadImages(
                uriTekananTabung!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMAGE_TEKANAN_TABUNG"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    tekananTabungPicture = downloadUrl.toString()
                    updateImgInspeksi(tekananTabungPicture.toString(), "imgTekananTabung")
                    UploadImgHandle()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Tekanan Tabung Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgHandle()
            Log.e("emptyTekananTabung", "true")
        }
    }

    private fun UploadImgKondisiTabung() {
        if (uriKondisiTabung!=null){
            laporanInspeksiViewModel.uploadImages(
                uriKondisiTabung!!,
                "IMAGE-INSPEKSI",
                inspeksiData.locationStorageId.toString(),
                "IMG_KONDISI_TABUNG"
            ).observe(viewLifecycleOwner) { downloadUrl ->
                if (downloadUrl != null) {
                    kondisiTabungPicture = downloadUrl.toString()
                    updateImgInspeksi(kondisiTabungPicture.toString(), "imgKondisiTabung")
                    UploadImgTekananTabung()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Upload Image Kondisi Tabung Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            UploadImgTekananTabung()
            Log.e("emptyKondisiTabung", "true")
        }

    }

    private fun updateImgInspeksi(documentName: String, img: String?) {
        laporanInspeksiViewModel.updateImgInspeksi(inspeksiData.inspeksiId.toString(), documentName, img.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status != AppConstants.STATUS_SUCCESS) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resultUpload() {
        val intent =
            Intent(requireActivity(), LaporanInspeksiActivity::class.java)
        intent.putExtra(LaporanInspeksiActivity.EXTRA_INSPEKSI, inspeksiData)
        startActivity(intent)
        requireActivity().finish()
    }

    internal var optionKondisiTabung: DialogKondisiTabungFragment.OnOptionKondisiTabung =
        object : DialogKondisiTabungFragment.OnOptionKondisiTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etKondisiTabung.setText(kategori)
                if (imageUri!= null){
                    uriKondisiTabung = imageUri
                    binding.imgKondisiTabung.visibility = View.VISIBLE
                } else {
                    binding.imgKondisiTabung.visibility = View.GONE
                }
            }
        }

    internal var optionIsiApar: DialogIsiAparFragment.OnOptionIsiApar =
        object : DialogIsiAparFragment.OnOptionIsiApar {
            override fun onOptionChosen(kategori: String?) {
                binding.etIsiApar.setText(kategori)
            }
        }

    internal var optionTekananTabung: DialogTekananTabung.OnOptionTekananTabung =
        object : DialogTekananTabung.OnOptionTekananTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etTekananTabung.setText(kategori)
                if (imageUri!= null){
                    uriTekananTabung = imageUri
                    binding.imgTekananTabung.visibility = View.VISIBLE
                } else {
                    binding.imgTekananTabung.visibility = View.GONE
                }
            }
        }

    internal var optionHandle: DialogHandleFragment.OnOptionHandle =
        object : DialogHandleFragment.OnOptionHandle {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etHandle.setText(kategori)
                if (imageUri!= null){
                    uriHandle = imageUri
                    binding.imgHandle.visibility = View.VISIBLE
                } else {
                    binding.imgHandle.visibility = View.GONE
                }
            }
        }

    internal var optionLabel: DialogLabelFragment.OnOptionLabel =
        object : DialogLabelFragment.OnOptionLabel {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etLabel.setText(kategori)
                if (imageUri!= null){
                    uriLabel = imageUri
                    binding.imgLabel.visibility = View.VISIBLE
                } else {
                    binding.imgLabel.visibility = View.GONE
                }
            }
        }

    internal var optionMulutPancar: DialogMulutPancarFragment.OnOptionMulutPancar =
        object : DialogMulutPancarFragment.OnOptionMulutPancar {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etMulutPancar.setText(kategori)
                if (imageUri!= null){
                    uriMulutPancar = imageUri
                    binding.imgMulutPancar.visibility = View.VISIBLE
                } else {
                    binding.imgMulutPancar.visibility = View.GONE
                }
            }
        }

    internal var optionPipaPancar: DialogPipaPancarFragment.OnOptionPipaPancar =
        object : DialogPipaPancarFragment.OnOptionPipaPancar {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etPipaPancar.setText(kategori)
                if (imageUri!= null){
                    uriPipaPancar = imageUri
                    binding.imgPipaPancar.visibility = View.VISIBLE
                } else {
                    binding.imgPipaPancar.visibility = View.GONE
                }
            }
        }

    internal var optionTandaPemasangan: DialogTandaPemasanganFragment.OnOptionTandaPemasangan =
        object : DialogTandaPemasanganFragment.OnOptionTandaPemasangan {
            override fun onOptionChosen(kategori: String?) {
                binding.etTandaPemasangan.setText(kategori)
            }
        }

    internal var optionJarakTanda: DialogJarakTandaFragment.OnOptionJarakTanda =
        object : DialogJarakTandaFragment.OnOptionJarakTanda {
            override fun onOptionChosen(kategori: String?) {
                binding.etJarakPemasangan.setText(kategori)
            }
        }

    internal var optionJarakApar: DialogJarakAparFragment.OnOptionJarakApar =
        object : DialogJarakAparFragment.OnOptionJarakApar {
            override fun onOptionChosen(kategori: String?) {
                binding.etJarakApar.setText(kategori)
            }
        }

    internal var optionWarnaTabung: DialogWarnaTabungFragment.OnOptionWarnaTabung =
        object : DialogWarnaTabungFragment.OnOptionWarnaTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etWarnaTabung.setText(kategori)
                if (imageUri!= null){
                    uriWarnaTabung = imageUri
                    binding.imgWarnaTabung.visibility = View.VISIBLE
                } else {
                    binding.imgWarnaTabung.visibility = View.GONE
                }

            }
        }

    internal var optionPemasanganApar: DialogPemasanganAparFragment.OnOptionPemasanganApar =
        object : DialogPemasanganAparFragment.OnOptionPemasanganApar {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etPemasangan.setText(kategori)
                if (imageUri!= null){
                    uriPemasangan = imageUri
                    binding.imgPemasangan.visibility = View.VISIBLE
                } else {
                    binding.imgPemasangan.visibility = View.GONE
                }
            }
        }

    internal var optionPetunjukPenggunaan: DialogPetunjukPenggunaan.OnOptionPetunjukPenggunaan =
        object : DialogPetunjukPenggunaan.OnOptionPetunjukPenggunaan {
            override fun onOptionChosen(kategori: String?) {
                binding.etPetunjukPenggunaan.setText(kategori)
            }
        }

    internal var optionCatatanPemeriksaan: DialogCatatanPemeriksaanFragment.OnOptionCatatanPemeriksaan =
        object : DialogCatatanPemeriksaanFragment.OnOptionCatatanPemeriksaan {
            override fun onOptionChosen(kategori: String?) {
                binding.etLabelCatatanPemeriksaan.setText(kategori)
            }
        }

    private fun setDialogSheet(fragment: BottomSheetDialogFragment){
        val mFragmentManager = childFragmentManager
        fragment.show(
            mFragmentManager,
            fragment::class.java.simpleName
        )
    }

}