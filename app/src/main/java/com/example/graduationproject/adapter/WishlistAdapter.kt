package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BooksItem

class WishlistAdapter() :
    ListAdapter<BookEntity, WishlistAdapter.viewholder>(WishlistAdapter) {

      companion object : DiffUtil.ItemCallback<BookEntity>() {
        override fun areItemsTheSame(
            oldItem: BookEntity,
            newItem: BookEntity
        ): Boolean {

            return oldItem.bookId == newItem.bookId
        }

        override fun areContentsTheSame(
            oldItem: BookEntity,
            newItem: BookEntity
        ): Boolean {
            return oldItem.bookId == newItem.bookId
        }

    }


    class viewholder(var binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookEntity) {
        Glide.with(binding.root).load(data.coverImage).into(binding.bookImg)

        }

    }

    interface OnItemClickListener {
        fun onItemClick(query: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =
            BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params = binding.root.layoutParams
        params.width = (parent.width * 0.47).toInt()
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


