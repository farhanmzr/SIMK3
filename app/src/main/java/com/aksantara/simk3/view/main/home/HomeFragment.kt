package com.aksantara.simk3.view.main.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentHomeBinding
import com.aksantara.simk3.databinding.ItemDialogGuestLoginBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.PREFS_NAME
import com.aksantara.simk3.utils.AppConstants.TEXT_HOME_GUEST
import com.aksantara.simk3.utils.AppConstants.TEXT_HOME_USER
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.login.LoginActivity
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiActivity
import com.aksantara.simk3.view.main.home.laporaninspeksi.LaporanInspeksiAdapter
import com.aksantara.simk3.view.main.home.scan.ScanQrFragment
import com.aksantara.simk3.view.main.home.scan.dialogdetailapar.DialogDetailAparFragment
import com.aksantara.simk3.view.main.inventory.detailinventory.editapar.EditAparFragment
import com.aksantara.simk3.view.main.listkadaluwarsa.ListAparKadaluwarsaFragment
import com.aksantara.simk3.view.main.listkurangbaik.ListAparKurangBaikFragment
import com.aksantara.simk3.view.main.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeAdapter = HomeAdapter()

    private lateinit var usersDataProfile: Users
    private lateinit var departemenData: Departemen

    private var unit: String? = null
    private var departemenId: String? = null

    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_ID_APAR = "extra_id_apar"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        checkState()

        if (arguments != null) {
            mainViewModel.getUserData()
                .observe(viewLifecycleOwner) { userProfile ->
                    if (userProfile != null) {
                        usersDataProfile = userProfile
                        val aparId = requireArguments().getString(EXTRA_ID_APAR)!!
                        val mDialogDetailApar = DialogDetailAparFragment()
                        val mBundle = Bundle()
                        mBundle.putString(DialogDetailAparFragment.EXTRA_APAR_DATA, aparId)
                        mBundle.putParcelable(DialogDetailAparFragment.EXTRA_USER, usersDataProfile)
                        mDialogDetailApar.arguments = mBundle
                        val mFragmentManager = childFragmentManager
                        mDialogDetailApar.show(
                            mFragmentManager,
                            mDialogDetailApar::class.java.simpleName
                        )
                        val btmNavbar: BottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
                        btmNavbar.visibility = View.VISIBLE
                    }
            }
        }
    }

    private fun checkState() {
        val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")
        departemenId = preferences.getString("departemenId", "none")

        when (unit) {
            GUEST -> {
                showViewGuest()
            }
            P2K3 -> {
                getUserData()
            }
            else -> {
                getUserData()
            }
        }
    }

    private fun showViewGuest() {
        getDataDepartemen()
        binding.apply {
            tvTitle.text = TEXT_HOME_GUEST
            linearGuest.visibility = View.VISIBLE
            linearUser.visibility = View.GONE
            tvLaporanInspeksi.visibility = View.GONE
            tvLihatLaporanSemua.visibility = View.GONE
            rvLaporanInspeksi.visibility = View.GONE
            linearGuest.setOnClickListener {
                val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
                val user: FirebaseUser? = firebaseAuth.currentUser
                firebaseAuth.signOut()
                user?.delete()
                val intent =
                    Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            btnScan.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val binding: ItemDialogGuestLoginBinding =
                    ItemDialogGuestLoginBinding.inflate(LayoutInflater.from(context))
                builder.setView(binding.root)
                val dialog = builder.create()
                dialog.show()
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
            linearAparKadaluwarsa.setOnClickListener {
                val mListKadaluwarsa = ListAparKadaluwarsaFragment()
                val mBundle = Bundle()
                mBundle.putString(ListAparKadaluwarsaFragment.EXTRA_ID, departemenId.toString())
                mBundle.putString(ListAparKadaluwarsaFragment.EXTRA_NAMA, unit.toString())
                mListKadaluwarsa.arguments = mBundle
                setCurrentFragment(mListKadaluwarsa)
            }
            linearAparKurangBagus.setOnClickListener {
                val mListKurangBaik = ListAparKurangBaikFragment()
                val mBundle = Bundle()
                mBundle.putString(ListAparKurangBaikFragment.EXTRA_ID, departemenId.toString())
                mBundle.putString(ListAparKurangBaikFragment.EXTRA_NAMA, unit.toString())
                mListKurangBaik.arguments = mBundle
                setCurrentFragment(mListKurangBaik)
            }
        }
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    initView(usersDataProfile)
                    getDataDepartemen()
                    setDataLaporanInspeksiRV(unit, departemenId)
                }
        }
    }

    private fun getDataDepartemen() {
        mainViewModel.getDepartemenData()
            .observe(viewLifecycleOwner) { data ->
                if (data != null) {
                    departemenData = data
                    binding.tvTotalAparTersedia.text = departemenData.totalApar.toString()
                    binding.tvTotalAparKadaluwarsa.text = departemenData.totalAparKadaluwarsa.toString()
                    binding.tvTotalAparKurangBagus.text = departemenData.totalAparKurangBagus.toString()
                }
        }
    }

    private fun setDataLaporanInspeksiRV(unit: String?, departemenId: String?) {
        showProgressBar(true)
        mainViewModel.getListInspeksiData(unit.toString(), departemenId.toString())
            .observe(viewLifecycleOwner) { dataLaporan ->
                if (dataLaporan != null && dataLaporan.isNotEmpty()) {
                    showProgressBar(false)
                    binding.rvLaporanInspeksi.visibility = View.VISIBLE
                    binding.linearEmptyInspeksi.visibility = View.GONE
                    homeAdapter.setListInspeksi(dataLaporan)
                    setAparAdapter()
                } else {
                    binding.rvLaporanInspeksi.visibility = View.GONE
                    binding.linearEmptyInspeksi.visibility = View.VISIBLE
                    showProgressBar(false)
                }
        }
    }

    private fun setAparAdapter() {
        with(binding.rvLaporanInspeksi) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = homeAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initView(usersDataProfile: Users) {
        binding.apply {
            tvTitle.text = TEXT_HOME_USER
            linearGuest.visibility = View.GONE
            linearUser.visibility = View.VISIBLE
            tvLaporanInspeksi.visibility = View.VISIBLE
            tvLihatLaporanSemua.visibility = View.VISIBLE
            rvLaporanInspeksi.visibility = View.VISIBLE

            imgProfile.loadImage(usersDataProfile.userPicture)
            tvNama.text = usersDataProfile.nama
            tvEmail.text = usersDataProfile.email
            tvUnit.text = usersDataProfile.departemen
            imgProfile.setOnClickListener {
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.profile
                setCurrentFragment(ProfileFragment())
            }
            btnScan.setOnClickListener {
                setCurrentFragment(ScanQrFragment())
//                val aparId = "UnitDekanatbg0ji5OwoLDdfW71G2pP"
//                val mDialogDetailApar = DialogDetailAparFragment()
//                val mBundle = Bundle()
//                mBundle.putString(DialogDetailAparFragment.EXTRA_APAR_DATA, aparId)
//                mBundle.putParcelable(DialogDetailAparFragment.EXTRA_USER, usersDataProfile)
//                mDialogDetailApar.arguments = mBundle
//                val mFragmentManager = childFragmentManager
//                mDialogDetailApar.show(
//                    mFragmentManager,
//                    mDialogDetailApar::class.java.simpleName
//                )
            }
            tvLihatLaporanSemua.setOnClickListener {
                val intent =
                    Intent(requireActivity(), LaporanInspeksiActivity::class.java)
                startActivity(intent)
            }
            linearAparKadaluwarsa.setOnClickListener {
                val mListKadaluwarsa = ListAparKadaluwarsaFragment()
                val mBundle = Bundle()
                mBundle.putString(ListAparKadaluwarsaFragment.EXTRA_ID, departemenId.toString())
                mBundle.putString(ListAparKadaluwarsaFragment.EXTRA_NAMA, unit.toString())
                mListKadaluwarsa.arguments = mBundle
                setCurrentFragment(mListKadaluwarsa)
            }
            linearAparKurangBagus.setOnClickListener {
                val mListKurangBaik = ListAparKurangBaikFragment()
                val mBundle = Bundle()
                mBundle.putString(ListAparKurangBaikFragment.EXTRA_ID, departemenId.toString())
                mBundle.putString(ListAparKurangBaikFragment.EXTRA_NAMA, unit.toString())
                mListKurangBaik.arguments = mBundle
                setCurrentFragment(mListKurangBaik)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

    override fun onResume() {
        super.onResume()
        if (unit == GUEST){
            Log.e("guest", "guest inventory")
            mainViewModel.setDepartemenData(AppConstants.ID_DEPARTEMEN_P2K3).observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null) {
                    departemenData = dataDepartemen
                    getDataDepartemen()
                }
            }
        } else {
            mainViewModel.setDepartemenData(departemenId.toString()).observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null) {
                    departemenData = dataDepartemen
                    getDataDepartemen()
                }
            }
        }
    }

    internal var optionResultScan: DialogDetailAparFragment.ResultDialogListener =
        object : DialogDetailAparFragment.ResultDialogListener {
            override fun onOptionResultScan(aparData: Apar) {
                val mEditApar = EditAparFragment()
                val mBundle = Bundle()
                mBundle.putParcelable(EditAparFragment.EXTRA_APAR_DATA, aparData)
                mEditApar.arguments = mBundle
                setCurrentFragment(mEditApar)
        }
    }

}