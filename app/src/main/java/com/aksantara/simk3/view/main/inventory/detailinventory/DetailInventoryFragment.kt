package com.aksantara.simk3.view.main.inventory.detailinventory

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDetailInventoryBinding
import com.aksantara.simk3.databinding.FragmentInventoryBinding
import com.aksantara.simk3.databinding.ItemDialogGuestLoginBinding
import com.aksantara.simk3.databinding.ItemDialogHapusAparBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.KURANG_BAGUS
import com.aksantara.simk3.utils.AppConstants.KURANG_BAIK
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.inventory.detailinventory.editapar.EditAparFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class DetailInventoryFragment : Fragment() {

    private lateinit var binding : FragmentDetailInventoryBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var aparData: Apar
    private var unit: String? = null
    private var isKurangBagus: Boolean? = null

    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_APAR_DATA = "extra_apar_data"
        const val FROM_HOME = "from_home"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        aparData = Apar()
        if (arguments != null) {
            aparData = requireArguments().getParcelable(EXTRA_APAR_DATA)!!
            mainViewModel.setAparData(aparData.aparId.toString())
                .observe(viewLifecycleOwner) { apar ->
                    if (apar != null) {
                        aparData = apar
                    }
                }
        }

        binding.icBack.setOnClickListener {
            activity?.onBackPressed()
        }

        when (unit) {
            P2K3 -> {
                binding.btnHapusApar.visibility = View.VISIBLE
            }
            GUEST -> {
                binding.btnEditApar.visibility = View.GONE
                binding.btnHapusApar.visibility = View.GONE
            }
            else -> {
                binding.btnHapusApar.visibility = View.GONE
            }
        }

        getAparData()
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
                if (aparData.statusKadaluwarsa != true){
                    updateKadaluwarsa()
                }
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
                    Log.d("updateApar", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getAparData() {
        progressDialog.show()
        mainViewModel.getAparData()
            .observe(viewLifecycleOwner) { apar ->
                if (apar != null) {
                    aparData = apar
                    progressDialog.dismiss()
                    setViewAparData(aparData)
                    checkKadaluwarsa(aparData)
                }
            }
    }

    private fun setViewAparData(aparData: Apar) {
        binding.apply {
            imgBarcodeApar.loadImage(aparData.qrAparPicture)
            tvKondisiApar.text = aparData.statusKondisiTerakhir
            tvMediaApar.text = aparData.media
            tvTipeApar.text = aparData.tipe
            tvUnitDepartemen.text = aparData.departemenNama
            tvLokasiApar.text = aparData.lokasiApar
            tvKapasitasApar.text = aparData.kapasitas + " Kg"
            tvTanggalPembelian.text = aparData.datePembelian
            tvKadaluwarsa.text = aparData.dateKadaluwarsa
            tvTerakhirInspeksi.text = aparData.dateTerakhirInspeksi

            btnEditApar.setOnClickListener {
                editApar()
            }
            btnHapusApar.setOnClickListener {
                openDialogDeleteApar()
            }
        }
    }

    private fun editApar() {
        val mDetailInventory = EditAparFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(EditAparFragment.EXTRA_APAR_DATA, aparData)
        mDetailInventory.arguments = mBundle
        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_detail_inventory_activity,
                mDetailInventory
            )
        }
    }

    private fun openDialogDeleteApar() {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogHapusAparBinding =
            ItemDialogHapusAparBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        val reason = binding.etReasonDeleteApar.text.toString().trim()
        binding.btnYa.setOnClickListener {
            if (reason.isEmpty()){
                dialog.dismiss()
                hapusApar(reason)
            } else {
                Toast.makeText(requireContext(), "Alasan APAR dihapus tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnTidak.setOnClickListener { dialog.dismiss() }
    }

    private fun hapusApar(reason: String) {
        progressDialog.show()
        isKurangBagus = aparData.statusApar == KURANG_BAIK
        Log.e("kurangbaik", isKurangBagus.toString())
        if (isKurangBagus == true){
            updateTotalAparKurangBagus(aparData.departemenId)
        }
        if (aparData.statusKadaluwarsa == true){
            updateTotalKadaluwarsa(aparData.departemenId)
        }
        deleteApar(aparData.aparId, aparData.departemenId, reason)
    }

    private fun updateTotalKadaluwarsa(departemenId: String?) {
        mainViewModel.updateMinusAparKadaluwarsa(departemenId.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusKadaluwarsa", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateTotalAparKurangBagus(departemenId: String?) {
        mainViewModel.updateAparKurangBagus(departemenId.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    Log.d("statusKurangBagus", status)
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteApar(aparId: String?, departemenId: String?, reason: String) {
        mainViewModel.deleteApar(aparId.toString(), departemenId.toString(), reason)
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    progressDialog.dismiss()
                    activity?.onBackPressed()
                } else {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
        }
    }

}