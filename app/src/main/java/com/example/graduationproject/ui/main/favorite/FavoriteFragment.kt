package com.example.graduationproject.ui.main.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.adapter.WishlistAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentFavoriteBinding
import com.example.graduationproject.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    lateinit var  binding :FragmentFavoriteBinding
    private val viewModel: FavouriteViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    val adapter: WishlistAdapter by lazy { WishlistAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=  FragmentFavoriteBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            collectStates()
        }
    }


    private suspend fun collectStates(){
        viewModel.getAllFavorite("Bearer ${getToken(Constants.userToken)}")
        viewModel.stateFav.collect{
            if (it.isLoading){
                Constants.showCustomAlertDialog(requireContext(), R.layout.custom_alert_dailog, false)
            }else{
                Constants.hideCustomAlertDialog()
            }

            if (it.allBooks.isNullOrEmpty()){
                binding.lottieEmpty.visibility= View.VISIBLE
            } else{
                binding.lottieEmpty.visibility= View.GONE
                adapter.submitList( it.allBooks)
                binding.recyclerFav.adapter =adapter

            }
        }
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }


}