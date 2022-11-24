package com.aksantara.simk3.view.main.profile.daftarunit

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentDaftarUnitBinding
import com.aksantara.simk3.databinding.FragmentInventoryBinding
import com.aksantara.simk3.databinding.ItemDialogHapusAparBinding
import com.aksantara.simk3.models.Departemen
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.scan.dialogdetailapar.DialogDetailAparFragment
import com.aksantara.simk3.view.main.inventory.InventoryAdapter
import com.aksantara.simk3.view.main.profile.daftarunit.dialogaddunit.DialogAddUnit
import com.aksantara.simk3.view.main.profile.daftarunit.dialogeditunit.DialogEditUnitFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DaftarUnitFragment : Fragment(), DaftarUnitClickCallback {

    private lateinit var binding : FragmentDaftarUnitBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val daftarUnitAdapter = DaftarUnitAdapter(this@DaftarUnitFragment)

    private lateinit var progressDialog : Dialog

    private var totalItem: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDaftarUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        setDataDepartemenRv()

        binding.icBack.setOnClickListener {
            bottomNav.visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
        binding.icAddUnit.setOnClickListener {
            val mDialogEditUnit = DialogAddUnit()
            val mBundle = Bundle()
            mBundle.putInt(DialogAddUnit.EXTRA_TOTAL_DATA.toString(), totalItem)
            mDialogEditUnit.arguments = mBundle
            val mFragmentManager = childFragmentManager
            mDialogEditUnit.show(
                mFragmentManager,
                mDialogEditUnit::class.java.simpleName
            )
        }

    }

    private fun setDataDepartemenRv() {
        showProgressBar(true)
        mainViewModel.getListDepartemen()
            .observe(viewLifecycleOwner) { dataDepartemen ->
                if (dataDepartemen != null && dataDepartemen.isNotEmpty()) {
                    showProgressBar(false)
                    daftarUnitAdapter.setListDepartemen(dataDepartemen)
                    setDepartemenAdapter()
                    totalItem = dataDepartemen.size
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setDepartemenAdapter() {
        with(binding.rvDaftarUnit) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = daftarUnitAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onEditClicked(data: Departemen) {
        val mDialogEditUnit = DialogEditUnitFragment()
        val mBundle = Bundle()
        mBundle.putString(DialogEditUnitFragment.EXTRA_DEPARTEMEN_DATA, data.departemenId)
        mDialogEditUnit.arguments = mBundle
        val mFragmentManager = childFragmentManager
        mDialogEditUnit.show(
            mFragmentManager,
            mDialogEditUnit::class.java.simpleName
        )
    }

    override fun onDeleteClicked(data: Departemen) {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogHapusAparBinding =
            ItemDialogHapusAparBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.tvTitle.text = "Hapus Data Departemen"
        binding.tvDesc.text = "Anda yakin ingin menghapus data Departemen ini?"
        binding.btnYa.setOnClickListener {
            progressDialog.show()
            mainViewModel.deleteDepartemen(data.departemenId.toString())
                .observe(viewLifecycleOwner) { status ->
                    if (status == AppConstants.STATUS_SUCCESS) {
                        progressDialog.dismiss()
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Departemen Berhasil Dihapus.", Toast.LENGTH_SHORT).show()
                        setDataDepartemenRv()
                    } else {
                        progressDialog.dismiss()
                        setDataDepartemenRv()
                        Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.btnTidak.setOnClickListener { dialog.dismiss() }
    }
}