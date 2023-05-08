package com.example.graduationproject.ui.auth.forgetpassword

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

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


        onClicks()
        validateBtn()
        collectResponse()
        collectProgress()
        collectError()

    }

    private fun onClicks() {
        binding.apply {
            btnFindAccount.setOnClickListener {
                if (emailValidation(getUserData())) {
                    viewModel.forgetPassword(getUserData())

                }
            }
        }
    }

    private fun getUserData(): User {
        val email = binding.txtEmail.text?.trim().toString()
        return User(email)
    }

    private fun collectResponse() {
        lifecycleScope.launch {
            viewModel.response.collect {

                Constants.customToast(
                    requireContext(),
                    requireActivity(),
                    it.message.toString()
                )

                it.code?.let { it1 -> saveCode("code", it1) }
                it.token?.let { it1 -> saveToken("token", it1) }
//                viewModel.eventFlow.collect {
//                    findNavController().navigate(it)
//                }
                findNavController().navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToVerifyAccountFragment(getUserData()))


            }
        }
    }


    private fun collectProgress() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.progress.collectLatest {
                    if (it) Constants.showCustomAlertDialog(requireActivity(),R.layout.custom_alert_dailog,false)
                    else Constants.hideCustomAlertDialog()
//                    binding.frameLoading.isVisible = it
                    Log.i(TAG, "collectProgress: $it")
                }
            }
        }


    }

    private fun collectError() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.error.collect {
                    Constants.customToast(requireContext(), requireActivity(), it)
                }
            }
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
        if (!user.email.validateEmail()) binding.txtEmailContainer.error =
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