package com.aksantara.simk3.view.main.inventory

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
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
import com.aksantara.simk3.databinding.FragmentInventoryBinding
import com.aksantara.simk3.models.Apar
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.ID_DEPARTEMEN_P2K3
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.PREFS_NAME
import com.aksantara.simk3.utils.AppConstants.SEMUA_UNIT
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.utils.loadImage
import com.aksantara.simk3.view.login.dialogdepartemen.PopupDepartemenFragment
import com.aksantara.simk3.view.main.MainActivity
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.HomeFragment
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryFragment
import com.aksantara.simk3.view.main.inventory.dialogfilter.DialogFilterUnitFragment
import com.aksantara.simk3.view.main.inventory.tambahapar.TambahAparFragment
import com.aksantara.simk3.view.main.listkadaluwarsa.ListAparKadaluwarsaFragment
import com.aksantara.simk3.view.main.listkurangbaik.ListAparKurangBaikFragment
import com.aksantara.simk3.view.main.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InventoryFragment : Fragment() {

    private lateinit var binding : FragmentInventoryBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val inventoryAdapter = InventoryAdapter()

    private var unit: String? = null
    private var departemenId: String? = null
    private var totalApar: Int? = null

    private lateinit var usersDataProfile: Users
    private lateinit var departemenData: Departemen

    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        progressDialog.show()
        checkState()
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
                showViewP2K3()
            }
            else -> {
                showViewUnit()
            }
        }
    }

    private fun showViewUnit() {
        getUserData()
        binding.apply {
            linearUnit.visibility = View.VISIBLE
            imgProfile.visibility = View.VISIBLE
            imgProfile.setOnClickListener {
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.profile
                setCurrentFragment(ProfileFragment())
            }
            btnAddApar.setOnClickListener {
                gotoAddApar()
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
        showDataAparRv(departemenId.toString(), unit.toString())
    }

    private fun gotoAddApar() {
        val mTambahApar = TambahAparFragment()
        val mBundle = Bundle()
        mBundle.putString(TambahAparFragment.DEPARTEMEN_ID, departemenData.departemenId)
        mBundle.putInt(TambahAparFragment.TOTAL_APAR, departemenData.totalApar!!)
        mTambahApar.arguments = mBundle
        setCurrentFragment(mTambahApar)
    }

    private fun showDataAparRv(departemenId: String, departemenNama: String) {
        showProgressBar(true)
        mainViewModel.getListDataApar(departemenId, departemenNama)
            .observe(viewLifecycleOwner) { dataLaporan ->
                if (dataLaporan != null && dataLaporan.isNotEmpty()) {
                    showProgressBar(false)
                    binding.rvDataApar.visibility = View.VISIBLE
                    binding.linearEmptyApar.visibility = View.GONE
                    inventoryAdapter.setListApar(dataLaporan)
                    setAparAdapter()
                } else {
                    showProgressBar(false)
                    binding.rvDataApar.visibility = View.GONE
                    binding.linearEmptyApar.visibility = View.VISIBLE
                }
            }
    }

    private fun setAparAdapter() {
        with(binding.rvDataApar) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = inventoryAdapter
        }
    }

    private fun getUserData() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    initView(usersDataProfile)
                    getDataDepartemen()
                }
            }
    }

    private fun getDataDepartemen() {
        mainViewModel.getDepartemenData()
            .observe(viewLifecycleOwner) { data ->
                if (data != null) {
                    departemenData = data
                    progressDialog.dismiss()
                    binding.tvTotalAparTersedia.text = departemenData.totalApar.toString()
                    binding.tvTotalAparKadaluwarsa.text = departemenData.totalAparKadaluwarsa.toString()
                    binding.tvTotalAparKurangBagus.text = departemenData.totalAparKurangBagus.toString()
                }
        }
    }

    private fun initView(usersDataProfile: Users) {
        binding.apply {
            imgProfile.loadImage(usersDataProfile.userPicture)
            tvCurrentUnit.text = usersDataProfile.departemen
        }
    }

    private fun showViewP2K3() {
        getUserData()
        binding.apply {
            linearP2K3.visibility = View.VISIBLE
            imgProfile.visibility = View.VISIBLE
            btnAddApar.setOnClickListener {
                gotoAddApar()
            }
            imgProfile.setOnClickListener {
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.selectedItemId = R.id.profile
                setCurrentFragment(ProfileFragment())
            }
            tvUnit.text = SEMUA_UNIT
            linearP2K3.setOnClickListener {
                openDialogDepartemen()
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
        showDataAparRv(departemenId.toString(), P2K3)
    }

    internal var optionDialogListener: DialogFilterUnitFragment.OnOptionDialogListener =
        object : DialogFilterUnitFragment.OnOptionDialogListener {
            override fun onOptionChosen(category: String?, id: String?) {
                progressDialog.show()
                binding.tvUnit.text = category.toString()
                unit = if (category == SEMUA_UNIT){
                    P2K3
                } else {
                    category
                }
                mainViewModel.setDepartemenData(id.toString()).observe(viewLifecycleOwner) { dataDepartemen ->
                    if (dataDepartemen != null) {
                        departemenData = dataDepartemen
                        getDataDepartemen()
                        departemenId = id
                        showDataAparRv(departemenId.toString(), unit.toString())
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
        }
    }

    private fun openDialogDepartemen() {
        val mPopupDepartemen = DialogFilterUnitFragment()
        val mFragmentManager = childFragmentManager
        mPopupDepartemen.show(
            mFragmentManager,
            mPopupDepartemen::class.java.simpleName
        )
    }

    private fun showViewGuest() {
        binding.apply {
            linearGuest.visibility = View.VISIBLE
            btnAddApar.visibility = View.GONE
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
        getDataDepartemen()
        showDataAparRv(ID_DEPARTEMEN_P2K3, P2K3)
    }

    override fun onResume() {
        super.onResume()
        if (unit == GUEST){
            Log.e("guest", "guest inventory")
            mainViewModel.setDepartemenData(ID_DEPARTEMEN_P2K3).observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null) {
                    departemenData = dataDepartemen
                    getDataDepartemen()
                    showDataAparRv(ID_DEPARTEMEN_P2K3, P2K3)
                }
            }
        } else {
            mainViewModel.setDepartemenData(departemenId.toString()).observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null) {
                    departemenData = dataDepartemen
                    getDataDepartemen()
                    showDataAparRv(departemenId.toString(), unit.toString())
                }
            }
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