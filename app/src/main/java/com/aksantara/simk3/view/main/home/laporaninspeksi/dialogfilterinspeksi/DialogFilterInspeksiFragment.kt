package com.aksantara.simk3.view.main.home.laporaninspeksi.dialogfilterinspeksi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogFilterInspeksiBinding
import com.aksantara.simk3.databinding.FragmentSheetDepartemenBinding
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiFragment
import com.aksantara.simk3.view.main.inventory.InventoryFragment
import com.aksantara.simk3.view.main.inventory.dialogfilter.DialogFilterUnitFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogFilterInspeksiFragment : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentDialogFilterInspeksiBinding

    private var departemen: String? = null
    private var unit: String? = null
    private var departemenId: String? = null

    private var kondisi: String? = null
    private var media: String? = null

    private var optionDialogListener: OnOptionDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDialogFilterInspeksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")
        departemenId = preferences.getString("departemenId", "none")
        initView()
    }

    private fun initView() {
        binding.apply {
            if (unit == P2K3){
                tvUnitDepartemen.visibility = View.VISIBLE
                textInputLayout.visibility = View.VISIBLE
                etDepartemen.setOnClickListener {
                    val mPopupDepartemen = PopupDepartemenFragment()
                    val mFragmentManager = childFragmentManager
                    mPopupDepartemen.show(
                        mFragmentManager,
                        mPopupDepartemen::class.java.simpleName
                    )
                }
            } else {
                tvUnitDepartemen.visibility = View.GONE
                textInputLayout.visibility = View.GONE
            }
            rbSemuaKondisi.isChecked = true
            rbSemuaMedia.isChecked = true
            icBack.setOnClickListener {
                dismiss()
            }

            btnTerapkanFilter.setOnClickListener {
                applyFilter()
            }
        }
    }

    internal var optionDialogDepartemen: PopupDepartemenFragment.OnOptionDialogListener =
        object : PopupDepartemenFragment.OnOptionDialogListener {
            override fun onOptionChosen(category: String?, id: String?) {
                departemen = category
                departemenId = id
                binding.etDepartemen.setText(departemen)
        }
    }

    private fun applyFilter() {
        val checkedMedia = binding.rgMedia.checkedRadioButtonId
        if (checkedMedia != 1) {
            when (checkedMedia) {
                R.id.rb_semua_media -> media = binding.rbSemuaMedia.text.toString().trim()
                R.id.rb_apar_air -> media = binding.rbAparAir.text.toString().trim()
                R.id.rb_apar_foam -> media = binding.rbAparFoam.text.toString().trim()
                R.id.rb_apar_co2 -> media = binding.rbAparCo2.text.toString().trim()
            }
        }
        val checkedKondisi = binding.rgKondisi.checkedRadioButtonId
        if (checkedKondisi != 1) {
            when (checkedKondisi) {
                R.id.rb_semua_kondisi -> kondisi = binding.rbSemuaKondisi.text.toString().trim()
                R.id.rb_sempurna -> kondisi = binding.rbSempurna.text.toString().trim()
                R.id.rb_baik -> kondisi = binding.rbBaik.text.toString().trim()
                R.id.rb_kurang_baik -> kondisi = binding.rbKurangBaik.text.toString().trim()
                R.id.rb_buruk -> kondisi = binding.rbBuruk.text.toString().trim()
            }
        }
        if (unit == P2K3){
            optionDialogListener?.onOptionChosenP2K3(media, kondisi, binding.etDepartemen.text.toString(), departemenId)
            dialog?.dismiss()
        } else {
            optionDialogListener?.onOptionChosen(media, kondisi)
            dialog?.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is LaporanInspeksiFragment) {
            val laporanInspeksi = fragment
            this.optionDialogListener = laporanInspeksi.optionDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogListener = null
    }

    interface OnOptionDialogListener {
        fun onOptionChosenP2K3(media: String?, kondisi: String?, departemen: String?, departemenId: String?)
        fun onOptionChosen(media: String?, kondisi: String?)
    }

}