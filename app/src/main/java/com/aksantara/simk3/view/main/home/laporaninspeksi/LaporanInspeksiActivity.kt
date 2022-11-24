package com.aksantara.simk3.view.main.home.laporaninspeksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivityInspeksiBinding
import com.aksantara.simk3.databinding.ActivityLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.DetailLaporanInspeksiFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanInspeksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanInspeksiBinding
    private lateinit var inspeksiData: Inspeksi

    companion object {

        const val EXTRA_INSPEKSI = "extra_inspeksi"
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanInspeksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EXTRA_INSPEKSI)){
            inspeksiData = intent.getParcelableExtra<Inspeksi>(EXTRA_INSPEKSI) as Inspeksi
            val mDetailInspeksi = DetailLaporanInspeksiFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(DetailLaporanInspeksiFragment.EXTRA_INSPEKSI_DATA, inspeksiData)
            mDetailInspeksi.arguments = mBundle
            setCurrentFragment(mDetailInspeksi)
        } else {
            setCurrentFragment(LaporanInspeksiFragment())
        }
    }


    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.host_laporan_inspeksi_activity, fragment)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

}