package com.aksantara.simk3.view.main.profile.daftarunit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvDaftarUnitBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenClickCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DaftarUnitAdapter(private val mDaftarUnitClickCallback : DaftarUnitClickCallback): RecyclerView.Adapter<DaftarUnitViewHolder>() {

    private var listDepartemen = ArrayList<Departemen>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListDepartemen(laporans: List<Departemen>?) {
        if (laporans == null) return
        this.listDepartemen.clear()
        this.listDepartemen.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarUnitViewHolder {
        val itemRvDepartemen =
            ItemRvDaftarUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaftarUnitViewHolder(mDaftarUnitClickCallback,itemRvDepartemen)
    }

    override fun onBindViewHolder(holder: DaftarUnitViewHolder, position: Int) {
        val kegiatans = listDepartemen[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listDepartemen.size

}