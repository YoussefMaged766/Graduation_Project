package com.example.graduationproject.ui.auth.newpassword

import android.content.ContentValues
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.constants.Constants.Companion.validateFirstname
import com.example.graduationproject.constants.Constants.Companion.validateLastname
import com.example.graduationproject.constants.Constants.Companion.validatePass
import com.example.graduationproject.databinding.FragmentCreateNewPasswordBinding
import com.example.graduationproject.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CreateNewPasswordFragment : Fragment() {

    lateinit var binding: FragmentCreateNewPasswordBinding
    val viewModel: NewPasswordViewModel by viewModels()
    lateinit var dataStore: DataStore<Preferences>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateNewPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
        collectResponse()
        collectError()
        collectProgress()
        validateBtn()

    }

    private fun onClicks() {

        binding.apply {
            btnVerify.setOnClickListener {
                lifecycleScope.launch {
                    if (userDataValidation(getUserData())) {
                        viewModel.newPassword(getUserData())
                    }

                }


            }
        }


    }


    private fun collectResponse() {
        lifecycleScope.launch {
            viewModel.response.collect {

                Constants.customToast(
                    requireContext(),
                    requireActivity(),
                    it.message.toString()
                )
                clearDataStore()
                Log.e("collectResponse: ", it.toString())
                viewModel.eventFlow.collect {
                    findNavController().navigate(it)
                }

            }
        }
    }


    private fun collectProgress() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.progress.collectLatest {
//                    binding.frameLoading.isVisible = it
                    if (it)   Constants.showCustomAlertDialog(requireActivity(),R.layout.custom_alert_dailog,false)
                    else Constants.hideCustomAlertDialog()
                    Log.i(ContentValues.TAG, "collectProgress: $it")
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


    private suspend fun getUserData(): User {

        val password = binding.txtPassword.text.toString().trim()
        val confirmPassword = binding.txtPasswordConfirm.text.toString().trim()
        return User(
            password = password,
            resetToken = getToken("token"),
            newPassword = confirmPassword
        )
    }

    private suspend fun getToken(key: String): String? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private fun userDataValidation(user: User): Boolean {
        if (user.password!!.validatePass() && user.password == user.newPassword

        ) {
            return true
        }

        if (!user.password!!.validatePass()) binding.txtPasswordContainer.error =
            "password should be at least 6 letters or numbers"
        if (!user.newPassword?.validatePass()!!) binding.txtPasswordConfirmContainer.error =
            "confirm password is not equal to password "

        if (user.password != user.confirmPassword) binding.txtPasswordConfirmContainer.error =
            "confirm password is not equal to password "


        binding.txtPassword.doOnTextChanged { _, _, _, _ ->
            binding.txtPasswordContainer.error = null
        }
        binding.txtPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            binding.txtPasswordConfirmContainer.error = null
        }

        return false
    }


    private fun validateBtn() {
        binding.txtPasswordConfirm.doOnTextChanged { s, _, _, _ ->
            if (s?.length != 0) {
                binding.txtPasswordContainer.error = null
                binding.btnVerify.setBackgroundResource(R.drawable.checkbox_checked)
                binding.btnVerify.isEnabled = true
                binding.btnVerify.setTextColor(Color.WHITE)
            } else {
                binding.btnVerify.setBackgroundResource(R.drawable.edittext_border)
                binding.btnVerify.isEnabled = false
                binding.btnVerify.setTextColor(resources.getColor(R.color.validateTextBButton))
            }

        }
    }

    private suspend fun clearDataStore() {

        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<String> = stringPreferencesKey("token")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }

}