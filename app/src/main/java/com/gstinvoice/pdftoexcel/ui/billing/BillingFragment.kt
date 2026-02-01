package com.gstinvoice.pdftoexcel.ui.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gstinvoice.pdftoexcel.GSTInvoiceApplication
import com.gstinvoice.pdftoexcel.R
import com.gstinvoice.pdftoexcel.databinding.FragmentBillingBinding
import com.gstinvoice.pdftoexcel.utils.BillingManager
import kotlinx.coroutines.launch

class BillingFragment : Fragment() {
    
    private var _binding: FragmentBillingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var billingManager: BillingManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val app = requireActivity().application as GSTInvoiceApplication
        billingManager = app.billingManager
        
        setupToolbar()
        setupClickListeners()
        observeBillingManager()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnPurchase.setOnClickListener {
            billingManager.launchPurchaseFlow(requireActivity())
        }
        
        binding.btnRestorePurchases.setOnClickListener {
            billingManager.restorePurchases()
        }
    }
    
    private fun observeBillingManager() {
        viewLifecycleOwner.lifecycleScope.launch {
            billingManager.billingState.collect { state ->
                when (state) {
                    is BillingManager.BillingState.Idle -> {
                        showLoading(false)
                    }
                    is BillingManager.BillingState.Loading -> {
                        showLoading(true)
                    }
                    is BillingManager.BillingState.Success -> {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            R.string.purchase_success,
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigateUp()
                    }
                    is BillingManager.BillingState.Cancelled -> {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            R.string.purchase_cancelled,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is BillingManager.BillingState.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            billingManager.isPremiumUnlocked.collect { isPremium ->
                if (isPremium) {
                    binding.purchaseLayout.visibility = View.GONE
                    binding.alreadyPremiumLayout.visibility = View.VISIBLE
                } else {
                    binding.purchaseLayout.visibility = View.VISIBLE
                    binding.alreadyPremiumLayout.visibility = View.GONE
                }
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnPurchase.isEnabled = !show
        binding.btnRestorePurchases.isEnabled = !show
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
