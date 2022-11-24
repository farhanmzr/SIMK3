package com.aksantara.simk3.view.main.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ItemRvLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.BAIK
import com.aksantara.simk3.utils.AppConstants.KURANG_BAIK
import com.aksantara.simk3.utils.AppConstants.SEMPURNA
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.DetailLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeViewHolder(private val binding: ItemRvLaporanInspeksiBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Inspeksi) {
        with(binding) {

            imgProfile.loadImage(data.userPicture)
            tvNama.text = data.namaPetugas
            tvLokasiApar.text = data.lokasiApar
            tvKondisiApar.text = data.statusKondisiApar
            tvJenisApar.text = data.jenisApar
            tvDate.text = data.createdAt

            when (data.statusKondisiApar) {
                SEMPURNA -> {
                    linearKondisi.setBackgroundResource(R.drawable.bg_alpha_green)
                    tvKondisiApar.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTxtGreen))
                }
                BAIK -> {
                    linearKondisi.setBackgroundResource(R.drawable.bg_yellow)
                    tvKondisiApar.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTxtYellow))
                }
                KURANG_BAIK -> {
                    linearKondisi.setBackgroundResource(R.drawable.bg_orange)
                    tvKondisiApar.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTxtOrange))
                }
                else -> {
                    linearKondisi.setBackgroundResource(R.drawable.bg_red)
                    tvKondisiApar.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorRedSnackbar))
                }
            }

            with(itemView) {
                setOnClickListener {
                    val intent =
                        Intent(context, LaporanInspeksiActivity::class.java)
                    intent.putExtra(LaporanInspeksiActivity.EXTRA_INSPEKSI, data)
                    context.startActivity(intent)
                }
            }

        }
    }
}