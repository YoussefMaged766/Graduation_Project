package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.home.HomeFragmentDirections

class HomePagingAdapter :
    PagingDataAdapter<BooksItem, HomePagingAdapter.viewholder>(DiffCallBack()) {


    class viewholder(var binding: BookItemBinding) : ViewHolder(binding.root) {
        var lastPosition = -1
        fun setAnimation(viewToAnimate: View, position: Int) {
            if (position > lastPosition){
                val animation: Animation = android.view.animation.AnimationUtils.loadAnimation(
                    viewToAnimate.context,
                    R.anim.book_anim
                )
                viewToAnimate.startAnimation(animation)
                lastPosition= position
            }

        }

        fun bind(bookX: BooksItem) {
            Glide.with(binding.root).load(bookX.coverImage).into(binding.bookImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params = binding.root.layoutParams
        params.width = (parent.width * 0.47).toInt()
        return viewholder(binding)
    }


    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToBookFragment2(getItem(position)!!)
            it.findNavController().navigate(action)
        })
        holder.setAnimation(holder.itemView,position)
    }

    class DiffCallBack : DiffUtil.ItemCallback<BooksItem>() {
        override fun areItemsTheSame(oldItem: BooksItem, newItem: BooksItem): Boolean {
            return oldItem.bookId == newItem.bookId && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BooksItem, newItem: BooksItem): Boolean {
            return oldItem.bookId == newItem.bookId && oldItem.title == newItem.title
        }


    }
}