package com.aksantara.simk3.view.main.home.detaillaporaninspeksi

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.*
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.ADA
import com.aksantara.simk3.utils.AppConstants.BAIK
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.KURANG_125CM
import com.aksantara.simk3.utils.AppConstants.KURANG_15M
import com.aksantara.simk3.utils.AppConstants.MASIH_TERISI
import com.aksantara.simk3.utils.AppConstants.MERAH_TERANG
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.PAS_125_CM
import com.aksantara.simk3.utils.AppConstants.SEMPURNA
import com.aksantara.simk3.utils.AppConstants.SESUAI_HIJAU
import com.aksantara.simk3.utils.AppConstants.TIDAK_SESUAI_STANDAR
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailLaporanInspeksiFragment : Fragment() {

    private lateinit var binding : FragmentDetailLaporanInspeksiBinding

    private val laporanInspeksiViewModel: LaporanInspeksiViewModel by activityViewModels()
    private lateinit var inspeksiData: Inspeksi
    private var unit: String? = null
    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_INSPEKSI_DATA = "extra_inspeksi_data"
        const val FROM_HOME = "from_home"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailLaporanInspeksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        progressDialog.show()

        inspeksiData = Inspeksi()
        if (arguments != null) {
            inspeksiData = requireArguments().getParcelable(EXTRA_INSPEKSI_DATA)!!
            laporanInspeksiViewModel.setInspeksiData(inspeksiData.inspeksiId.toString())
                .observe(viewLifecycleOwner) { inspeksi ->
                    if (inspeksi != null) {
                        inspeksiData = inspeksi
                }
            }
        }

        when (unit) {
            P2K3 -> {
                binding.btnHapusInspeksi.visibility = View.VISIBLE
            }
            GUEST -> {
            }
            else -> {
                binding.btnHapusInspeksi.visibility = View.GONE
            }
        }

        getInspeksiData()
    }

    private fun getInspeksiData() {
        laporanInspeksiViewModel.getInspeksiData()
            .observe(viewLifecycleOwner) { inspeksi ->
                if (inspeksi != null) {
                    inspeksiData = inspeksi
                    progressDialog.dismiss()
                    setViewInspeksiData(inspeksiData)
                }
            }
    }

    private fun setViewInspeksiData(inspeksiData: Inspeksi) {
        binding.apply {
            tvUnitDepartemen.text = inspeksiData.departemenNama
            tvLokasiApar.text = inspeksiData.lokasiApar
            tvMediaApar.text = inspeksiData.jenisApar
            tvKondisiApar.text = inspeksiData.statusKondisiApar

            tvNamaPetugas.text = inspeksiData.namaPetugas
            tvTglInspeksi.text = inspeksiData.createdAt

            tvKondisiTabung.text = inspeksiData.kondisiTabung
            tvIsiApar.text = inspeksiData.isiApar
            tvTekananTabung.text = inspeksiData.tekananTabung
            tvHandle.text = inspeksiData.handle
            tvLabel.text = inspeksiData.label
            tvMulutPancar.text = inspeksiData.mulutPancar
            tvPipaPancar.text = inspeksiData.pipaPancar
            tvTandaPemasangan.text = inspeksiData.tandaPemasangan
            tvJarakTanda.text = inspeksiData.jarakTanda
            tvJarakAntarApar.text = inspeksiData.jarakApar
            tvWarnaTabung.text = inspeksiData.warnaTabung
            tvPemasangan.text = inspeksiData.pemasanganApar
            tvPetunjukPenggunaan.text = inspeksiData.petunjukPenggunaan
            tvLabelCatatanPemeriksaan.text = inspeksiData.catatanPemeriksaan

            when (inspeksiData.statusKondisiApar) {
                SEMPURNA -> {
                    tvKondisiApar.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTxtGreen))
                }
                BAIK -> {
                    tvKondisiApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtYellow))
                }
                AppConstants.KURANG_BAIK -> {
                    tvKondisiApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtOrange))
                }
                else -> {
                    tvKondisiApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
                }
            }

            //<<Image>>//
            if (inspeksiData.imgKondisiTabung != null){
                imgKondisiTabung.visibility = View.VISIBLE
                imgKondisiTabung.setOnClickListener { openImage(inspeksiData.imgKondisiTabung.toString()) }
            }
            if (inspeksiData.imgTekananTabung != null){
                imgTekananTabung.visibility = View.VISIBLE
                imgTekananTabung.setOnClickListener { openImage(inspeksiData.imgTekananTabung.toString()) }
            }
            if (inspeksiData.imgHandle != null){
                imgHandle.visibility = View.VISIBLE
                imgHandle.setOnClickListener { openImage(inspeksiData.imgHandle.toString()) }
            }
            if (inspeksiData.imgLabel != null){
                imgLabel.visibility = View.VISIBLE
                imgLabel.setOnClickListener { openImage(inspeksiData.imgLabel.toString()) }
            }
            if (inspeksiData.imgMulutPancar != null){
                imgMulutPancar.visibility = View.VISIBLE
                imgMulutPancar.setOnClickListener { openImage(inspeksiData.imgMulutPancar.toString()) }
            }
            if (inspeksiData.imgPipaPancar != null){
                imgPipaPancar.visibility = View.VISIBLE
                imgPipaPancar.setOnClickListener { openImage(inspeksiData.imgPipaPancar.toString()) }
            }
            if (inspeksiData.imgWarnaTabung != null){
                imgWarnaTabung.visibility = View.VISIBLE
                imgWarnaTabung.setOnClickListener { openImage(inspeksiData.imgWarnaTabung.toString()) }
            }
            if (inspeksiData.imgPemasangan != null){
                imgPemasangan.visibility = View.VISIBLE
                imgPemasangan.setOnClickListener { openImage(inspeksiData.imgPemasangan.toString()) }
            }

            //<<Text Color>>//
            if (inspeksiData.kondisiTabung == BAIK){
                tvKondisiTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvKondisiTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.isiApar == MASIH_TERISI){
                tvIsiApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvIsiApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.tekananTabung == SESUAI_HIJAU){
                tvTekananTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvTekananTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.handle == BAIK){
                tvHandle.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvHandle.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.label == BAIK){
                tvLabel.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvLabel.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.mulutPancar == BAIK){
                tvMulutPancar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvMulutPancar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.pipaPancar == BAIK){
                tvPipaPancar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvPipaPancar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.tandaPemasangan == ADA){
                tvTandaPemasangan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvTandaPemasangan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.jarakTanda == KURANG_125CM || inspeksiData.jarakTanda == PAS_125_CM){
                tvJarakTanda.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvJarakTanda.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.jarakApar == KURANG_15M){
                tvJarakAntarApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvJarakAntarApar.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.warnaTabung == MERAH_TERANG){
                tvWarnaTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvWarnaTabung.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.pemasanganApar == TIDAK_SESUAI_STANDAR){
                tvPemasangan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            } else {
                tvPemasangan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            }
            if (inspeksiData.petunjukPenggunaan == ADA){
                tvPetunjukPenggunaan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvPetunjukPenggunaan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }
            if (inspeksiData.catatanPemeriksaan == ADA){
                tvLabelCatatanPemeriksaan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTxtBlue))
            } else {
                tvLabelCatatanPemeriksaan.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRedSnackbar))
            }

            icBack.setOnClickListener { activity?.onBackPressed() }
            btnHapusInspeksi.setOnClickListener {
                openDialogHapusInspeksi()
            }
            btnEditInspeksi.setOnClickListener {
                val mHomeFragment = EditLaporanInspeksiFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.commit {
                    addToBackStack(null)
                    replace(
                        R.id.host_laporan_inspeksi_activity,
                        mHomeFragment
                    )
                }
            }
        }
    }

    private fun openDialogHapusInspeksi() {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogHapusInspeksiBinding =
            ItemDialogHapusInspeksiBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.btnYa.setOnClickListener {
            hapusInspeksi()
        }
        binding.btnTidak.setOnClickListener { dialog.dismiss() }
    }

    private fun hapusInspeksi() {
        progressDialog.show()
        laporanInspeksiViewModel.deleteInspeksi(inspeksiData.inspeksiId.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("deleteInspeksi", status)
                    progressDialog.dismiss()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun openImage(image: String) {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogImageInspeksiBinding =
            ItemDialogImageInspeksiBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.imgInspeksi.loadImage(image)
    }



}