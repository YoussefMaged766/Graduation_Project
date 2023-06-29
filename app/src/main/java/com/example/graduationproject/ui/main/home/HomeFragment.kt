package com.example.graduationproject.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.example.graduationproject.R
import com.example.graduationproject.adapter.HomePagingAdapter
import com.example.graduationproject.adapter.loadState.LoadStateAdapter
import com.example.graduationproject.adapter.SearchResultAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.models.BooksItem
import com.example.graduationproject.ui.main.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    val adapter: HomePagingAdapter by lazy { HomePagingAdapter() }
    val viewModel: HomeViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e( "onCreate: ","alo" )
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
        recycler()
        lifecycleScope.launch {
            Log.e( "onViewCreated: ",getUserId(Constants.userId).toString() )
        }

//        collectRecommendationState()
        binding.swipeRefresh.setOnRefreshListener {
            getListedBook()
            lifecycleScope.launch {
                delay(1000)
            }
        }
    }

    private fun getListedBook() {
        lifecycleScope.launch {

            viewModel.Books("Bearer ${getToken("userToken")}")

        }
        viewModel.data.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)

            }

        }

    }

  private  fun recycler() {
        binding.recyclerHome.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() },
            footer = LoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener {
            binding.swipeRefresh.isRefreshing = it.source.refresh is LoadState.Loading
            handelError(it)


        }

    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Session Expired!")
            .setCancelable(false)
            .setPositiveButton("Login") {
                    _, _ ->
                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                lifecycleScope.launch {
                    clearDataStore()
                    clearDataStoreID()
                }
                requireActivity().finish()
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

    private fun handelError(loadStates: CombinedLoadStates) {
        val errorState = loadStates.source.refresh as? LoadState.Error
            ?: loadStates.source.prepend as? LoadState.Error
        errorState?.let {
            when(val error = it.error) {
                is HttpException -> {
                    val gson = Gson()
                    val type = object : TypeToken<BooksItem>() {}.type
                    val errorResponse: BooksItem? =
                        gson.fromJson(error.response()?.errorBody()!!.charStream(), type)
                    if (errorResponse?.message.toString()=="Token is not valid!"){
                        showExitDialog()
                    }
                }
                else -> {}
            }
        }
    }

    private fun collectRecommendationState(){
        lifecycleScope.launch{
            viewModel.getRecommendation(getUserId(Constants.userId)!!)
            viewModel.stateReco.collect{
                Log.e( "collectRecommendationState: ",it.allBooks.toString() )
                binding.swipeRefresh.isRefreshing = it.isLoading
                Log.e( "collectRecommendationStateError: ", it.error.toString())

                if (it.isLoading) {
                    Constants.showCustomAlertDialog(
                        requireActivity(),
                        R.layout.custom_alert_dailog,
                        false
                    )
                } else {
                    Constants.hideCustomAlertDialog()
                }
            }
        }
    }

    private suspend fun getUserId(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }



}