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

    fun list(): List<Intro> = arrayListOf(Intro(R.drawable.ic_launcher_background, "desc") ,
        Intro(R.drawable.ic_launcher_background, "alo"),
        Intro(R.drawable.ic_launcher_background, "dessdgasdfsadfasfdc")
        )

}