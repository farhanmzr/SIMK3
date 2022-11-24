package com.aksantara.simk3.view.main.profile.daftarunit.dialogeditunit

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogDetailAparBinding
import com.aksantara.simk3.databinding.FragmentDialogEditUnitBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.scan.dialogdetailapar.DialogDetailAparFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogEditUnitFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogEditUnitBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var departemenData: Departemen

    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_DEPARTEMEN_DATA = "extra_departemen_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogEditUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        departemenData = Departemen()
        if (arguments != null) {
            val aparId = requireArguments().getString(EXTRA_DEPARTEMEN_DATA)!!
            mainViewModel.setDepartemenData(aparId)
                .observe(viewLifecycleOwner) { departemen ->
                    if (departemen != null) {
                        departemenData = departemen
                        getDepartemenData()
                    }
                }
        }
    }

    private fun getDepartemenData() {
        mainViewModel.getDepartemenData()
            .observe(viewLifecycleOwner) { departemens ->
                if (departemens != null) {
                    departemenData = departemens
                    setView(departemenData)
                }
            }
    }

    private fun setView(departemenData: Departemen) {
        binding.apply {
            etNamaUnit.setText(departemenData.nama)
            btnEditUnit.setOnClickListener {
                progressDialog.show()
                mainViewModel.updateUnitName(departemenData.departemenId.toString(), binding.etNamaUnit.text.toString())
                    .observe(viewLifecycleOwner) { status ->
                        if (status == AppConstants.STATUS_SUCCESS) {
                            progressDialog.dismiss()
                            dismiss()
                            Toast.makeText(requireContext(), "Nama Unit Berhasil Diperbaharui.", Toast.LENGTH_SHORT).show()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            icBack.setOnClickListener { dismiss() }
        }
    }

}