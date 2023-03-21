package com.example.graduationproject.ui.main.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.graduationproject.databinding.FragmentFavoriteBinding
import com.example.graduationproject.databinding.FragmentProfileBinding


class FavoriteFragment : Fragment() {
    lateinit var  binding :FragmentFavoriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        val adapter = MyAdapter(data)
//        recyclerView.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=  FragmentFavoriteBinding.inflate(layoutInflater)

        return binding.root
    }


}