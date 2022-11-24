package com.aksantara.simk3.view.main.inventory.dialogfilter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvDepartemenBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.SEMUA_UNIT
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenClickCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogFilterUnitAdapter(private val mDialogClickCallback : PopupDepartemenClickCallback) : RecyclerView.Adapter<DialogFilterUnitAdapter.PopupDepartemenViewHolder>() {

    private var listDepartemen = ArrayList<Departemen>()

    private var selectedPos: Int = -1

    inner class PopupDepartemenViewHolder(private val mDialogClickCallback : PopupDepartemenClickCallback, private val binding: ItemRvDepartemenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Departemen, position: Int) {
            binding.apply {
                if (data.nama.equals(P2K3)){
                    rbSelect.text = SEMUA_UNIT
                } else {
                    rbSelect.text = (data.nama)
                }

                rbSelect.isChecked = selectedPos == position

                rbSelect.setOnClickListener {
                    if (selectedPos >= 0) notifyItemChanged(selectedPos)
                    selectedPos = adapterPosition
                    notifyItemChanged(selectedPos)
                    notifyDataSetChanged()
                    val value = rbSelect.text.toString()
                    mDialogClickCallback.onItemClicked(value, data.departemenId.toString())
                }
            }
        }
    }


    fun setListDepartemen(departemens: List<Departemen>?) {
        if (departemens == null) return
        this.listDepartemen.clear()
        this.listDepartemen.addAll(departemens)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogFilterUnitAdapter.PopupDepartemenViewHolder {
        val itemRvDialogKelolaTokoBinding =
            ItemRvDepartemenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PopupDepartemenViewHolder(mDialogClickCallback, itemRvDialogKelolaTokoBinding)
    }

    override fun onBindViewHolder(holder: DialogFilterUnitAdapter.PopupDepartemenViewHolder, position: Int) {
        holder.bind(listDepartemen[position], position)
    }


    override fun getItemCount(): Int = listDepartemen.size


}