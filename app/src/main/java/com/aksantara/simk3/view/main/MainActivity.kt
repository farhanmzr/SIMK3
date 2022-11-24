package com.aksantara.simk3.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils.replace
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivityMainBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.ID_DEPARTEMEN_P2K3
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.home.HomeFragment
import com.aksantara.simk3.view.main.inventory.InventoryFragment
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryFragment
import com.aksantara.simk3.view.main.profile.ProfileFragment
import com.aksantara.simk3.view.main.remainder.RemainderFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExit = false

    private lateinit var usersData: Users
    private lateinit var aparData: Apar
    private lateinit var departemenData: Departemen
    private val mainViewModel: MainViewModel by viewModels()

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val HOME_FRAGMENT_TAG = "home_fragment_tag"
        const val INVENTORY_FRAGMENT_TAG = "inventory_fragment_tag"
        const val REMAINDER_FRAGMENT_TAG = "remainder_fragment_tag"
        const val PROFILE_FRAGMENT_TAG = "profile_fragment_tag"
        const val CHILD_FRAGMENT = "child_fragment"

        const val EXTRA_USER = "extra_user"
        const val EXTRA_APAR = "extra_apar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val inventoryFragment = InventoryFragment()
        val remainderFragment = RemainderFragment()
        val profileFragment = ProfileFragment()

        if (intent.hasExtra(EXTRA_USER)){
            usersData = intent.getParcelableExtra<Users>(EXTRA_USER) as Users
            mainViewModel.setUserProfile(usersData.userId.toString()).observe(this) { userProfile ->
                if (userProfile != null) {
                    usersData = userProfile
                    setDepartemenData(usersData.departemenId.toString())
                }
            }
        } else {
            Log.e("sethome", "guest")
            setDepartemenData(ID_DEPARTEMEN_P2K3)
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    setCurrentFragment(homeFragment, HOME_FRAGMENT_TAG)
                }
                R.id.inventory -> {
                    setCurrentFragment(inventoryFragment, INVENTORY_FRAGMENT_TAG)
                }
                R.id.remainder -> {
                    setCurrentFragment(remainderFragment, REMAINDER_FRAGMENT_TAG)
                }
                R.id.profile -> {
                    setCurrentFragment(profileFragment, PROFILE_FRAGMENT_TAG)
                }
            }
            true
        }
    }

    private fun setDepartemenData(departemenId: String) {
        mainViewModel.setDepartemenData(departemenId).observe(this) { dataDepartemen ->
            if (dataDepartemen != null) {
                departemenData = dataDepartemen
                setCurrentFragment(HomeFragment(), HOME_FRAGMENT_TAG)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager.commit {
            replace(R.id.host_fragment_activity_main, fragment, fragmentTag)
        }
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            super.onBackPressed()
            return
        } else if (supportFragmentManager.findFragmentByTag(CHILD_FRAGMENT) != null) {
            super.onBackPressed()
            binding.bottomNavigation.visibility = View.VISIBLE
            return
        }

        this.doubleBackToExit = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExit = false }, 2000)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {

        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}