package com.aksantara.simk3.view.main.inventory.tambahapar.dialogmediaapar

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDialogMediaAparBinding
import com.aksantara.simk3.view.main.inventory.detailinventory.editapar.EditAparFragment
import com.aksantara.simk3.view.main.inventory.tambahapar.TambahAparFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogMediaAparFragment : DialogFragment() {

    private var mView: View? = null
    private var _binding: FragmentDialogMediaAparBinding? = null
    private val binding get() = _binding!!
    private var optionDialogListener: OnOptionDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            //initiate the binding here and pass the root to the dialog view
            _binding = FragmentDialogMediaAparBinding.inflate(layoutInflater).apply {

                btnPilihKategori.setOnClickListener {
                    val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
                    if (checkedRadioButtonId != 1) {
                        var kategori: String? = null
                        when (checkedRadioButtonId) {
                            R.id.apar_air -> kategori = binding.aparAir.text.toString().trim()
                            R.id.apar_busa -> kategori = binding.aparBusa.text.toString().trim()
                            R.id.apar_serbuk_kimia -> kategori = binding.aparSerbukKimia.text.toString().trim()
                            R.id.apar_karbondioksida -> kategori = binding.aparKarbondioksida.text.toString().trim()
                        }
                        optionDialogListener?.onOptionChosen(kategori)
                        dialog?.dismiss()
                    }
                }
            }
            AlertDialog.Builder(this).apply {
                setView(binding.root)
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is TambahAparFragment) {
            val tambahAparFragment = fragment
            this.optionDialogListener = tambahAparFragment.optionDialogListener
        } else if (fragment is EditAparFragment){
            val editAparFragment = fragment
            this.optionDialogListener = editAparFragment.optionDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogListener = null
    }


    interface OnOptionDialogListener {
        fun onOptionChosen(media: String?)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mView = null
    }

}