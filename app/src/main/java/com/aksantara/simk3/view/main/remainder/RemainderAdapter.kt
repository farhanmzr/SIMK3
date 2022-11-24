package com.aksantara.simk3.view.main.remainder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.databinding.ItemRvRemainderBinding
import com.aksantara.simk3.models.Reminder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RemainderAdapter: RecyclerView.Adapter<RemainderViewHolder>() {

    private var listRemainder = ArrayList<Reminder>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListRemainder(laporans: List<Reminder>?) {
        if (laporans == null) return
        this.listRemainder.clear()
        this.listRemainder.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainderViewHolder {
        val itemRvRemainder =
            ItemRvRemainderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RemainderViewHolder(itemRvRemainder)
    }

    override fun onBindViewHolder(holder: RemainderViewHolder, position: Int) {
        val kegiatans = listRemainder[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listRemainder.size

}