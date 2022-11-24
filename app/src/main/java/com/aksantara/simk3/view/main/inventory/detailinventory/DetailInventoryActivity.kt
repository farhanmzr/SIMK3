package com.aksantara.simk3.view.main.inventory.detailinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivityDetailInventoryBinding
import com.aksantara.simk3.databinding.ActivityLaporanInspeksiBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.home.detaillaporaninspeksi.DetailLaporanInspeksiFragment
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiActivity
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailInventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailInventoryBinding
    private lateinit var aparData: Apar

    companion object {

        const val EXTRA_APAR = "extra_apar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(EXTRA_APAR)) {
            aparData = intent.getParcelableExtra<Apar>(EXTRA_APAR) as Apar
            val mDetailInventory = DetailInventoryFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(DetailInventoryFragment.EXTRA_APAR_DATA, aparData)
            mDetailInventory.arguments = mBundle
            Log.e("data", aparData.toString())
            setCurrentFragment(mDetailInventory)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.host_detail_inventory_activity, fragment)
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