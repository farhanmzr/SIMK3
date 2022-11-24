package com.aksantara.simk3.view.login.dialogdepartemen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.databinding.FragmentSheetDepartemenBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.view.login.LoginFragment
import com.aksantara.simk3.view.login.LoginViewModel
import com.aksantara.simk3.view.login.register.RegisterFragment
import com.aksantara.simk3.view.main.home.laporaninspeksi.dialogfilterinspeksi.DialogFilterInspeksiFragment
import com.aksantara.simk3.view.main.inventory.detailinventory.editapar.EditAparFragment
import com.aksantara.simk3.view.main.inventory.tambahapar.TambahAparFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PopupDepartemenFragment: BottomSheetDialogFragment(), PopupDepartemenClickCallback {

    private lateinit var binding: FragmentSheetDepartemenBinding

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val popupDepartemenAdapter = PopupDepartemenAdapter(this@PopupDepartemenFragment)

    private var departemenName: String ?= null
    private var departemenId: String ?= null

    private var optionDialogDepartemen: OnOptionDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSheetDepartemenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            btnLanjutkan.setOnClickListener {
                optionDialogDepartemen?.onOptionChosen(departemenName, departemenId)
                dismiss()
            }

        }
        setDataDepartemenRv()
    }

    private fun setDataDepartemenRv() {
        showProgressBar(true)
        loginViewModel.getListDepartemen()
            .observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null && dataDepartemen.isNotEmpty()) {
                    showProgressBar(false)
                    popupDepartemenAdapter.setListDepartemen(dataDepartemen)
                    setDepartemenAdapter()
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setDepartemenAdapter() {
        with(binding.rvDepartemen) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = popupDepartemenAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onItemClicked(value: String, id: String) {
        departemenName = value
        departemenId = id
        binding.btnLanjutkan.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        setDataDepartemenRv()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is RegisterFragment) {
            val registerFragment = fragment
            this.optionDialogDepartemen = registerFragment.optionDialogDepartemen
        } else if (fragment is EditAparFragment){
            val editAparFragment = fragment
            this.optionDialogDepartemen = editAparFragment.optionDialogDepartemen
        } else if (fragment is TambahAparFragment){
            val tambahApar = fragment
            this.optionDialogDepartemen = tambahApar.optionDialogDepartemen
        } else if (fragment is DialogFilterInspeksiFragment){
            val dialogFragment = fragment
            this.optionDialogDepartemen = dialogFragment.optionDialogDepartemen
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogDepartemen = null
    }

    interface OnOptionDialogListener {
        fun onOptionChosen(category: String?, id: String?)
    }

}