package com.example.graduationproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.graduationproject.R
import com.example.graduationproject.databinding.IntroItemBinding
import com.example.graduationproject.models.Intro

class IntroAdapter(val  items :List<Intro>) :RecyclerView.Adapter<IntroAdapter.viewHolder>() {

    class viewHolder( var binding:IntroItemBinding) :ViewHolder(binding.root){
        fun bind(item: Intro){
           binding.txtDescIntro.text = item.desc
           binding.introImg.setImageResource(item.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view = IntroItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
    return items.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
       holder.bind(items[position])
    }
}