package com.aksantara.simk3.view.main.profile.daftarunit

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvDaftarUnitBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.utils.AppConstants.P2K3
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DaftarUnitViewHolder(private val mDaftarUnitClickCallback: DaftarUnitClickCallback, private val binding: ItemRvDaftarUnitBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Departemen) {
        with(binding) {

            if (data.nama == P2K3){
                linearLayout.visibility = View.GONE
            }
            tvNamaUnit.text = data.nama
            icDelete.setOnClickListener {
                mDaftarUnitClickCallback.onDeleteClicked(data)
            }
            icEdit.setOnClickListener {
                mDaftarUnitClickCallback.onEditClicked(data)
            }

            with(itemView) {
                setOnClickListener {

                }
            }

        }
    }
}