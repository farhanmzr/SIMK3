package com.aksantara.simk3.view.main.inventory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvDataAparBinding
import com.aksantara.simk3.models.Apar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InventoryAdapter: RecyclerView.Adapter<InventoryViewHolder>() {

    private var listApar = ArrayList<Apar>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListApar(laporans: List<Apar>?) {
        if (laporans == null) return
        this.listApar.clear()
        this.listApar.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val itemRvApar =
            ItemRvDataAparBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(itemRvApar)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val apars = listApar[position]
        holder.bind(apars)
    }

    override fun getItemCount(): Int = listApar.size

}