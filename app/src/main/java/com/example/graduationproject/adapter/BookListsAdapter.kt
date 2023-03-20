package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.databinding.BookListsItemBinding
import com.example.graduationproject.databinding.SearchHistoryItemBinding
import com.example.graduationproject.db.HistorySearchEntity
import com.example.graduationproject.models.BookResponse
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.search.SearchFragmentDirections

class BookListsAdapter(private val listener: BookListsAdapter.OnItemClickListener) :
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


    class viewholder(var binding: BookListsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BooksItem) {
//            binding.bookt.text = data.query

        }

    }

    interface OnItemClickListener {
        fun onItemClick(query: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =
            BookListsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


