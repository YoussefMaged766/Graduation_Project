package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.home.HomeFragmentDirections
import com.example.graduationproject.ui.main.search.SearchResultFragmentDirections

class SearchResultAdapter :PagingDataAdapter<BooksItem,SearchResultAdapter.viewholder>(DiffCallBack()) {



    class viewholder(var binding: BookItemBinding):ViewHolder(binding.root){

        fun bind(bookX: BooksItem){
            Glide.with(binding.root).load(bookX.coverImage).into(binding.bookImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
         val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }



    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.itemView.setOnClickListener {
            val action = SearchResultFragmentDirections.actionSearchResultFragmentToBookFragment(getItem(position)!!)
            it.findNavController().navigate(action)
        }

    }

    class DiffCallBack : DiffUtil.ItemCallback<BooksItem>() {
        override fun areItemsTheSame(oldItem: BooksItem, newItem: BooksItem): Boolean {
            return oldItem.id == newItem.id && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BooksItem, newItem: BooksItem): Boolean {
            return oldItem.id == newItem.id && oldItem.title == newItem.title
        }


    }
}