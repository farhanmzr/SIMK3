package com.aksantara.simk3.view.main.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeAdapter: RecyclerView.Adapter<HomeViewHolder>() {

    private var listInspeksi = ArrayList<Inspeksi>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListInspeksi(laporans: List<Inspeksi>?) {
        if (laporans == null) return
        this.listInspeksi.clear()
        this.listInspeksi.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemRvInspeksi =
            ItemRvLaporanInspeksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(itemRvInspeksi)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val kegiatans = listInspeksi[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listInspeksi.size

}