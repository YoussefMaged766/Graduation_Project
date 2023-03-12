package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BookX
import com.example.graduationproject.models.BooksItem

class SearchResultAdapter(var items :List<BooksItem>) :RecyclerView.Adapter<SearchResultAdapter.viewholder>() {



    class viewholder(var binding: BookItemBinding):ViewHolder(binding.root){

        fun bind(bookX: BooksItem){
            Glide.with(binding.root).load(bookX.coverImage).into(binding.bookImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
         val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(items[position])
    }
}