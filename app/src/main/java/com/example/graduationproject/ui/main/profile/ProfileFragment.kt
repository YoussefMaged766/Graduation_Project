package com.example.graduationproject.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.graduationproject.adapter.ViewPagerAdapter
import com.example.graduationproject.databinding.FragmentProfileBinding
import com.example.graduationproject.ui.main.favorite.FavoriteFragment
import com.example.graduationproject.ui.main.wishlist.WishlistFragment
import com.example.graduationproject.utils.FadeOutTransformation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val fadeOutTransformation= FadeOutTransformation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
    }

    private fun setUpViewPager() {
        val adapter =
            ViewPagerAdapter(
                supportFragmentManager = requireActivity().supportFragmentManager,
                lifecycle = lifecycle
            )
        adapter.addFragment(WishlistFragment(), "Wishlist")
        adapter.addFragment(FavoriteFragment(), "Favorite")
        binding.viewpager.adapter = adapter
        binding.viewpager.isSaveEnabled = false
        binding.viewpager.setPageTransformer(fadeOutTransformation)
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
            binding.viewpager.setCurrentItem(tab.position, true)
        }.attach()


    }


}