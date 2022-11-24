package com.aksantara.simk3.view.onboarding

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.ActivityOnboardingBinding
import com.aksantara.simk3.models.Onboarding
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.login.LoginViewModel
import com.aksantara.simk3.view.main.MainActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var progressDialog : Dialog

    private val loginViewModel: LoginViewModel by viewModels()

    private val onBoardingViewPagerAdapter = OnboardingAdapter(
        listOf(
            Onboarding(
                R.drawable.img_onboarding_1,
                "Inspeksi APAR\nSemakin Mudah",
                "Dengan Scan QR, anda dapat melakukan inspeksi APAR di Departemen anda dengan mudah."
            ),
            Onboarding(
                R.drawable.img_onboarding_2,
                "Lihat Kondisi APAR\ndengan Detail",
                "Anda dapat melihat berbagai informasi dan kondisi APAR terbaru dengan mudah dan detail."
            ),
            Onboarding(
                R.drawable.img_onboarding_3,
                "Pantau Laporan \nInspeksi APAR",
                "Anda dapat memantau hasil dari inpeksi APAR dengan mudah dan detail."
            )
        )
    )

    private val indicators = arrayOfNulls<ImageView>(3)

    companion object {
        const val COMPLETED_ONBOARDING_PREF_NAME = "completed_onboarding_pref_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialogHelper.progressDialog(this)

        setupOnboardingIndicator()
        setCurrentIndicator(0)
        setViewpagerAdapter()

    }

    private fun setupOnboardingIndicator() {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_onboarding_indicator_inactive
                )
            )
            indicators[i]!!.layoutParams = layoutParams
            binding.layoutOnboardingIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {

        for (i in indicators.indices) {
            val imageView: ImageView = binding.layoutOnboardingIndicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_onboarding_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_onboarding_indicator_inactive
                    )
                )
            }
        }
    }

    private fun setViewpagerAdapter() {

        binding.onBoardingViewPager.adapter = onBoardingViewPagerAdapter

        binding.onBoardingViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                    if (position == onBoardingViewPagerAdapter.itemCount - 1) {
                        binding.apply {
                            btnNext.text = getString(R.string.mulai_sekarang)
                            btnNext.setOnClickListener {
                                PreferenceManager.getDefaultSharedPreferences(this@OnboardingActivity).edit().apply {
                                    putBoolean(COMPLETED_ONBOARDING_PREF_NAME, true)
                                    apply()
                                }
                                val intent =
                                    Intent(this@OnboardingActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            linear.setBackgroundResource(R.drawable.bg_neutral_outline)
                            tvSkip.text = getString(R.string.masuk_sebagai_tamu)
                            tvSkip.setOnClickListener {
                                PreferenceManager.getDefaultSharedPreferences(this@OnboardingActivity).edit().apply {
                                    putBoolean(COMPLETED_ONBOARDING_PREF_NAME, true)
                                    apply()
                                }
                                progressDialog.show()
                                loginViewModel.signInWithAnonym()
                                    .observe(this@OnboardingActivity) { statusLogin ->
                                        if (statusLogin == AppConstants.STATUS_SUCCESS) {
                                            // Login sukses, masuk ke Main Activity
                                            val preferences = getSharedPreferences(
                                                AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
                                            val editor = preferences.edit()
                                            editor.putString("unit", AppConstants.GUEST)
                                            editor.apply()
                                            progressDialog.dismiss()
                                            val intent =
                                                Intent(this@OnboardingActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            progressDialog.dismiss()
                                            Toast.makeText(this@OnboardingActivity, statusLogin, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    } else if(position == onBoardingViewPagerAdapter.itemCount - 2) {
                        binding.apply {
                            btnNext.text = getString(R.string.selanjutnya)
                            btnNext.setOnClickListener {
                                binding.onBoardingViewPager.currentItem = position + 1
                            }
                            linear.setBackgroundResource(R.drawable.bg_null)
                            tvSkip.text = getString(R.string.lewati)
                            tvSkip.setOnClickListener {
                                binding.onBoardingViewPager.currentItem = position + 1
                            }
                        }
                    } else {
                        binding.apply {
                            btnNext.text = getString(R.string.selanjutnya)
                            btnNext.setOnClickListener {
                                binding.onBoardingViewPager.currentItem = position + 1
                            }
                            linear.setBackgroundResource(R.drawable.bg_null)
                            tvSkip.text = getString(R.string.lewati)
                            tvSkip.setOnClickListener {
                                binding.onBoardingViewPager.currentItem = position + 2
                            }
                        }
                    }
                }
            }
        )
    }



}