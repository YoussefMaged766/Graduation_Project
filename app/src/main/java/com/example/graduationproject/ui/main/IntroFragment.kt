package com.example.graduationproject.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.adapter.IntroAdapter
import com.example.graduationproject.databinding.FragmentIntroBinding
import com.example.graduationproject.models.Intro

class IntroFragment : Fragment() {

    lateinit var binding: FragmentIntroBinding
    lateinit var adapter: IntroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IntroAdapter(list())
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)
       binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback(){
           override fun onPageSelected(position: Int) {
               super.onPageSelected(position)
               if (position == 2){
                   binding.btnFinish.visibility = View.VISIBLE
               }else{
                   binding.btnFinish.visibility = View.GONE
               }
           }
         })
        binding.btnFinish.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_welcomeFragment)

        }
    }

    fun list(): List<Intro> = arrayListOf(Intro(R.drawable.intro1, "Choose your favorite \n Book with us"),
        Intro(R.drawable.intro2, "Explore our diverse collection of books across various genres \n Swipe across and browse to find \n captivating reads or let our intelligent algorithm \n recommend books based on your preferences"),
        Intro(R.drawable.intro3, "Have nice Day to read books \n with us")
        )

}