package com.aksantara.simk3.view.main.home.scan.inspeksi

import android.app.Dialog
import android.content.Intent
import android.net.Uri
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
import com.aksantara.simk3.databinding.FragmentInspeksiBinding
import com.aksantara.simk3.databinding.ItemDialogInspeksiBerhasilBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.models.ListInspeksi
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.BAIK
import com.aksantara.simk3.utils.AppConstants.BERLEBIH_MERAH
import com.aksantara.simk3.utils.AppConstants.BERLUBANG_BERKARAT
import com.aksantara.simk3.utils.AppConstants.BURUK
import com.aksantara.simk3.utils.AppConstants.HABIS
import com.aksantara.simk3.utils.AppConstants.KURANG_BAIK
import com.aksantara.simk3.utils.AppConstants.KURANG_MERAH
import com.aksantara.simk3.utils.AppConstants.LEBIH_125CM
import com.aksantara.simk3.utils.AppConstants.LEBIH_15M
import com.aksantara.simk3.utils.AppConstants.PUDAR
import com.aksantara.simk3.utils.AppConstants.RETAK
import com.aksantara.simk3.utils.AppConstants.RUSAK
import com.aksantara.simk3.utils.AppConstants.SEMPURNA
import com.aksantara.simk3.utils.AppConstants.STATUS_DOCUMENT_EXIST
import com.aksantara.simk3.utils.AppConstants.STATUS_SUCCESS
import com.aksantara.simk3.utils.AppConstants.TERSUMBAT
import com.aksantara.simk3.utils.AppConstants.TIDAK_ADA
import com.aksantara.simk3.utils.AppConstants.TIDAK_SESUAI_STANDAR
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiActivity
import com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
class InspeksiFragment : Fragment() {

    private lateinit var binding : FragmentInspeksiBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val inspeksiViewModel: InspeksiViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private lateinit var aparData: Apar
    private lateinit var inspeksiData: Inspeksi
    private lateinit var listInspeksiData: ListInspeksi

    private val imageList = ArrayList<Uri>()
    private var uploads = 0
    private var index = 0
    private var url: ArrayList<String>? = null

    private var statusApar: String? = null
    private var dateNotSpace: String? = null
    private var statusKondisiApar: String? = null
    private var inspeksiId: String? = null
    private var listInspeksiId: String? = null

    private var kondisiTabungPicture: String? = null
    private var tekananTabungPicture: String? = null
    private var handlePicture: String? = null
    private var labelPicture: String? = null
    private var mulutPancarPicture: String? = null
    private var pipaPancarPicture: String? = null
    private var warnaTabungPicture: String? = null
    private var pemasanganPicture: String? = null

    private var totalKondisiBuruk = 0

    private var uriKondisiTabung: Uri? = null
    private var uriTekananTabung: Uri? = null
    private var uriHandle: Uri? = null
    private var uriLabel: Uri? = null
    private var uriMulutPancar: Uri? = null
    private var uriPipaPancar: Uri? = null
    private var uriWarnaTabung: Uri? = null
    private var uriPemasangan: Uri? = null

    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_APAR_DATA = "extra_apar_data"
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInspeksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aparData = Apar()
        usersDataProfile = Users()
        val bundle = arguments
        if (bundle != null) {
            aparData = bundle.getParcelable<Apar>(EXTRA_APAR_DATA) as Apar
            usersDataProfile = bundle.getParcelable<Users>(EXTRA_USER) as Users
        }

