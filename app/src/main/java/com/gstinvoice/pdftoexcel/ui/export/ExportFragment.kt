package com.gstinvoice.pdftoexcel.ui.export

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gstinvoice.pdftoexcel.GSTInvoiceApplication
import com.gstinvoice.pdftoexcel.R
import com.gstinvoice.pdftoexcel.databinding.FragmentExportBinding
import com.gstinvoice.pdftoexcel.ui.preview.PreviewViewModel
import com.gstinvoice.pdftoexcel.ui.preview.PreviewViewModelFactory
import kotlinx.coroutines.launch

class ExportFragment : Fragment() {
    
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!
    
    private val exportViewModel: ExportViewModel by viewModels {
        val app = requireActivity().application as GSTInvoiceApplication
        ExportViewModelFactory(app.repository)
    }
    
    private val previewViewModel: PreviewViewModel by activityViewModels {
        val app = requireActivity().application as GSTInvoiceApplication
        PreviewViewModelFactory(app.repository, app.billingManager)
    }
    
    private var exportedFileUri: Uri? = null
    private var exportType: ExportType = ExportType.CSV
    
    private val createCsvFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportToFile(uri, ExportType.CSV)
            }
        }
    }
    
    private val createExcelFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportToFile(uri, ExportType.EXCEL)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnExportCsv.setOnClickListener {
            exportType = ExportType.CSV
            createCsvDocument()
        }
        
        binding.btnExportExcel.setOnClickListener {
            exportType = ExportType.EXCEL
            createExcelDocument()
        }
        
        binding.btnShare.setOnClickListener {
            exportedFileUri?.let { uri ->
                shareFile(uri)
            } ?: run {
                Toast.makeText(requireContext(), "Please export a file first", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            exportViewModel.uiState.collect { state ->
                when (state) {
                    is ExportViewModel.ExportUiState.Idle -> {
                        showLoading(false)
                    }
                    is ExportViewModel.ExportUiState.Loading -> {
                        showLoading(true)
                    }
                    is ExportViewModel.ExportUiState.Success -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        binding.btnShare.isEnabled = true
                    }
                    is ExportViewModel.ExportUiState.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnExportCsv.isEnabled = !show
        binding.btnExportExcel.isEnabled = !show
        binding.btnShare.isEnabled = !show
    }
    
    private fun createCsvDocument() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "invoice_export.csv")
        }
        createCsvFile.launch(intent)
    }
    
    private fun createExcelDocument() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_TITLE, "invoice_export.xlsx")
        }
        createExcelFile.launch(intent)
    }
    
    private fun exportToFile(uri: Uri, type: ExportType) {
        val invoiceData = previewViewModel.getCurrentInvoiceData()
        
        if (invoiceData == null) {
            Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            return
        }
        
        exportedFileUri = uri
        
        when (type) {
            ExportType.CSV -> exportViewModel.exportToCSV(invoiceData, uri)
            ExportType.EXCEL -> exportViewModel.exportToExcel(invoiceData, uri)
        }
    }
    
    private fun shareFile(uri: Uri) {
        val mimeType = when (exportType) {
            ExportType.CSV -> "text/csv"
            ExportType.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_file)))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        exportViewModel.resetState()
        _binding = null
    }
    
    private enum class ExportType {
        CSV, EXCEL
    }
}
