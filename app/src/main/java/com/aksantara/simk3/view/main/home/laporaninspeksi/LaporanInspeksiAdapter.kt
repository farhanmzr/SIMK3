package com.aksantara.simk3.view.main.home.laporaninspeksi

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanInspeksiAdapter: RecyclerView.Adapter<LaporanInspeksiViewHolder>() {

    private var listInspeksi = ArrayList<Inspeksi>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListInspeksi(laporans: List<Inspeksi>?) {
        if (laporans == null) return
        this.listInspeksi.clear()
        this.listInspeksi.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanInspeksiViewHolder {
        val itemRvInspeksi =
            ItemRvLaporanInspeksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaporanInspeksiViewHolder(itemRvInspeksi)
    }

    override fun onBindViewHolder(holder: LaporanInspeksiViewHolder, position: Int) {
        val kegiatans = listInspeksi[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listInspeksi.size

}