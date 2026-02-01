package com.gstinvoice.pdftoexcel.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gstinvoice.pdftoexcel.GSTInvoiceApplication
import com.gstinvoice.pdftoexcel.R
import com.gstinvoice.pdftoexcel.databinding.FragmentHomeBinding
import com.gstinvoice.pdftoexcel.utils.BillingManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as GSTInvoiceApplication
        HomeViewModelFactory(app.repository, app.billingManager)
    }
    
    private lateinit var recentFilesAdapter: RecentFilesAdapter
    
    private val pdfPicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri)
                navigateToPreview(uri, fileName)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            handleMenuItemClick(item)
        }
    }
    
    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.action_home_to_settings)
                true
            }
            R.id.action_restore_purchases -> {
                viewModel.billingManager.restorePurchases()
                observeBillingState()
                true
            }
            else -> false
        }
    }
    
    private fun setupRecyclerView() {
        recentFilesAdapter = RecentFilesAdapter(
            onItemClick = { file ->
                val uri = Uri.parse(file.filePath)
                navigateToPreview(uri, file.fileName)
            },
            onDeleteClick = { file ->
                viewModel.deleteRecentFile(file.id)
            }
        )
        
        binding.rvRecentFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentFilesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSelectPdf.setOnClickListener {
            openPdfPicker()
        }
        
        binding.btnGoPremium.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_billing)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentFiles.collect { files ->
                recentFilesAdapter.submitList(files)
                binding.tvNoRecentFiles.visibility = if (files.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPremiumUnlocked.collect { isPremium ->
                binding.premiumBanner.visibility = if (isPremium) View.GONE else View.VISIBLE
            }
        }
    }
    
    private fun observeBillingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.billingManager.billingState.collect { state ->
                when (state) {
                    is BillingManager.BillingState.Success -> {
                        Toast.makeText(requireContext(), R.string.restore_success, Toast.LENGTH_SHORT).show()
                    }
                    is BillingManager.BillingState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        pdfPicker.launch(intent)
    }
    
    private fun getFileName(uri: Uri): String {
        var fileName = "invoice.pdf"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }
    
    private fun navigateToPreview(uri: Uri, fileName: String) {
        val action = HomeFragmentDirections.actionHomeToPreview(
            uri.toString(),
            fileName
        )
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
