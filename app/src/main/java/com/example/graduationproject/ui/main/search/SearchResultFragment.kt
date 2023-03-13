package com.example.graduationproject.ui.main.search

import android.content.ContentValues
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
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.graduationproject.R
import com.example.graduationproject.adapter.SearchResultAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentSearchResultBinding
import com.example.graduationproject.models.BooksItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    lateinit var binding: FragmentSearchResultBinding
    private val args: SearchResultFragmentArgs by navArgs()
    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    lateinit var adapter: SearchResultAdapter

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
        collectResponse()
        collectProgress()

    }

    fun getListedBook(query: String) {
        lifecycleScope.launch {
            viewModel.search(query,"Bearer ${getToken("userToken")}")
        }
    }

    private fun collectProgress() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.progress.collectLatest {
                    if (it){
                        Constants.showCustomAlertDialog(requireContext(),R.layout.custom_alert_dailog,false)
                        Log.e( "collectProgress12: ", "true")
                    } else{
//                        dialog.cancel()
                        Constants.hideCustomAlertDialog()
                        Log.e( "collectProgress12: ", "false")
                    }
//                    binding.frameLoading.isVisible = it
                    Log.i(ContentValues.TAG, "collectProgress: $it")
                }
            }
        }


    }

    fun collectResponse(){
        lifecycleScope.launch {
            viewModel.response.collect{
                if (it?.books!!.isEmpty()){
                    binding.lottieNoResult.visibility =View.VISIBLE
                } else{
                    adapter = SearchResultAdapter(it?.books!!)
                    binding.recyclerSearch.adapter =adapter
                    Log.e( "collectResponse: ", it.books.toString())
                }

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