package com.example.graduationproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BooksItem

class BookListsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<BooksItem, BookListsAdapter.viewholder>(BookListsAdapter) {

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
            Log.e( "bind: ", data.title.toString())
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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
           listener.onItemClick(position)
        }

    }
}


