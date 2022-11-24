package com.aksantara.simk3.view.main.home.laporaninspeksi

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentLaporanInspeksiBinding
import com.aksantara.simk3.models.Inspeksi
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.AppConstants
import com.aksantara.simk3.utils.AppConstants.GUEST
import com.aksantara.simk3.utils.AppConstants.P2K3
import com.aksantara.simk3.utils.AppConstants.SEMUA_KONDISI
import com.aksantara.simk3.utils.AppConstants.SEMUA_MEDIA
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.laporaninspeksi.dialogfilterinspeksi.DialogFilterInspeksiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanInspeksiFragment : Fragment() {

    private lateinit var binding : FragmentLaporanInspeksiBinding

    private val laporanInspeksiViewModel: LaporanInspeksiViewModel by activityViewModels()
    private val laporanInspeksiAdapter = LaporanInspeksiAdapter()
    private lateinit var progressDialog : Dialog

    private var unit: String? = null
    private var departemenId: String? = null

    private var filterJenis: String? = null
    private var filterKondisi: String? = null

    private lateinit var inspeksiData: Inspeksi
    private lateinit var usersDataProfile: Users

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLaporanInspeksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        binding.apply {
            icBack.setOnClickListener {
                activity?.onBackPressed()
            }
            btnFilter.setOnClickListener {
                val mDialogFilterInspeksi = DialogFilterInspeksiFragment()
                val mFragmentManager = childFragmentManager
                mDialogFilterInspeksi.show(
                    mFragmentManager,
                    mDialogFilterInspeksi::class.java.simpleName
                )
            }
        }

        checkState()

    }

    private fun checkState() {
        val preferences = requireContext().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        unit = preferences.getString("unit", "none")
        departemenId = preferences.getString("departemenId", "none")

        filterJenis = SEMUA_MEDIA
        filterKondisi = SEMUA_KONDISI

        showDataInspeksiRv(unit.toString(), departemenId.toString(), filterJenis.toString(), filterKondisi.toString())
    }

    private fun showDataInspeksiRv(unit: String, departemenId: String, filterJenis: String, filterKondisi: String) {
        showProgressBar(true)
        laporanInspeksiViewModel.getListInspeksiData(unit, departemenId, filterJenis, filterKondisi)
            .observe(viewLifecycleOwner) { dataLaporan ->
                if (dataLaporan != null && dataLaporan.isNotEmpty()) {
                    showProgressBar(false)
                    binding.rvLaporanInspeksi.visibility = View.VISIBLE
                    binding.linearEmptyInspeksi.visibility = View.GONE
                    laporanInspeksiAdapter.setListInspeksi(dataLaporan)
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
            adapter = laporanInspeksiAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    internal var optionDialogListener: DialogFilterInspeksiFragment.OnOptionDialogListener =
        object : DialogFilterInspeksiFragment.OnOptionDialogListener {
            override fun onOptionChosenP2K3(
                media: String?,
                kondisi: String?,
                departemen: String?,
                departemenId: String?
            ) {
                Log.e(departemen, "departemen")
                Log.e(departemenId, "departemenId")
                showDataInspeksiRv(departemen.toString(), departemenId.toString(), media.toString(), kondisi.toString())
            }

            override fun onOptionChosen(media: String?,
                                        kondisi: String?) {
                showDataInspeksiRv(unit.toString(), departemenId.toString(), media.toString(), kondisi.toString())
            }
        }
}