package com.example.graduationproject.ui.main.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.example.graduationproject.R
import com.example.graduationproject.adapter.SearchResultAdapter
import com.example.graduationproject.adapter.loadState.LoadStateAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentSearchResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    lateinit var binding: FragmentSearchResultBinding
    private val args: SearchResultFragmentArgs by navArgs()
    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    private val adapter: SearchResultAdapter by lazy { SearchResultAdapter() }

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
        recycler()
    }

    private fun getListedBook(query: String) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = query
        lifecycleScope.launch {
            delay(2000)
            viewModel.searchResult(query, "Bearer ${getToken("userToken")}")
        }
        viewModel.data.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }

        }

    }

    private fun recycler() {
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )
        binding.recyclerSearch.adapter = adapter
        adapter.addLoadStateListener {
            binding.lottieSearch.isVisible = it.source.refresh is LoadState.Loading
            if (it.refresh is LoadState.NotLoading) {
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

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

}