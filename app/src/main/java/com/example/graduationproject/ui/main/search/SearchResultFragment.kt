package com.example.graduationproject.ui.main.search

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingSource
import com.example.graduationproject.R
import com.example.graduationproject.adapter.SearchResultAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentSearchResultBinding
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.utils.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    lateinit var binding: FragmentSearchResultBinding
    private val args: SearchResultFragmentArgs by navArgs()
    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
  private  val  adapter: SearchResultAdapter by lazy {SearchResultAdapter()}

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
        binding = FragmentSearchResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListedBook(args.query)
        collectState()
    }

    private fun getListedBook(query: String) {
        lifecycleScope.launch {
            viewModel.search(query, "Bearer ${getToken("userToken")}")
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
                 binding.recyclerSearch.adapter = adapter

                 adapter.loadStateFlow.map { it.refresh }
                     .distinctUntilChanged()
                     .collect {
                         if (it is LoadState.NotLoading) {

                             // PagingDataAdapter.itemCount here
                             if (adapter.itemCount == 0) {
                                 binding.lottieNoResult.visibility = View.VISIBLE
                                 Constants.hideCustomAlertDialog()
                             } else {
                                 binding.lottieNoResult.visibility = View.GONE
                                 Constants.hideCustomAlertDialog()
                             }
                             Log.e("collectResponse: ", adapter.itemCount.toString())
                         }
                     }
               }

            }
        }
    }
}