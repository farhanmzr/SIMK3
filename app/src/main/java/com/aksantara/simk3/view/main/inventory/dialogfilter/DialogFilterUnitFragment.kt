package com.aksantara.simk3.view.main.inventory.dialogfilter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.databinding.FragmentSheetDepartemenBinding
import com.aksantara.simk3.utils.AppConstants.FILTER_UNIT_DEPARTEMEN
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenClickCallback
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.inventory.InventoryFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogFilterUnitFragment: BottomSheetDialogFragment(), PopupDepartemenClickCallback {

    private lateinit var binding: FragmentSheetDepartemenBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val dialogFilterUnitAdapter = DialogFilterUnitAdapter(this@DialogFilterUnitFragment)

    private var departemenName: String ?= null
    private var departemenId: String ?= null

    private var optionDialogListener: OnOptionDialogListener? = null

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
            tvTitle.text = FILTER_UNIT_DEPARTEMEN
            btnClose.setOnClickListener {
                dismiss()
            }
            btnLanjutkan.setOnClickListener {
                optionDialogListener?.onOptionChosen(departemenName, departemenId)
                dismiss()
            }

        }
        setDataDepartemenRv()
    }

    private fun setDataDepartemenRv() {
        showProgressBar(true)
        mainViewModel.getListDepartemen()
            .observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null && dataDepartemen.isNotEmpty()) {
                    showProgressBar(false)
                    dialogFilterUnitAdapter.setListDepartemen(dataDepartemen)
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
            adapter = dialogFilterUnitAdapter
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

        if (fragment is InventoryFragment) {
            val inventoryFragment = fragment
            this.optionDialogListener = inventoryFragment.optionDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogListener = null
    }

    interface OnOptionDialogListener {
        fun onOptionChosen(category: String?, id: String?)
    }

}