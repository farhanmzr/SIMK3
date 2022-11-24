package com.aksantara.simk3.view.main.home.scan.inspeksi.dialogsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogSheetInspeksiBinding
import com.aksantara.simk3.utils.AppConstants.JARAK_TANDA
import com.aksantara.simk3.utils.AppConstants.KURANG_125CM
import com.aksantara.simk3.utils.AppConstants.LEBIH_125CM
import com.aksantara.simk3.utils.AppConstants.PAS_125_CM
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.editlaporaninspeksi.EditLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogJarakTandaFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogSheetInspeksiBinding

    private var kategori: String? = null

    private var optionJarakTanda: OnOptionJarakTanda? = null

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
            rbChoice3.visibility = View.VISIBLE
            imgCategoryInspeksi.setBackgroundResource(R.drawable.img_jarak_tanda)
            tvTitleInspeksi.text = JARAK_TANDA
            rbChoice1.text = KURANG_125CM
            rbChoice2.text = PAS_125_CM
            rbChoice3.text = LEBIH_125CM
        }

        binding.btnLanjutkan.setOnClickListener {
            val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
            if (checkedRadioButtonId != 1) {
                when (checkedRadioButtonId) {
                    R.id.rb_choice_1 -> kategori = binding.rbChoice1.text.toString().trim()
                    R.id.rb_choice_2 -> kategori = binding.rbChoice2.text.toString().trim()
                    R.id.rb_choice_3 -> kategori = binding.rbChoice3.text.toString().trim()
                }
                optionJarakTanda?.onOptionChosen(kategori)
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is InspeksiFragment) {
            val inspeksi = fragment
            this.optionJarakTanda = inspeksi.optionJarakTanda
        } else if (fragment is EditLaporanInspeksiFragment) {
            val inspeksi = fragment
            this.optionJarakTanda = inspeksi.optionJarakTanda
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionJarakTanda = null
    }

    interface OnOptionJarakTanda {
        fun onOptionChosen(kategori: String?)
    }
}