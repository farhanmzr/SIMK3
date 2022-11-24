package com.aksantara.simk3.view.main.profile.daftarunit.dialogaddunit

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogAddUnitBinding
import com.aksantara.simk3.databinding.FragmentDialogEditUnitBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.profile.daftarunit.dialogeditunit.DialogEditUnitFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
class DialogAddUnit : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogAddUnitBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var departemenData: Departemen

    private lateinit var progressDialog : Dialog

    private var totalItem = 0
    private var departemenId: String? = null

    companion object {
        const val EXTRA_TOTAL_DATA = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogAddUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        if (arguments != null) {
            totalItem = requireArguments().getInt(EXTRA_TOTAL_DATA.toString())
            departemenId = "departemen" + String.format("%03d", totalItem.toDouble().roundToInt())
            Log.e("departemenId", departemenId.toString())
        }

        binding.apply {
            btnAddUnit.setOnClickListener {
                if (validateInput()){
                    addDepartemen()
                }
            }
            icBack.setOnClickListener { dismiss() }
        }
    }

    private fun addDepartemen() {
        progressDialog.show()

        departemenData = Departemen(
            departemenId = departemenId,
            nama = binding.etNamaUnit.text.toString(),
            totalApar = 0,
            totalAparKurangBagus = 0,
            totalAparKadaluwarsa = 0,
            statusDepartemenDelete = false
        )

        mainViewModel.uploadDataDepartemen(departemenData)
            .observe(viewLifecycleOwner) { status ->
                if (status == AppConstants.STATUS_SUCCESS) {
                    progressDialog.dismiss()
                    dismiss()
                    Toast.makeText(requireContext(), "Berhasil menambahkan Departemen baru.", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInput(): Boolean {
        val name = binding.etNamaUnit.text.toString().trim()

        return when {

            name.isEmpty() -> {
                showSnakbar("Nama Departemen Tidak Boleh Kosong.")
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


}