package com.aksantara.simk3.view.main.home.scan.dialogdetailapar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogDetailAparBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.HomeFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class DialogDetailAparFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogDetailAparBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var aparData: Apar
    private lateinit var usersDataProfile: Users


    private var optionResultScan: ResultDialogListener? = null

    companion object {
        const val EXTRA_APAR_DATA = "extra_apar_data"
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogDetailAparBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aparData = Apar()
        if (arguments != null) {
            val aparId = requireArguments().getString(EXTRA_APAR_DATA)!!
            mainViewModel.setAparData(aparId)
                .observe(viewLifecycleOwner) { apar ->
                    if (apar != null) {
                        aparData = apar
                        getAparData()
                        getUserData()
                    }
                }
        }

        binding.apply {
            icBack.setOnClickListener {
                dismiss()
            }
            btnEditApar.setOnClickListener {
                optionResultScan?.onOptionResultScan(aparData)
                dismiss()
            }
            btnInspeksiApar.setOnClickListener {
                if (aparData.statusKadaluwarsa != true) {
                    val intent =
                        Intent(requireActivity(), InspeksiActivity::class.java)
                    intent.putExtra(InspeksiActivity.EXTRA_APAR, aparData)
                    intent.putExtra(InspeksiActivity.EXTRA_USER, usersDataProfile)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "APAR sudah kadaluwarsa. Silahkan untuk memperbaharui APAR.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    Log.e("nama", usersDataProfile.nama.toString())
                }
            }
    }

    private fun getAparData() {
        mainViewModel.getAparData()
            .observe(viewLifecycleOwner) { apar ->
                if (apar != null) {
                    aparData = apar
                    binding.btnInspeksiApar.isEnabled = aparData.statusDeletedApar != true
                    checkKadaluwarsa(aparData)
                    setViewAparData(aparData)
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkKadaluwarsa(aparData: Apar) {

        val localeID = Locale("in", "ID")
        val inputFormat = SimpleDateFormat("dd MMMM yyyy", localeID)
        val inputDateStr = aparData.dateKadaluwarsa.toString()
        val date = inputFormat.parse(inputDateStr)

        val outputFormat = SimpleDateFormat("MM/dd/yyyy", localeID)
        val dateKadaluwarsa = outputFormat.format(date)
        val dateNow = DateHelper.getCurrentDateFormat()

        val sdf = SimpleDateFormat("MM/dd/yyyy", localeID)
        val firstDate: Date = sdf.parse(dateKadaluwarsa)
        val secondDate: Date = sdf.parse(dateNow)

        when {
            firstDate > secondDate -> {
                Log.e("days", "Belum Kadaluwarsa")
            }
            firstDate < secondDate -> {
                updateKadaluwarsa()
            }
            else -> {
                Log.e("days", "same day")
            }
        }

    }

    private fun updateKadaluwarsa() {
        mainViewModel.updatePlusAparKadaluwarsa(aparData.departemenId.toString(), aparData.aparId.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusKadaluwarsa", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setViewAparData(aparData: Apar) {
        binding.apply {
            tvKondisiApar.text = aparData.statusKondisiTerakhir
            tvMediaApar.text = aparData.media
            tvTipeApar.text = aparData.tipe
            tvUnitDepartemen.text = aparData.departemenNama
            tvLokasiApar.text = aparData.lokasiApar
            tvKapasitasApar.text = aparData.kapasitas + " Kg"
            tvTanggalPembelian.text = aparData.datePembelian
            tvKadaluwarsa.text = aparData.dateKadaluwarsa
            tvTerakhirInspeksi.text = aparData.dateTerakhirInspeksi
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is HomeFragment) {
            val homeFragment = fragment
            this.optionResultScan = homeFragment.optionResultScan
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionResultScan = null
    }

    interface ResultDialogListener {
        fun onOptionResultScan(aparData: Apar)
    }

}