        progressDialog = ProgressDialogHelper.progressDialogInspeksi(requireContext())
        getUserData()
        initView()
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                }
            }
    }

    private fun initView() {
        binding.apply {

            tvUnitDepartemen.text = aparData.departemenNama
            tvLokasiApar.text = aparData.lokasiApar

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
            icBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnSimpanData.setOnClickListener {
                if (validateInput()){
                    inspeksiApar()
                }
            }
        }
    }

    private fun inspeksiApar() {

        progressDialog.show()

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

        val nama = usersDataProfile.nama.toString().filter { !it.isWhitespace() }
        val departemen = aparData.departemenNama.toString().filter { !it.isWhitespace() }
        dateNotSpace = DateHelper.getCurrentDate().filter { !it.isWhitespace() }
        inspeksiId = "InspeksiApar-${aparData.aparId}"
        listInspeksiId = "$nama$departemen$dateNotSpace"
        //for test
//        listInspeksiId = "AdminP2K3FarhanUnitDekanat13November2022"

        inspeksiData = Inspeksi(inspeksiId = inspeksiId,
            departemenId = aparData.departemenId,
            aparId = aparData.aparId,
            userId = usersDataProfile.userId,
            userDepartemen = usersDataProfile.departemen,
            userPicture = usersDataProfile.userPicture,
            departemenNama = aparData.departemenNama,
            lokasiApar = aparData.lokasiApar,
            namaPetugas = usersDataProfile.nama,
            jenisApar = aparData.media,
            statusKondisiApar = statusKondisiApar,
            statusApar = statusApar,
            kondisiTabung = binding.etKondisiTabung.text.toString(),
            isiApar = binding.etIsiApar.text.toString(),
            tekananTabung = binding.etTekananTabung.text.toString(),
            handle = binding.etHandle.text.toString(),
            label = binding.etLabel.text.toString(),
            mulutPancar = binding.etMulutPancar.text.toString(),
            pipaPancar = binding.etPipaPancar.text.toString(),
            tandaPemasangan = binding.etTandaPemasangan.text.toString(),
            jarakTanda = binding.etJarakPemasangan.text.toString(),
            jarakApar = binding.etJarakApar.text.toString(),
            warnaTabung = binding.etWarnaTabung.text.toString(),
            pemasanganApar = binding.etPemasangan.text.toString(),
            petunjukPenggunaan = binding.etPetunjukPenggunaan.text.toString(),
            catatanPemeriksaan = binding.etLabelCatatanPemeriksaan.text.toString(),
            createdAt = DateHelper.getCurrentDate(),
            locationStorageId = "${dateNotSpace}${aparData.locationStorageId}",
            statusDeletedInspeksi = false,
            updatedAt = "-",
            timeStamp = Timestamp(Date()),
            imgHandle = handlePicture,
            imgKondisiTabung = kondisiTabungPicture,
            imgLabel = labelPicture,
            imgMulutPancar = mulutPancarPicture,
            imgPemasangan = pemasanganPicture,
            imgPipaPancar = pipaPancarPicture,
            imgTekananTabung = tekananTabungPicture,
            imgWarnaTabung = warnaTabungPicture
        )

        listInspeksiData = ListInspeksi(listInspeksiId = listInspeksiId,
            departemenId = aparData.departemenId,
            aparId = aparData.aparId,
            userId = usersDataProfile.userId,
            userDepartemen = usersDataProfile.departemen,
            userPicture = usersDataProfile.userPicture,
            departemenNama = aparData.departemenNama,
            lokasiApar = aparData.lokasiApar,
            namaPetugas = usersDataProfile.nama,
            jenisApar = aparData.media,
            statusKondisiApar = statusKondisiApar,
            statusApar = statusApar,
            kondisiTabung = binding.etKondisiTabung.text.toString(),
            isiApar = binding.etIsiApar.text.toString(),
            tekananTabung = binding.etTekananTabung.text.toString(),
            handle = binding.etHandle.text.toString(),
            label = binding.etLabel.text.toString(),
            mulutPancar = binding.etMulutPancar.text.toString(),
            pipaPancar = binding.etPipaPancar.text.toString(),
            tandaPemasangan = binding.etTandaPemasangan.text.toString(),
            jarakTanda = binding.etJarakPemasangan.text.toString(),
            jarakApar = binding.etJarakApar.text.toString(),
            warnaTabung = binding.etWarnaTabung.text.toString(),
            pemasanganApar = binding.etPemasangan.text.toString(),
            petunjukPenggunaan = binding.etPetunjukPenggunaan.text.toString(),
            catatanPemeriksaan = binding.etLabelCatatanPemeriksaan.text.toString(),
            createdAt = DateHelper.getCurrentDate(),
            locationStorageId = "${dateNotSpace}${aparData.locationStorageId}",
            timeStamp = Timestamp(Date()),
            imgHandle = handlePicture,
            imgKondisiTabung = kondisiTabungPicture,
            imgLabel = labelPicture,
            imgMulutPancar = mulutPancarPicture,
            imgPemasangan = pemasanganPicture,
            imgPipaPancar = pipaPancarPicture,
            imgTekananTabung = tekananTabungPicture,
            imgWarnaTabung = warnaTabungPicture
        )

        if (uriKondisiTabung == null && uriTekananTabung == null && uriHandle == null && uriLabel == null
            && uriMulutPancar == null && uriPipaPancar == null && uriWarnaTabung == null && uriPemasangan == null){
            UploadDataListInspeksi(listInspeksiData, inspeksiData)
        } else {
            UploadDataListInspeksiWithImage(listInspeksiData, inspeksiData)
        }

    }

    private fun UploadDataListInspeksiWithImage(listInspeksiData: ListInspeksi, inspeksiData: Inspeksi) {
        inspeksiViewModel.uploadDataListInspeksi(listInspeksiData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                UploadDataInspeksiWithImage(inspeksiData)
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //kurang upload with Image, sama kayak document Exist
    //yg edit udah done
    private fun UploadDataInspeksiWithImage(inspeksiData: Inspeksi) {
        inspeksiViewModel.uploadDataInspeksi(inspeksiData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                if (statusKondisiApar == KURANG_BAIK || statusKondisiApar == BURUK){
                    Log.e("updateKurangBagus", "true")
                    inspeksiViewModel.updateAparKurangBagus(inspeksiData.departemenId.toString()).observe(viewLifecycleOwner) { status2 ->
                        if (status2 == STATUS_SUCCESS){
                            Log.e("UploadImg", "true")
                            UploadImgKondisiTabung()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(requireActivity(), status2, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("lgsgUploadImg", "true")
                    UploadImgKondisiTabung()
                }
            } else if (status == STATUS_DOCUMENT_EXIST){
                inspeksiViewModel.setInspeksiData(inspeksiId.toString())
                    .observe(viewLifecycleOwner) { inspeksi ->
                        val dateNow = DateHelper.getCurrentDate()
                        inspeksiData.createdAt = inspeksi.createdAt
                        inspeksiData.updatedAt = dateNow
                        if (inspeksi != null) {
                            if (inspeksi.statusApar != statusApar){
                                if (inspeksi.statusApar == BAIK) {
                                    UpdatePlusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, true)
                                } else {
                                    UpdateMinusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, true)
                                }
                            } else {
                                UpdateStatusKondisiApar(inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, true)
                            }
                        }
                    }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadMultipleImg(){
        val storageRef = FirebaseStorage.getInstance()
        val imageFolder = storageRef.reference.child("ImageFolder")
        for (i in uploads.until(imageList.size)){
            val imageName = imageFolder.child("image/${imageList[i].lastPathSegment}")
            imageName.putFile(imageList[i]).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageName.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    url!![i] = downloadUri.toString()
                    Log.e("url$i", url!![i])

                } else {
                    task.exception?.let {
                        throw it
                    }
                }
            }
        }
    }

    private fun UploadImgPemasangan() {
        if (uriPemasangan != null){
            inspeksiViewModel.uploadImages(
                uriPemasangan!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriWarnaTabung!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriPipaPancar!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriMulutPancar!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriLabel!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriHandle!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriTekananTabung!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
            inspeksiViewModel.uploadImages(
                uriKondisiTabung!!,
                "IMAGE-INSPEKSI",
                "${dateNotSpace}${aparData.locationStorageId}",
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
        inspeksiViewModel.updateImgInspeksi(inspeksiId.toString(), documentName, img.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status != STATUS_SUCCESS) {
                    inspeksiViewModel.updateImgListInspeksi(listInspeksiId.toString(), documentName, img.toString())
                        .observe(viewLifecycleOwner) { status2 ->
                            if (status2 != STATUS_SUCCESS) {
                                Log.e("updateImgListInspeksi", "true")
                                Toast.makeText(requireContext(), status2, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    private fun UploadDataInspeksi(inspeksiData: Inspeksi) {
        //Upload Inspeksi
        inspeksiViewModel.uploadDataInspeksi(inspeksiData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                //Upload
                if (statusKondisiApar == KURANG_BAIK || statusKondisiApar == BURUK){
                    Log.e("updateKurangBagus", "true")
                    inspeksiViewModel.updateAparKurangBagus(inspeksiData.departemenId.toString()).observe(viewLifecycleOwner) { status2 ->
                        if (status2 == STATUS_SUCCESS){
                            progressDialog.dismiss()
                            resultUpload()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(requireActivity(), status2, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("lgsgUploadWithoutImg", "true")
                    progressDialog.dismiss()
                    resultUpload()
                }
            } else if (status == STATUS_DOCUMENT_EXIST) {
                Log.e("documentInspeksiExist", "true")
                //Edit Inspeksi
                //Get document before, then if still bad not update status and update total at departement
                inspeksiViewModel.setInspeksiData(inspeksiId.toString())
                    .observe(viewLifecycleOwner) { inspeksi ->
                        val dateNow = DateHelper.getCurrentDate()
                        inspeksiData.createdAt = inspeksi.createdAt
                        inspeksiData.updatedAt = dateNow
                        if (inspeksi != null) {
                            if (inspeksi.statusApar != statusApar){
                                if (inspeksi.statusApar == BAIK) {
                                    UpdatePlusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, false)
                                } else {
                                    UpdateMinusStatusAparKurangBaik(inspeksiData.departemenId, inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, false)
                                }
                            } else {
                                UpdateStatusKondisiApar(inspeksiData.aparId, statusApar, statusKondisiApar, dateNow, false)
                            }
                        }
                    }
            } else {
                //Error
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateInspeksiUpload(inspeksiData: Inspeksi, isImage: Boolean){
        inspeksiViewModel.editDataInspeksi(inspeksiData).observe(viewLifecycleOwner) { statusUpdate ->
            if (statusUpdate != null) {
                if (isImage){
                    UploadImgKondisiTabung()
                } else {
                    progressDialog.dismiss()
                    resultUpload()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), statusUpdate, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun UpdateStatusKondisiApar(aparId: String?, statusApar: String?, statusKondisiApar: String?, dateNow: String?, isImage: Boolean) {
        inspeksiViewModel.updateInspeksiStatusApar(aparId.toString(), statusApar.toString(), statusKondisiApar.toString(), dateNow.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == STATUS_SUCCESS) {
                    if (isImage){
                        updateInspeksiUpload(inspeksiData, true)
                    } else {
                        updateInspeksiUpload(inspeksiData, false)
                        Log.d("statusKondisiApar", status)
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UpdatePlusStatusAparKurangBaik(departemenId: String?, aparId: String?, statusApar: String?, statusKondisiApar: String?, dateNow: String?, isImage: Boolean) {
        inspeksiViewModel.updateInspeksiPlusAparStatusKurangBagus(departemenId.toString(), aparId.toString(), statusApar.toString(), statusKondisiApar.toString(), dateNow.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == STATUS_SUCCESS) {
                    if (isImage){
                        updateInspeksiUpload(inspeksiData, true)
                    } else {
                        updateInspeksiUpload(inspeksiData, false)
                        Log.d("statusUpdatePlusApar", status)
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UpdateMinusStatusAparKurangBaik(departemenId: String?, aparId: String?, statusApar: String?, statusKondisiApar: String?, dateNow: String?, isImage: Boolean) {
        inspeksiViewModel.updateInspeksiMinusAparStatusKurangBagus(departemenId.toString(), aparId.toString(), statusApar.toString(), statusKondisiApar.toString(), dateNow.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == STATUS_SUCCESS) {
                    if (isImage){
                        updateInspeksiUpload(inspeksiData, true)
                    } else {
                        updateInspeksiUpload(inspeksiData, false)
                        Log.d("statusUpdateMinusApar", status)
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun UploadDataListInspeksi(listInspeksiData: ListInspeksi, inspeksiData: Inspeksi) {

        inspeksiViewModel.uploadDataListInspeksi(listInspeksiData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                UploadDataInspeksi(inspeksiData)
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), status, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun resultUpload() {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogInspeksiBerhasilBinding =
            ItemDialogInspeksiBerhasilBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.btnCekLaporanInspeksi.setOnClickListener {
            dialog.dismiss()
            val intent =
                Intent(requireActivity(), LaporanInspeksiActivity::class.java)
            intent.putExtra(LaporanInspeksiActivity.EXTRA_INSPEKSI, inspeksiData)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.btnBackHome.setOnClickListener {
            dialog.dismiss()
            val intent =
                Intent(requireActivity(), MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_USER, usersDataProfile)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun validateInput(): Boolean {

        val handle = binding.etHandle.text.toString().trim()
        val label = binding.etLabel.text.toString().trim()
        val isiApar = binding.etIsiApar.text.toString().trim()
        val pemasangan = binding.etPemasangan.text.toString().trim()
        val kondisiTabung = binding.etKondisiTabung.text.toString().trim()
        val jarakApar = binding.etJarakApar.text.toString().trim()
        val jarakPemasangan = binding.etJarakPemasangan.text.toString().trim()
        val catatanPemeriksaan = binding.etLabelCatatanPemeriksaan.text.toString().trim()
        val mulutPancar = binding.etMulutPancar.text.toString().trim()
        val petunjukPenggunaan = binding.etPetunjukPenggunaan.text.toString().trim()
        val pipaPancar = binding.etPipaPancar.text.toString().trim()
        val tandaPemasangan = binding.etTandaPemasangan.text.toString().trim()
        val tekanan = binding.etTekananTabung.text.toString().trim()
        val warnaTabung = binding.etWarnaTabung.text.toString().trim()

        return when {

            handle.isEmpty() -> {
                showSnakbar("Handle Tidak Boleh Kosong.")
                false
            }
            label.isEmpty() -> {
                showSnakbar("Label Tidak Boleh Kosong.")
                false
            }
            isiApar.isEmpty() -> {
                showSnakbar("Isi Apar Tidak Boleh Kosong.")
                false
            }
            pemasangan.isEmpty() -> {
                showSnakbar("Pemasangan Tidak Boleh Kosong.")
                false
            }
            kondisiTabung.isEmpty() -> {
                showSnakbar("Kondisi Tabung Tidak Boleh Kosong.")
                false
            }
            jarakApar.isEmpty() -> {
                showSnakbar("Jarak APAR Tidak Boleh Kosong.")
                false
            }
            jarakPemasangan.isEmpty() -> {
                showSnakbar("Jarak Pemasangan Tidak Boleh Kosong.")
                false
            }
            catatanPemeriksaan.isEmpty() -> {
                showSnakbar("Catatan Pemeriksaan Tidak Boleh Kosong.")
                false
            }
            mulutPancar.isEmpty() -> {
                showSnakbar("Mulut Pancar Tidak Boleh Kosong.")
                false
            }
            petunjukPenggunaan.isEmpty() -> {
                showSnakbar("Petunjuk Penggunaan Tidak Boleh Kosong.")
                false
            }
            pipaPancar.isEmpty() -> {
                showSnakbar("Pipa Pancar Tidak Boleh Kosong.")
                false
            }
            tandaPemasangan.isEmpty() -> {
                showSnakbar("Tanda Pemasangan Tidak Boleh Kosong.")
                false
            }
            tekanan.isEmpty() -> {
                showSnakbar("Tekanan Tidak Boleh Kosong.")
                false
            }
            warnaTabung.isEmpty() -> {
                showSnakbar("Warna Tabung Tidak Boleh Kosong.")
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

    private fun setDialogSheet(fragment: BottomSheetDialogFragment){
        val mFragmentManager = childFragmentManager
        fragment.show(
            mFragmentManager,
            fragment::class.java.simpleName
        )
    }

    internal var optionKondisiTabung: DialogKondisiTabungFragment.OnOptionKondisiTabung =
        object : DialogKondisiTabungFragment.OnOptionKondisiTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etKondisiTabung.setText(kategori)
                if (kategori == BERLUBANG_BERKARAT){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == HABIS){
                    totalKondisiBuruk+=1
                }
            }
    }

    internal var optionTekananTabung: DialogTekananTabung.OnOptionTekananTabung =
        object : DialogTekananTabung.OnOptionTekananTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etTekananTabung.setText(kategori)
                if (kategori == KURANG_MERAH || kategori == BERLEBIH_MERAH){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == RUSAK){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == RUSAK){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == TERSUMBAT){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == RETAK){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == TIDAK_ADA){
                    totalKondisiBuruk+=1
                }
            }
    }

    internal var optionJarakTanda: DialogJarakTandaFragment.OnOptionJarakTanda =
        object : DialogJarakTandaFragment.OnOptionJarakTanda {
            override fun onOptionChosen(kategori: String?) {
                binding.etJarakPemasangan.setText(kategori)
                if (kategori == LEBIH_125CM){
                    totalKondisiBuruk+=1
                }
            }
    }

    internal var optionJarakApar: DialogJarakAparFragment.OnOptionJarakApar =
        object : DialogJarakAparFragment.OnOptionJarakApar {
            override fun onOptionChosen(kategori: String?) {
                binding.etJarakApar.setText(kategori)
                if (kategori == LEBIH_15M){
                    totalKondisiBuruk+=1
                }
            }
    }

    internal var optionWarnaTabung: DialogWarnaTabungFragment.OnOptionWarnaTabung =
        object : DialogWarnaTabungFragment.OnOptionWarnaTabung {
            override fun onOptionChosen(kategori: String?, imageUri: Uri?) {
                binding.etWarnaTabung.setText(kategori)
                if (kategori == PUDAR){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == TIDAK_SESUAI_STANDAR){
                    totalKondisiBuruk+=1
                }
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
                if (kategori == TIDAK_ADA){
                    totalKondisiBuruk+=1
                }
            }
    }

    internal var optionCatatanPemeriksaan: DialogCatatanPemeriksaanFragment.OnOptionCatatanPemeriksaan =
        object : DialogCatatanPemeriksaanFragment.OnOptionCatatanPemeriksaan {
            override fun onOptionChosen(kategori: String?) {
                binding.etLabelCatatanPemeriksaan.setText(kategori)
                if (kategori == TIDAK_ADA){
                    totalKondisiBuruk+=1
                }
            }
    }
}