package com.aksantara.simk3.view.main.listkurangbaik

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentListAparKadaluwarsaBinding
import com.aksantara.simk3.databinding.FragmentListAparKurangBaikBinding
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.inventory.InventoryAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListAparKurangBaikFragment : Fragment() {

    private lateinit var binding : FragmentListAparKurangBaikBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val inventoryAdapter = InventoryAdapter()

    private var departemenId: String? = null
    private var departemenNama: String? = null

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAMA = "extra_nama"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListAparKurangBaikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        binding.icBack.setOnClickListener {
            bottomNav.visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        if (arguments != null) {
            departemenId = requireArguments().getString(EXTRA_ID)!!
            departemenNama = requireArguments().getString(EXTRA_NAMA)!!
            showDataAparRv(departemenId.toString(), departemenNama.toString())
        }
    }

    private fun showDataAparRv(departemenId: String, departemenNama: String) {
        showProgressBar(true)
        mainViewModel.getListDataAparKurangBaik(departemenId, departemenNama)
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

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}
