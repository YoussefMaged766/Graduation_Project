package com.example.graduationproject.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.databinding.BookItemBinding
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BooksItem
import java.lang.IndexOutOfBoundsException

class WishlistAdapter() :
    ListAdapter<BookEntity, WishlistAdapter.viewholder>(WishlistAdapter) {
    var selectedItem = -1
    interface OnItemLongClickListener {

        fun onItemLongClick(position: Int, data: BookEntity)

    }
    var onItemLongClickListener: OnItemLongClickListener? = null
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
//        if (selected) {
//            binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.purple_200))
//        } else {
//            binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.white))
//        }
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

        holder.itemView.setOnLongClickListener {
//            if (selectedItem.contains(position)) {
//                selectedItem.remove(position)
//                holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.background))
//            } else {
//                selectedItem.add(position)
//                holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.purple_200))
//            }
            if (selectedItem == position){
                selectedItem = -1
            } else {
                selectedItem = position
            }
            notifyDataSetChanged()

try {
    onItemLongClickListener?.onItemLongClick(position, getItem(position))
} catch (e: IndexOutOfBoundsException) {
    e.printStackTrace()
    Log.e( "onBindViewHolder: ",e.message.toString() )
}

            true
        }
        try {
            if (position == selectedItem&& selectedItem!=-1){
                holder.binding.frameLayout.setBackgroundColor(holder.itemView.context.getColor(R.color.selected_background))
            } else {
                holder.binding.frameLayout.setBackgroundColor(Color.TRANSPARENT)
            }

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            Log.e( "onBindViewHolder: ",e.message.toString() )
        }

        holder.binding.root.setOnClickListener {
//            val action = FavoriteFragmentDirections.actionFavoriteFragmentToBookFragment()
//            it.findNavController().navigate(action)
        }

    }
}


