package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BooksItem

class WishlistAdapter() :
    ListAdapter<BooksItem, WishlistAdapter.viewholder>(WishlistAdapter) {

      companion object : DiffUtil.ItemCallback<BooksItem>() {
        override fun areItemsTheSame(
            oldItem: BooksItem,
            newItem: BooksItem
        ): Boolean {

            return oldItem.bookId == newItem.bookId
        }

        override fun areContentsTheSame(
            oldItem: BooksItem,
            newItem: BooksItem
        ): Boolean {
            return oldItem.bookId == newItem.bookId
        }

    }


    class viewholder(var binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BooksItem) {
        Glide.with(binding.root).load(data.coverImage).into(binding.bookImg)

        }

    }

    interface OnItemClickListener {
        fun onItemClick(query: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =
            BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.root.setOnClickListener {
//            val action = FavoriteFragmentDirections.actionFavoriteFragmentToBookFragment()
//            it.findNavController().navigate(action)
        }

    }
}


