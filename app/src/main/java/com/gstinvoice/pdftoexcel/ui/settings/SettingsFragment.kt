package com.gstinvoice.pdftoexcel.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gstinvoice.pdftoexcel.R
import com.gstinvoice.pdftoexcel.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupClickListeners()
        displayAppVersion()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupClickListeners() {
        binding.privacyPolicyCard.setOnClickListener {
            openPrivacyPolicy()
        }
        
        binding.restorePurchasesCard.setOnClickListener {
            // Navigate back to home and trigger restore
            findNavController().navigateUp()
        }
    }
    
    private fun displayAppVersion() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            )
            val versionName = packageInfo.versionName
            binding.tvVersionValue.text = versionName
        } catch (e: PackageManager.NameNotFoundException) {
            binding.tvVersionValue.text = getString(R.string.version_number)
        }
    }
    
    private fun openPrivacyPolicy() {
        val privacyUrl = getString(R.string.privacy_policy_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
        
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
