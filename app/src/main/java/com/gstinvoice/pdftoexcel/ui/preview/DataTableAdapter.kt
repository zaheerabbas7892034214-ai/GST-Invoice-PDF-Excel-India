package com.gstinvoice.pdftoexcel.ui.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gstinvoice.pdftoexcel.data.model.ExportData
import com.gstinvoice.pdftoexcel.databinding.ItemTableHeaderBinding
import com.gstinvoice.pdftoexcel.databinding.ItemTableRowBinding

class DataTableAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ROW = 1
        private const val FREE_ROW_LIMIT = 10
    }
    
    private var data: ExportData? = null
    private var unlockAllRows = false
    
    fun submitData(exportData: ExportData) {
        data = exportData
        notifyDataSetChanged()
    }
    
    fun setUnlockRows(unlock: Boolean) {
        unlockAllRows = unlock
        notifyDataSetChanged()
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ROW
    }
    
    override fun getItemCount(): Int {
        val currentData = data ?: return 0
        return currentData.rows.size + 1 // +1 for header
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemTableHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemTableRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                RowViewHolder(binding)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentData = data ?: return
        
        when (holder) {
            is HeaderViewHolder -> holder.bind(currentData.headers)
            is RowViewHolder -> {
                val rowIndex = position - 1
                val isLocked = !unlockAllRows && rowIndex >= FREE_ROW_LIMIT
                holder.bind(currentData.rows[rowIndex], isLocked)
            }
        }
    }
    
    class HeaderViewHolder(
        private val binding: ItemTableHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(headers: List<String>) {
            binding.tvCell1.text = headers.getOrNull(0) ?: ""
            binding.tvCell2.text = headers.getOrNull(1) ?: ""
            binding.tvCell3.text = headers.getOrNull(2) ?: ""
            binding.tvCell4.text = headers.getOrNull(3) ?: ""
            binding.tvCell5.text = headers.getOrNull(4) ?: ""
            binding.tvCell6.text = headers.getOrNull(5) ?: ""
        }
    }
    
    class RowViewHolder(
        private val binding: ItemTableRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(row: List<String>, isLocked: Boolean) {
            if (isLocked) {
                binding.tvCell1.text = "••••"
                binding.tvCell2.text = "••••"
                binding.tvCell3.text = "••••"
                binding.tvCell4.text = "••••"
                binding.tvCell5.text = "••••"
                binding.tvCell6.text = "••••"
                binding.root.alpha = 0.4f
            } else {
                binding.tvCell1.text = row.getOrNull(0) ?: ""
                binding.tvCell2.text = row.getOrNull(1) ?: ""
                binding.tvCell3.text = row.getOrNull(2) ?: ""
                binding.tvCell4.text = row.getOrNull(3) ?: ""
                binding.tvCell5.text = row.getOrNull(4) ?: ""
                binding.tvCell6.text = row.getOrNull(5) ?: ""
                binding.root.alpha = 1.0f
            }
        }
    }
}
