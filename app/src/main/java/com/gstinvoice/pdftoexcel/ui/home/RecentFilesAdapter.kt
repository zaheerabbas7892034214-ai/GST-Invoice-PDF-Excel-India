package com.gstinvoice.pdftoexcel.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gstinvoice.pdftoexcel.data.database.RecentFileEntity
import com.gstinvoice.pdftoexcel.databinding.ItemRecentFileBinding
import java.text.SimpleDateFormat
import java.util.*

class RecentFilesAdapter(
    private val onItemClick: (RecentFileEntity) -> Unit,
    private val onDeleteClick: (RecentFileEntity) -> Unit
) : ListAdapter<RecentFileEntity, RecentFilesAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemRecentFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(file: RecentFileEntity) {
            binding.tvFileName.text = file.fileName
            binding.tvFileDetails.text = "${file.rowCount} rows • ${formatDate(file.conversionDate)}"
            
            binding.root.setOnClickListener {
                onItemClick(file)
            }
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(file)
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < 60_000 -> "Just now"
                diff < 3600_000 -> "${diff / 60_000} minutes ago"
                diff < 86400_000 -> "${diff / 3600_000} hours ago"
                diff < 604800_000 -> "${diff / 86400_000} days ago"
                else -> {
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<RecentFileEntity>() {
        override fun areItemsTheSame(oldItem: RecentFileEntity, newItem: RecentFileEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: RecentFileEntity, newItem: RecentFileEntity): Boolean {
            return oldItem == newItem
        }
    }
}
