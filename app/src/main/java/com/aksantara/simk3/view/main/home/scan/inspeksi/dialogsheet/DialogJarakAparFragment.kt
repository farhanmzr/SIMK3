package com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogSheetInspeksiBinding
import com.aksantara.simk3.utils.AppConstants.JARAK_APAR
import com.aksantara.simk3.utils.AppConstants.KURANG_15M
import com.aksantara.simk3.utils.AppConstants.LEBIH_15M
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogJarakAparFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private var optionJarakApar: OnOptionJarakApar? = null

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
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_jarak_apar)
            tvTitleInspeksi.text = JARAK_APAR
            rbChoice1.text = KURANG_15M
            rbChoice2.text = LEBIH_15M
        }

        binding.btnLanjutkan.setOnClickListener {
            val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
            if (checkedRadioButtonId != 1) {
                when (checkedRadioButtonId) {
                    R.id.rb_choice_1 -> kategori = binding.rbChoice1.text.toString().trim()
                    R.id.rb_choice_2 -> kategori = binding.rbChoice2.text.toString().trim()
                }
                optionJarakApar?.onOptionChosen(kategori)
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is InspeksiFragment) {
            val inspeksi = fragment
            this.optionJarakApar = inspeksi.optionJarakApar
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionJarakApar = inspeksi.optionJarakApar
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionJarakApar = null
    }

    interface OnOptionJarakApar {
        fun onOptionChosen(kategori: String?)
    }
}