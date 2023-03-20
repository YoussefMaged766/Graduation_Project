package com.example.graduationproject.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.graduationproject.R
import com.example.graduationproject.adapter.HomePagingAdapter
import com.example.graduationproject.adapter.SearchResultAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.ui.main.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    val adapter: HomePagingAdapter by lazy { HomePagingAdapter() }
    val viewModel: HomeViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
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
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListedBook()
        collectState()
    }

    private fun getListedBook() {
        lifecycleScope.launch {
            viewModel.getAllBooks("Bearer ${getToken("userToken")}")
        }
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private fun collectState(){
        lifecycleScope.launch {
            viewModel.state.collectLatest { bookState ->

                withContext(Dispatchers.Main){
                    if (bookState.isLoading){
                        Constants.showCustomAlertDialog(requireContext(),R.layout.custom_alert_dailog,false)
                    }
                    if (!bookState.isLoading){
                        Constants.hideCustomAlertDialog()
                    }

                    bookState.allBooks?.let { adapter.submitData(it) }
                    binding.recyclerHome.adapter = adapter

                    if (bookState.error=="Token is not valid!"){
                        showExitDialog()
                    }
                }

            }
            adapter.loadStateFlow.collectLatest{

             binding.error.isVisible = it.refresh is LoadState.Error

            }
            adapter.addLoadStateListener {
                if (it.prepend is LoadState.Error){ // if there is an error
                    binding.error.isVisible = true
                    Log.e("collectState: ","error" )
            }
            }
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Session Expired!")
            .setMessage("please login again")
            .setCancelable(false)
            .setPositiveButton("No") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("Login"){
                    _, _ ->
                findNavController().navigate(R.id.action_homeFragment_to_nav_graph)
                lifecycleScope.launch {
                    clearDataStore()
                    clearDataStoreID()
                }



            }
            .show()
    }

    private suspend fun clearDataStore() {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey("userToken")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }

    private suspend fun clearDataStoreID() {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey("userId")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }

}