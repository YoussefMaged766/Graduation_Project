package com.example.graduationproject.ui.auth.forgetpassword

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.databinding.FragmentForgetPasswordBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {

    lateinit var binding: FragmentForgetPasswordBinding
    val viewModel: ForgetPasswordViewModel by viewModels()
    lateinit var dataStore: DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFindAccount.setOnClickListener {
            lifecycleScope.launch {
                if (emailValidation(getUserData())) {
                    viewModel.forgetPassword(getUserData())
                    collectResponse()
                    collectProgress()
                    collectError()

                }

            }
        }
        validateBtn()

    }

    private fun getUserData(): User {
        val email = binding.txtEmail.text?.trim().toString()
        return User(email)
    }

//    fun sendEmail(user: User) {
//        viewModel.forgetPassword(user).collect {
//            it.let {
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        Constants.customToast(
//                            requireContext(),
//                            requireActivity(),
//                            it.data?.body()?.message.toString()
//                        )
//                        saveCode("code", it.data?.body()?.code!!)
//                        saveToken("token",it.data?.body()?.token!!)
//                        binding.frameLoading.visibility = View.GONE
//                    }
//                    Status.LOADING -> {
//                        binding.frameLoading.visibility = View.VISIBLE
//                    }
//                    Status.ERROR -> {
//                        binding.frameLoading.visibility = View.GONE
//                        Constants.customToast(
//                            requireContext(),
//                            requireActivity(),
//                            it.message.toString()
//                        )
//                        Log.d(TAG, "sendEmail: ${it.message.toString()}")
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }

    private suspend fun collectResponse() {
        viewModel.response.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                Constants.customToast(
                    requireContext(),
                    requireActivity(),
                    it.message.toString()
                )

                saveCode("code", it.code!!)
                saveToken("token", it.token!!)
                Log.e("collectResponse: ", it.toString())
                viewModel.eventFlow.collect {
                    findNavController().navigate(it)
                }
            }

        })
    }


    private suspend fun collectProgress() {
        viewModel.progress.collect {
            binding.frameLoading.visibility = it
        }

    }

    private suspend fun collectError() {
        viewModel.error.collect {
            Constants.customToast(requireContext(), requireActivity(), it)
        }
    }


    private suspend fun saveCode(key: String, value: Int) {
        dataStore = requireContext().dataStore
        val dataStoreKey = intPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    private suspend fun saveToken(key: String, value: String) {
        dataStore = requireContext().dataStore
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    private fun emailValidation(user: User): Boolean {
        if (user.email!!.validateEmail()) {
            return true
        }
        if (user.email.validateEmail()) binding.txtEmailContainer.error =
            "Please enter a valid E-mail"

        binding.txtEmail.doOnTextChanged { _, _, _, _ ->
            binding.txtEmailContainer.error = null

        }

        return false
    }

    private fun validateBtn() {
        binding.txtEmail.doOnTextChanged { s, _, _, _ ->
            if (s?.length != 0) {
                binding.txtEmailContainer.error = null
                binding.btnFindAccount.setBackgroundResource(R.drawable.checkbox_checked)
                binding.btnFindAccount.isEnabled = true
                binding.btnFindAccount.setTextColor(Color.WHITE)
            } else {
                binding.btnFindAccount.setBackgroundResource(R.drawable.edittext_border)
                binding.btnFindAccount.isEnabled = false
                binding.btnFindAccount.setTextColor(resources.getColor(R.color.validateTextBButton))
            }

        }
    }
}