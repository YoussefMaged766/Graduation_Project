package com.example.graduationproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentBookBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class BookFragment : Fragment() {

    lateinit var binding: FragmentBookBinding
    private lateinit var dataStore: DataStore<Preferences>
    private val data : BookFragmentArgs by navArgs()
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
       binding = FragmentBookBinding.inflate(layoutInflater)
//        data.bookObject
       Glide.with(requireContext()).load(data.bookObject.coverImage).into(binding.BookImage)
        binding.txtBookTitle.text = data.bookObject.title
//        binding.txtAuthor.text = data.bookObject.
        binding.txtDescription.text =data.bookObject.description
        binding.txtRating.text = data.bookObject.ratings.toString()
        binding.imageViewHeart.setOnClickListener(View.OnClickListener {
        lifecycleScope.launch{
            getToken("userToken")

        }
        })
        return binding.root
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }
}