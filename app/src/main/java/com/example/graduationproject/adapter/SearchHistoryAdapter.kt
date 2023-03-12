package com.example.graduationproject.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.databinding.SearchHistoryItemBinding
import com.example.graduationproject.db.HistorySearchEntity
import java.util.Calendar

class SearchHistoryAdapter(private val listener: OnItemClickListener) :
    ListAdapter<HistorySearchEntity, SearchHistoryAdapter.viewholder>(Companion) {

    companion object : DiffUtil.ItemCallback<HistorySearchEntity>() {
        override fun areItemsTheSame(
            oldItem: HistorySearchEntity,
            newItem: HistorySearchEntity
        ): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: HistorySearchEntity,
            newItem: HistorySearchEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }


    class viewholder(var binding: SearchHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HistorySearchEntity) {
            binding.txtHistory.text = data.query

        }

    }

    interface OnItemClickListener {
        fun onItemClick(query: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =
            SearchHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.iconClose.setOnClickListener {
            listener.onItemClick(getItem(position).query.toString())

        }

    }


}
