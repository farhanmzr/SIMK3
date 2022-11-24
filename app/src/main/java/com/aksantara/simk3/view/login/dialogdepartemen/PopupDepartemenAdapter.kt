package com.aksantara.simk3.view.login.dialogdepartemen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvDepartemenBinding
import com.aksantara.simk3.models.Departemen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PopupDepartemenAdapter(private val mDialogClickCallback : PopupDepartemenClickCallback) : RecyclerView.Adapter<PopupDepartemenAdapter.PopupDepartemenViewHolder>() {


    private var listDepartemen = ArrayList<Departemen>()

    private var selectedPos: Int = -1

    inner class PopupDepartemenViewHolder(private val mDialogClickCallback : PopupDepartemenClickCallback, private val binding: ItemRvDepartemenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Departemen, position: Int) {
            binding.apply {
                rbSelect.text = (data.nama)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupDepartemenAdapter.PopupDepartemenViewHolder {
        val itemRvDialogKelolaTokoBinding =
            ItemRvDepartemenBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PopupDepartemenViewHolder(mDialogClickCallback, itemRvDialogKelolaTokoBinding)
    }

    override fun onBindViewHolder(holder: PopupDepartemenAdapter.PopupDepartemenViewHolder, position: Int) {
        holder.bind(listDepartemen[position], position)
    }


    override fun getItemCount(): Int = listDepartemen.size


}