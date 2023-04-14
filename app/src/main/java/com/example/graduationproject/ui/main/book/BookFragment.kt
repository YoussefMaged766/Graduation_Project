package com.example.graduationproject.ui.main.book

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentBookBinding
import com.example.graduationproject.models.BookIdResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookFragment : Fragment() {

    lateinit var binding: FragmentBookBinding
    private lateinit var dataStore: DataStore<Preferences>
    private val data : BookFragmentArgs by navArgs()
    private val viewModel: BookViewModel by viewModels()
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
        addMenu()
    }

   private fun selectHeart(){
        binding.imageViewHeart.setOnClickListener {
            if (binding.imageViewAnimation.isSelected){
                binding.imageViewAnimation.isSelected = false

            }else{
                binding.imageViewAnimation.isSelected = true
                binding.imageViewAnimation.likeAnimation()
                setFavourite()
                collectState()
                Log.e( "selectHeart: ", "click")

            }
        }
    }


  private  fun show(){
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


    private fun setFavourite(){
        lifecycleScope.launch {
            viewModel.setFavorite("Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString())
            )
        }
    }

    private fun setWishlist(){
        lifecycleScope.launch {
            viewModel.setWishlist("Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString())
            )
        }
    }
    private fun collectState(){
        lifecycleScope.launch {
            viewModel.stateFavourite.collect {
                if (it.success!=null){
                        Constants.customToast(requireContext(),requireActivity(),it.success.toString())
                    }


            }
        }
    }

    private fun collectStateWishlist(){
        lifecycleScope.launch {
            viewModel.stateWishlist.collect {
                if (it.success!=null){
                    Constants.customToast(requireContext(),requireActivity(),it.success.toString())
                }

            }
        }
    }
   private fun addMenu(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.optional_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_whishlist ->{
                        setWishlist()
                        collectStateWishlist()
                        return true
                    }

                }
                return false
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}