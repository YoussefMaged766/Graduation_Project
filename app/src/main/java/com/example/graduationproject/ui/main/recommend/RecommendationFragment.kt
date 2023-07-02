package com.example.graduationproject.ui.main.recommend

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
import com.example.graduationproject.R
import com.example.graduationproject.adapter.BookListsAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentRecommendationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendationFragment : Fragment(), BookListsAdapter.OnItemClickListener {

    lateinit var binding: FragmentRecommendationBinding
    val viewModel: RecommendationViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    val adapter by lazy { BookListsAdapter(this) }
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
        binding = FragmentRecommendationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectRecommendationState()

    }

    override fun onItemClick(position: Int) {
        val action =
            RecommendationFragmentDirections.actionRecommendationFragmentToBookFragment(adapter.currentList[position])
        findNavController().navigate(action)
    }

    private suspend fun getUserId(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private fun collectRecommendationState() {
        lifecycleScope.launch {

            viewModel.getRecommendationFromMongo("Bearer ${getToken("userToken")}")

            viewModel.stateReco2.collect {
                if (it.recommendation?.results.isNullOrEmpty()) {
                    viewModel.getRecommendation(getUserId(Constants.userId)!!)
                    viewModel.getRecommendationFromMongo("Bearer ${getToken("userToken")}")
                    val data = async { it.recommendation?.results }
                    adapter.submitList(data.await())

                    viewModel.stateReco.collect{it2->
                        if (it2.isLoading) {
                            binding.shimmerRecyclerRecommendation.startShimmerAnimation()
                            binding.shimmerRecyclerRecommendation.visibility = View.VISIBLE
                            binding.rvRecommendation.visibility = View.GONE

                        } else {
                            binding.shimmerRecyclerRecommendation.stopShimmerAnimation()
                            binding.shimmerRecyclerRecommendation.visibility = View.GONE
                            binding.rvRecommendation.visibility = View.VISIBLE
                        }
                    }



                } else {
                    Log.e("collectRecommendationState: ", it.recommendation?.updatedAt.toString())
                    adapter.submitList(it.recommendation?.results)
                    binding.shimmerRecyclerRecommendation.startShimmerAnimation()
                    binding.shimmerRecyclerRecommendation.visibility = View.VISIBLE

                    if (it.isLoading) {
                        binding.shimmerRecyclerRecommendation.startShimmerAnimation()
                        binding.shimmerRecyclerRecommendation.visibility = View.VISIBLE
                        binding.rvRecommendation.visibility = View.GONE

                    } else {
                        binding.shimmerRecyclerRecommendation.stopShimmerAnimation()
                        binding.shimmerRecyclerRecommendation.visibility = View.GONE
                        binding.rvRecommendation.visibility = View.VISIBLE
                    }

                }
            }


        }
        binding.rvRecommendation.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelRequest()
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

}