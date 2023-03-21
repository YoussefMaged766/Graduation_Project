package com.example.graduationproject

import android.content.ContentValues.TAG
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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentBookBinding
import com.example.graduationproject.ui.main.favorite.FavoriteViewModel
import com.example.graduationproject.ui.main.home.HomeViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookFragment : Fragment() {

    lateinit var binding: FragmentBookBinding
    private lateinit var dataStore: DataStore<Preferences>
    private val data : BookFragmentArgs by navArgs()
    val viewModel: FavoriteViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       binding = FragmentBookBinding.inflate(layoutInflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        show()
        selectHeart()
        binding.imageViewHeart.setOnClickListener {

        }
    }

    fun selectHeart(){
        binding.imageViewHeart.setOnClickListener {
            if (binding.imageViewAnimation.isSelected){
                binding.imageViewAnimation.isSelected = false

            }else{
                binding.imageViewAnimation.isSelected = true
                binding.imageViewAnimation.likeAnimation()
                lifecycleScope.launch {
                    addFavoriate()
                }
            }
        }
    }


    fun show(){
        Glide.with(requireContext()).load(data.bookObject.coverImage).into(binding.BookImage)
        binding.txtBookTitle.text = data.bookObject.title

        binding.txtDescription.text =data.bookObject.description
        binding.txtRating.text = data.bookObject.ratings.toString()
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }
    private suspend fun addFavoriate(){
        viewModel.addFavorite(
            getToken("userToken").toString(),
            data.bookObject.bookId.toString()
        )
        Log.d(TAG, "addFavoriate: i'm here")
        //test
    }
}