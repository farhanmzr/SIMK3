package com.aksantara.simk3.view.main.remainder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentRemainderBinding
import com.aksantara.simk3.models.Reminder
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.PREFS_NAME
import com.aksantara.simk3.utils.AppConstants.REMAINDER_DESC
import com.aksantara.simk3.utils.AppConstants.REMAINDER_TITLE
import com.aksantara.simk3.utils.DateHelper
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class RemainderFragment : Fragment() {

    private lateinit var binding : FragmentRemainderBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val remainderAdapter = RemainderAdapter()
    private var unit: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRemainderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkState()
    }

    private fun checkState() {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        when (unit) {
            GUEST -> {
                binding.linearGuest.visibility = View.VISIBLE
                binding.rvRemainder.visibility = View.GONE
                binding.btnLogin.setOnClickListener {
                    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    firebaseAuth.signOut()
                    user?.delete()
                    val intent =
                        Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            P2K3 -> {
                initView()
            }
            else -> {
                initView()
            }
        }
    }

    private fun initView() {
        binding.apply {
            imgProfile.setOnClickListener {
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.profile
                setCurrentFragment(ProfileFragment())
            }
            rvRemainder.visibility = View.VISIBLE
            linearGuest.visibility = View.GONE
        }
        showRvRemainder()
        checkDate()
    }

    private fun checkDate() {
        val day = DateHelper.getCurrentDateDay()
        val date = DateHelper.getCurrentDate()
        val dateNotSpace = DateHelper.getCurrentDate().filter { !it.isWhitespace() }
        if (day == "10" || day == "11" || day == "12"){
            val reminder = Reminder(
                reminderId = "reminder$dateNotSpace",
                reminderTitle = REMAINDER_TITLE,
                reminderDesc = REMAINDER_DESC,
                reminderDate = date,
                timeStamp = Timestamp(Date())
            )
            uploadDataRemainder(reminder)
        } else {
            Log.e("Tidak tgl diatas", "true")
        }
    }

    private fun uploadDataRemainder(reminder: Reminder) {
        mainViewModel.uploadDataRemainder(reminder).observe(viewLifecycleOwner) { status ->
            if (status != AppConstants.STATUS_SUCCESS) {
                Log.d("UploadRemainder", status)
            }
        }
    }

    private fun showRvRemainder() {
        showProgressBar(true)
        mainViewModel.getListDataReminder()
            .observe(viewLifecycleOwner) { dataReminder ->
                if (dataReminder != null && dataReminder.isNotEmpty()) {
                    showProgressBar(false)
                    binding.rvRemainder.visibility = View.VISIBLE
                    binding.linearEmptyRemainder.visibility = View.GONE
                    remainderAdapter.setListRemainder(dataReminder)
                    setRemainderAdapter()
                } else {
                    binding.rvRemainder.visibility = View.GONE
                    binding.linearEmptyRemainder.visibility = View.VISIBLE
                    showProgressBar(false)
                }
            }
    }

    private fun setRemainderAdapter() {
        with(binding.rvRemainder) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = remainderAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

}