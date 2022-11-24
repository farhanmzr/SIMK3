package com.aksantara.simk3.view.main.home.scan

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.aksantara.simk3.R
import com.aksantara.simk3.databinding.FragmentScanQrBinding
import com.aksantara.simk3.models.Users
import com.aksantara.simk3.utils.ProgressDialogHelper
import com.aksantara.simk3.view.main.MainViewModel
import com.aksantara.simk3.view.main.home.HomeFragment
import com.aksantara.simk3.view.main.home.scan.dialogdetailapar.DialogDetailAparFragment
import com.aksantara.simk3.view.main.home.scan.inspeksi.InspeksiFragment
import com.aksantara.simk3.view.main.inventory.detailinventory.DetailInventoryFragment
import com.aksantara.simk3.view.main.inventory.dialogfilter.DialogFilterUnitFragment
import com.aksantara.simk3.view.main.inventory.tambahapar.TambahAparFragment
import com.aksantara.simk3.view.main.inventory.tambahapar.dialogmediaapar.DialogMediaAparFragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ScanQrFragment : Fragment() {

    private lateinit var binding : FragmentScanQrBinding

    private lateinit var codeScanner: CodeScanner

    private lateinit var progressDialog : Dialog

    companion object {
        private const val CAMERA_REQ = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btmNavbar: BottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        btmNavbar.visibility = View.GONE
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())


        setupPermissions()
        codeScanner()

    }

    private fun codeScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scn)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                activity?.runOnUiThread {
                    val mHomeFragment = HomeFragment()
                    val mBundle = Bundle()
                    mBundle.putString(HomeFragment.EXTRA_ID_APAR, it.text)
                    mHomeFragment.arguments = mBundle
                    setCurrentFragment(mHomeFragment)
                }
            }

            errorCallback = ErrorCallback {
                activity?.runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            binding.scn.setOnClickListener {
                codeScanner.startPreview()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQ -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        requireContext(),
                        "You need the camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment)
        }
    }

}