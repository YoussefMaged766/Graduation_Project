package com.example.graduationproject.ui.main.book

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.adapter.BookListsAdapter
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentBookBinding
import com.example.graduationproject.models.BookEntity
import com.example.graduationproject.models.BookIdResponse
import com.example.graduationproject.models.mappers.toBookEntity
import com.example.graduationproject.utils.NetworkState
import com.noowenz.showmoreless.ShowMoreLess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookFragment : Fragment() , BookListsAdapter.OnItemClickListener {

    lateinit var binding: FragmentBookBinding
    private lateinit var dataStore: DataStore<Preferences>
    private val data: BookFragmentArgs by navArgs()
    private val viewModel: BookViewModel by viewModels()
    val adapter :BookListsAdapter by lazy { BookListsAdapter(this) }
  private var bookEntity=BookEntity()



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
//        checkFav()
//        checkLocalFav()
        showMoreAndLess()
        collectRecommend()
        addToCart(data.bookObject.isbn13.toString())


    }

    private fun selectHeart() {
        binding.imageViewHeart.setOnClickListener {
            if (binding.imageViewAnimation.isSelected) {

                binding.imageViewAnimation.isSelected = false
                setUnFavourite()

            } else {
                binding.imageViewAnimation.isSelected = true
                binding.imageViewAnimation.likeAnimation()
//                addFavourite()
                setFavourite()
                collectState()

            }
        }
    }


    private fun show() {
        Glide.with(requireContext()).load(data.bookObject.coverImage).into(binding.BookImage)
        binding.txtBookTitle.text = data.bookObject.title
        binding.txtDescription.text = data.bookObject.description
//        binding.txtRating.text = data.bookObject.ratings.toString()
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


    private fun setFavourite() {
        lifecycleScope.launch {
            viewModel.setFavorite(
                "Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString()),
                data.bookObject,
                getUserId(Constants.userId)!!
            )

        }

    }

    private fun setUnFavourite() {
        lifecycleScope.launch {
            viewModel.removeFavorite(
                "Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString()),
                data.bookObject,
                getUserId(Constants.userId)!!
            )

        }
    }


    private fun setWishlist() {
        lifecycleScope.launch {
            viewModel.setWishlist(
                "Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString()),
                data.bookObject,
                getUserId(Constants.userId)!!
            )
        }
    }
    private fun setRead() {
        lifecycleScope.launch {
            viewModel.setRead(
                "Bearer ${getToken("userToken")}",
                BookIdResponse(bookId = data.bookObject.id.toString()),
                data.bookObject,
                getUserId(Constants.userId)!!
            )
        }
    }


    private fun collectState() {
        lifecycleScope.launch {
            viewModel.stateFavourite.collect {
                if (it.success != null) {
                    Constants.customToast(
                        requireContext(),
                        requireActivity(),
                        it.success.toString()
                    )

                }

            }
        }
    }


    private fun collectStateWishlist() {
        lifecycleScope.launch {
            viewModel.stateWishlist.collect {
                if (it.success != null) {
                    Constants.customToast(
                        requireContext(),
                        requireActivity(),
                        it.success.toString()
                    )
                }

            }
        }
    }

    private fun collectStateRead() {
        lifecycleScope.launch {
            viewModel.stateRead.collect {
                if (it.success != null) {
                    Constants.customToast(
                        requireContext(),
                        requireActivity(),
                        it.success.toString()
                    )
                }

            }
        }
    }

    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.optional_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_whishlist -> {
//                        addWishList()
                        setWishlist()
                        collectStateWishlist()
                        return true
                    }

                    R.id.action_alreadyRead -> {
                        setRead()
                        collectStateRead()
                        return true
                    }

                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun checkFav() {
        lifecycleScope.launch {
            viewModel.getAllFavorite(
                "Bearer ${getToken("userToken")}",
                getUserId(Constants.userId)!!
            )
            viewModel.stateFav.collect {
                if (!it.allLocalBooks.isNullOrEmpty()) {
                    // check if id in list
                    binding.imageViewAnimation.isSelected =
                        it.allLocalBooks.contains(data.bookObject.toBookEntity(getUserId(Constants.userId)!!))
                }

                if (it.allLocalBooks != null) {
                    if (it.allLocalBooks.any { it.bookId == data.bookObject.bookId }) {
                        binding.imageViewAnimation.isSelected = true

                    }
                }


//                if (it.isLoading){
//                    Constants.showCustomAlertDialog(requireContext(), R.layout.custom_alert_dailog, false)
//                }else{
//                    Constants.hideCustomAlertDialog()
//                    binding.container.visibility = View.VISIBLE
//                }


            }
        }
    }

    private fun showMoreAndLess() {
        ShowMoreLess.Builder(requireContext())
            .textLengthAndLengthType(length = 5, textLengthType = ShowMoreLess.TYPE_LINE)
            .showMoreLabel("Show More")
            .showLessLabel("Show Less")
            .showMoreLabelColor(R.color.brown2)
            .showLessLabelColor(R.color.brown2)
            .labelBold(true)
            .expandAnimation(true)
            .enableLinkify(false)
            .textClickable(textClickableInExpand = true, textClickableInCollapse = true)
            .build().apply {
                addShowMoreLess(
                    binding.txtDescription,
                    data.bookObject.description.toString(),
                    isContentExpanded = false
                )
                setListener(object : ShowMoreLess.OnShowMoreLessClickedListener {
                    override fun onShowLessClicked() {
                        addShowMoreLess(
                            binding.txtDescription,
                            data.bookObject.description.toString(),
                            isContentExpanded = false
                        )
                    }

                    override fun onShowMoreClicked() {
                        addShowMoreLess(
                            binding.txtDescription,
                            data.bookObject.description.toString(),
                            isContentExpanded = true
                        )
                    }

                })
            }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    private fun collectRecommend(){
        lifecycleScope.launch{
            viewModel.getItemBasedRecommend(data.bookObject.title.toString())

            viewModel.stateRecommend.collect{
                if (it.isLoading){
                    Constants.showCustomAlertDialog(requireActivity(), R.layout.custom_alert_dailog, false)
                }else{
                    Constants.hideCustomAlertDialog()
                }

//                binding.progress.isVisible = it.isLoading
                Log.e( "bind: ",it.allBooks.toString() )
                if (!it.allBooks.isNullOrEmpty()){
                    adapter.submitList(it.allBooks)
                    binding.recyclerViewRecommend.adapter = adapter
                }
            }
        }
    }


    private fun addToCart(isbn: String){
        binding.btnAddToCart.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, " ${isbn} buy")
            startActivity(intent)

        }
    }

}