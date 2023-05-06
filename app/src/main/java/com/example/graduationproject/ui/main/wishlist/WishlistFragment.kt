package com.example.graduationproject.ui.main.wishlist

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.adapter.WishlistAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentWishlistBinding
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BookIdResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WishlistFragment : Fragment() {

    lateinit var binding: FragmentWishlistBinding
    val adapter: WishlistAdapter by lazy { WishlistAdapter() }
    private val viewModel: WishlistViewModel by viewModels()
    val position1: Int = 0
    val hash = HashMap<Int, BookEntity>()
    private lateinit var dataStore: DataStore<Preferences>
    val networkState = com.example.graduationproject.utils.NetworkState
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
        binding = FragmentWishlistBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            getAllWishlist()
        }
        longClick()
//        addMenu()

    }


    private suspend fun getAllWishlist() {

        viewModel.getAllWishlist(
            "Bearer ${getToken(Constants.userToken)}",
            userId = getUserId(Constants.userId)!!
        )

        viewModel.stateWishlist.collect {

//            if (it.isLoading){
//                Constants.showCustomAlertDialog(requireContext(), R.layout.custom_alert_dailog, false)
//            }else{
//                Constants.hideCustomAlertDialog()
//            }

            if (it.allLocalBooks.isNullOrEmpty()) {
                binding.lottieEmpty.visibility = View.VISIBLE
            } else {
                binding.lottieEmpty.visibility = View.GONE
                adapter.submitList(it.allLocalBooks)
                binding.recyclerWishlist.adapter = adapter


            }


        }
    }

    private fun remove(bookId: BookIdResponse, bookEntity: BookEntity) {

            lifecycleScope.launch {
                viewModel.removeWishlist(
                    "Bearer ${getToken(Constants.userToken)}",
                    userId = getUserId(Constants.userId)!!,
                    bookId = bookId,
                    booksItem = bookEntity.bookId!!
                )
            }
            lifecycleScope.launch {
                viewModel.stateRemoveWishlist.collect {
                    if (it.isLoading) {
                        Constants.showCustomAlertDialog(
                            requireContext(),
                            R.layout.custom_alert_dailog,
                            false
                        )
                    } else {
                        Constants.hideCustomAlertDialog()
                    }
                    if (it.success != null) {
                        Constants.customToast(requireContext(), requireActivity(), it.success)
                        adapter.notifyDataSetChanged()
                        lifecycleScope.launch {
                            getAllWishlist()
                        }
                    }
                }
            }



    }

    private fun longClick() {
        adapter.onItemLongClickListener = object : WishlistAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int, data: BookEntity) {
                val vibrator =
                    requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            50,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(50)
                }
                hash[position] = data
                addMenu()
            }

        }

    }

    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.remove_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        if (networkState.isOnline()){
                            val bookId = hash[position1]?.bookIdMongo
                            val bookEntity = hash[position1]
                            remove(BookIdResponse(bookId = bookId), bookEntity!!)
                        } else{
                            Constants.customToast(requireContext(), requireActivity(), "check your internet connection")
                            Log.e( "onMenuItemSelected: ","no Internet" )
                        }


                        return true
                    }

                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private suspend fun getUserId(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }


}