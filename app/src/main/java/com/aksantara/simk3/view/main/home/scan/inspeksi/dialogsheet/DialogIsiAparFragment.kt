package com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogSheetInspeksiBinding
import com.aksantara.simk3.utils.AppConstants.HABIS
import com.aksantara.simk3.utils.AppConstants.ISI_APAR
import com.aksantara.simk3.utils.AppConstants.MASIH_TERISI
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogIsiAparFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private var optionIsiApar: OnOptionIsiApar? = null

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
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_isi_apar)
            tvTitleInspeksi.text = ISI_APAR
            rbChoice1.text = MASIH_TERISI
            rbChoice2.text = HABIS
        }

        binding.btnLanjutkan.setOnClickListener {
            val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
            if (checkedRadioButtonId != 1) {
                when (checkedRadioButtonId) {
                    R.id.rb_choice_1 -> kategori = binding.rbChoice1.text.toString().trim()
                    R.id.rb_choice_2 -> kategori = binding.rbChoice2.text.toString().trim()
                }
                optionIsiApar?.onOptionChosen(kategori)
                dialog?.dismiss()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is InspeksiFragment) {
            val inspeksi = fragment
            this.optionIsiApar = inspeksi.optionIsiApar
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionIsiApar = inspeksi.optionIsiApar
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionIsiApar = null
    }

    interface OnOptionIsiApar {
        fun onOptionChosen(kategori: String?)
    }
}