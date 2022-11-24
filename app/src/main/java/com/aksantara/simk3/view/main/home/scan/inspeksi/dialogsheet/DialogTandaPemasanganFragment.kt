package com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogSheetInspeksiBinding
import com.aksantara.simk3.utils.AppConstants.ADA
import com.aksantara.simk3.utils.AppConstants.TANDA_PEMASANGAN
import com.aksantara.simk3.utils.AppConstants.TIDAK_ADA
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogTandaPemasanganFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private var optionTandaPemasangan: OnOptionTandaPemasangan? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogSheetInspeksiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            icBack.setOnClickListener { dismiss() }
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_tanda_pemasangan)
            tvTitleInspeksi.text = TANDA_PEMASANGAN
            rbChoice1.text = ADA
            rbChoice2.text = TIDAK_ADA
        }

        binding.btnLanjutkan.setOnClickListener {
            val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
            if (checkedRadioButtonId != 1) {
                when (checkedRadioButtonId) {
                    R.id.rb_choice_1 -> kategori = binding.rbChoice1.text.toString().trim()
                    R.id.rb_choice_2 -> kategori = binding.rbChoice2.text.toString().trim()
                }
                optionTandaPemasangan?.onOptionChosen(kategori)
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is InspeksiFragment) {
            val inspeksi = fragment
            this.optionTandaPemasangan = inspeksi.optionTandaPemasangan
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionTandaPemasangan = inspeksi.optionTandaPemasangan
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionTandaPemasangan = null
    }

    interface OnOptionTandaPemasangan {
        fun onOptionChosen(kategori: String?)
    }
}