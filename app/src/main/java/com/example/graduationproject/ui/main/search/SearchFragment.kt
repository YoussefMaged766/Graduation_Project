package com.example.graduationproject.ui.main.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.adapter.SearchHistoryAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentSearchBinding
import com.example.graduationproject.db.HistorySearchEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class SearchFragment : Fragment(),SearchHistoryAdapter.OnItemClickListener {

    lateinit var binding: FragmentSearchBinding
     var adapter=SearchHistoryAdapter(this)
    val viewModel:SearchViewModel by viewModels()
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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            getQuery()
            getAllHistory()
            Log.e( "onViewCreated: ", getUserId("userId").toString())
        }
    }

    private suspend fun getQuery() {

            binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(query!!)
                    findNavController().navigate(action)
                    lifecycleScope.launch{
                        saveHistorySearch(HistorySearchEntity(Calendar.getInstance().timeInMillis,query,getUserId("userId")!!))
                    }

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                   return false
                }

            })


    }

    fun saveHistorySearch(query:HistorySearchEntity){
        viewModel.saveSearchHistory(query)
        lifecycleScope.launch {
            viewModel.error.collect{
                Constants.customToast(requireContext(),requireActivity(),it)
            }
        }
    }

   private suspend fun getAllHistory(){
       getUserId("userId")?.let { viewModel.getAllHistorySearch(it) }
        lifecycleScope.launch {
            viewModel.searchResponse.collect{

                adapter.submitList(it)
                binding.recyclerSearch.adapter = adapter

            }
        }
    }



    private fun deleteHistorySearch(query:String){
        viewModel.deleteHistorySearch(query)
        lifecycleScope.launch {
            viewModel.successDelete.collect{
                Constants.customToast(requireContext(),requireActivity(),it)
            }

        }
    }

    private suspend fun getUserId(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }
    override fun onItemClick(query: String) {
       deleteHistorySearch(query)


    }


}