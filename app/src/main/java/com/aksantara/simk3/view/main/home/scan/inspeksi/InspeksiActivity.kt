package com.aksantara.simk3.view.main.home.scan.inspeksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivityInspeksiBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InspeksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInspeksiBinding

    private val inspeksiViewModel: InspeksiViewModel by viewModels()
    private lateinit var aparData: Apar
    private lateinit var usersDataProfile: Users

    companion object {

        const val EXTRA_APAR = "extra_apar"
        const val EXTRA_USER = "extra_user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspeksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EXTRA_APAR) && intent.hasExtra(EXTRA_USER)){
            aparData = intent.getParcelableExtra<Apar>(EXTRA_APAR) as Apar
            usersDataProfile = intent.getParcelableExtra<Users>(EXTRA_USER) as Users
            inspeksiViewModel.setAparData(aparData.aparId.toString()).observe(this) { apars ->
                if (apars != null) {
                    aparData = apars
                    val mInspeksi = InspeksiFragment()
                    val mBundle = Bundle()
                    mBundle.putParcelable(InspeksiFragment.EXTRA_APAR_DATA, aparData)
                    mBundle.putParcelable(InspeksiFragment.EXTRA_USER, usersDataProfile)
                    mInspeksi.arguments = mBundle
                    setCurrentFragment(mInspeksi)
                }
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.host_inspeksi_activity, fragment)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}