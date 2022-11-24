package com.aksantara.simk3.view.main.inventory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ItemRvDataAparBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryActivity
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InventoryViewHolder(private val binding: ItemRvDataAparBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Apar) {
        with(binding) {

            tvJenisApar.text = data.media
            tvLokasiApar.text = data.lokasiApar
            tvKondisiApar.text = data.statusKondisiTerakhir

            with(itemView) {
                setOnClickListener {
                    val intent =
                        Intent(context, DetailInventoryActivity::class.java)
                    intent.putExtra(DetailInventoryActivity.EXTRA_APAR, data)
                    context.startActivity(intent)
                }
            }

        }
    }
}