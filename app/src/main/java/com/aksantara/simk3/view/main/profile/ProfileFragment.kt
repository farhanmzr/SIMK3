package com.aksantara.simk3.view.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentProfileBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.PREFS_NAME
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.aksantara.simk3.view.main.profile.changepassword.ChangePasswordFragment
import com.aksantara.simk3.view.main.profile.daftarunit.DaftarUnitFragment
import com.aksantara.simk3.view.main.profile.editakun.EditAkunFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private var unit: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE
        checkState()
    }

    private fun checkState() {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")

        when (unit) {
            GUEST -> {
                showViewGuest()
            }
            P2K3 -> {
                showViewP2K3()
            }
            else -> {
                showViewUnit()
            }
        }
    }

    private fun showViewUnit() {
        getUserData()
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    initView(usersDataProfile)
                }
            }
    }

    private fun initView(usersDataProfile: Users) {
        binding.apply {
            //visible
            cardViewUser.visibility = View.VISIBLE
            linearUser.visibility = View.VISIBLE
            if (unit == P2K3){
                btnChangeUnit.visibility = View.VISIBLE
            }
            linearGuest.visibility = View.GONE

            imgProfile.loadImage(usersDataProfile.userPicture)
            tvNama.text = usersDataProfile.nama
            tvEmail.text = usersDataProfile.email
            tvRole.text = usersDataProfile.departemen

            btnChangeUnit.setOnClickListener {
                val mDaftarUnit = DaftarUnitFragment()
                setCurrentFragment(mDaftarUnit)
            }
            btnEditAkun.setOnClickListener {
                val mEditAkunFragment = EditAkunFragment()
                setCurrentFragment(mEditAkunFragment)
            }
            btnLogout.setOnClickListener {
                val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            btnChangePassword.setOnClickListener {
                val mChangePasswordFragment = ChangePasswordFragment()
                setCurrentFragment(mChangePasswordFragment)
            }
        }
    }

    private fun showViewP2K3() {
        getUserData()
    }

    private fun showViewGuest() {
        binding.apply {
            linearGuest.visibility = View.VISIBLE
            cardViewUser.visibility = View.GONE
            linearUser.visibility = View.GONE
            btnLogout.setOnClickListener {
                val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                val user: FirebaseUser? = firebaseAuth.currentUser
                firebaseAuth.signOut()
                user?.delete()
                val intent =
                    Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            btnLogin.setOnClickListener {
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
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

}