package com.aksantara.simk3.view.splashscreen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.preference.PreferenceManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivitySplashScreenBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SplashScreenActivity : AppCompatActivity() {

    private val timeOut: Long = 2000
    private lateinit var binding: ActivitySplashScreenBinding

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var users = Users()
    private var unit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = this.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")
        val firebaseUser = firebaseAuth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseUser != null) {
                if (unit == GUEST){
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    users.isAuthenticated = true
                    users.userId = firebaseUser.uid
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_USER, users)
                    startActivity(intent)
                    finish()
                }
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).apply {
                    if (!getBoolean(OnboardingActivity.COMPLETED_ONBOARDING_PREF_NAME, false)) {
                        startActivity(Intent(this@SplashScreenActivity, OnboardingActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }, timeOut)

    }
}