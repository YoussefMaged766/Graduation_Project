package com.example.graduationproject.ui.main.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.adapter.WishlistAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentFavoriteBinding
import com.example.graduationproject.models.mappers.toBookItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment(), WishlistAdapter.OnItemClickListener {
    lateinit var  binding :FragmentFavoriteBinding
    private val viewModel: FavouriteViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    val adapter: WishlistAdapter by lazy { WishlistAdapter(this) }
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
        viewModel.getAllFavorite("Bearer ${getToken(Constants.userToken)}", getUserId(Constants.userId)!!)
        viewModel.stateFav.collect{
//            if (it.isLoading){
//                Constants.showCustomAlertDialog(requireContext(), R.layout.custom_alert_dailog, false)
//            }else{
//                Constants.hideCustomAlertDialog()
//            }

            if (it.allLocalBooks.isNullOrEmpty()){
                binding.lottieEmpty.visibility= View.VISIBLE
            } else{
                binding.lottieEmpty.visibility= View.GONE
                adapter.submitList( it.allLocalBooks)
                binding.recyclerFav.adapter =adapter
                Log.e( "collectStates: ",it.allLocalBooks.toString() )

            }
        }
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private suspend fun getUserId(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    override fun onItemClick(position: Int) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToBookFragment2(adapter.currentList[position].toBookItem())
        findNavController().navigate(action)
    }


}