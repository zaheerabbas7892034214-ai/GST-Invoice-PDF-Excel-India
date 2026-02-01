package com.gstinvoice.pdftoexcel.ui.preview

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gstinvoice.pdftoexcel.GSTInvoiceApplication
import com.gstinvoice.pdftoexcel.R
import com.gstinvoice.pdftoexcel.databinding.FragmentPreviewBinding
import com.gstinvoice.pdftoexcel.data.model.ExportData
import kotlinx.coroutines.launch

class PreviewFragment : Fragment() {
    
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!
    
    private val args: PreviewFragmentArgs by navArgs()
    
    private val viewModel: PreviewViewModel by viewModels {
        val app = requireActivity().application as GSTInvoiceApplication
        PreviewViewModelFactory(app.repository, app.billingManager)
    }
    
    private lateinit var dataTableAdapter: DataTableAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        val uri = Uri.parse(args.pdfUri)
        viewModel.loadPDF(uri, args.fileName)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupRecyclerView() {
        dataTableAdapter = DataTableAdapter()
        binding.rvDataTable.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataTableAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnExport.setOnClickListener {
            val invoiceData = viewModel.getCurrentInvoiceData()
            if (invoiceData != null) {
                val action = PreviewFragmentDirections.actionPreviewToExport()
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnUnlock.setOnClickListener {
            findNavController().navigate(R.id.action_preview_to_billing)
        }
        
        binding.btnRetry.setOnClickListener {
            val uri = Uri.parse(args.pdfUri)
            viewModel.loadPDF(uri, args.fileName)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PreviewViewModel.PreviewUiState.Idle -> {
                        showLoading(false)
                        showError(false)
                        showContent(false)
                    }
                    is PreviewViewModel.PreviewUiState.Loading -> {
                        showLoading(true)
                        showError(false)
                        showContent(false)
                    }
                    is PreviewViewModel.PreviewUiState.Success -> {
                        showLoading(false)
                        showError(false)
                        showContent(true)
                        displayData(state.data)
                    }
                    is PreviewViewModel.PreviewUiState.Error -> {
                        showLoading(false)
                        showError(true, state.message)
                        showContent(false)
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPremiumUnlocked.collect { isPremium ->
                updatePremiumStatus(isPremium)
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.loadingLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showError(show: Boolean, message: String? = null) {
        binding.errorLayout.visibility = if (show) View.VISIBLE else View.GONE
        message?.let { binding.tvError.text = it }
    }
    
    private fun showContent(show: Boolean) {
        binding.contentLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun displayData(data: ExportData) {
        dataTableAdapter.submitData(data)
    }
    
    private fun updatePremiumStatus(isPremium: Boolean) {
        if (isPremium) {
            binding.previewInfoCard.visibility = View.GONE
            binding.blurOverlay.visibility = View.GONE
            dataTableAdapter.setUnlockRows(true)
        } else {
            binding.previewInfoCard.visibility = View.VISIBLE
            binding.blurOverlay.visibility = View.VISIBLE
            dataTableAdapter.setUnlockRows(false)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
