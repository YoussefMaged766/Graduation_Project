package com.example.graduationproject.ui.auth.login

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.constants.Constants.Companion.validatePass
import com.example.graduationproject.databinding.FragmentLoginBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.ui.main.MainActivity
import com.example.graduationproject.ui.main.home.HomeActivity
import com.example.graduationproject.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    val viewModel: LoginViewModel by viewModels()
    private lateinit var dataStore: DataStore<Preferences>
    private var RUN_ONCE = true

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
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ocClicks()
        collectResponse()
        collectProgress()
        collectError()
        validateBtn()
        animation()
        lifecycleScope.launch {
            delay(5000L)
            handleCheckBox("hello")

        }
        if (RUN_ONCE) {
            RUN_ONCE = false
            lifecycleScope.launch {
                delay(1000L)
                saveIsLogging("Logging", RUN_ONCE)
            }
        }


    }

    private fun ocClicks() {
        binding.apply {
            btnSignIn.setOnClickListener {
                if (emailAndPassValidation(getUserData()))
                    viewModel.loginUser(
                        getUserData(),
                        requireContext(),
                        HomeActivity::class.java,
                        requireActivity()
                    )

            }
            forgetPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)

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
                Log.e("collectResponse: ", it.toString())
//                handleCheckBox(it.token.toString())
            }
        }
    }

    private fun collectProgress() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.progress.collectLatest {
                    binding.frameLoading.isVisible = it
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

    private fun getUserData(): User {
        val email = binding.txtEmail.text?.trim().toString()
        val password = binding.txtPassword.text.toString().trim()
        return User(email, password)
    }

    private fun emailAndPassValidation(user: User): Boolean {
        if (user.email!!.validateEmail() && user.password!!.validatePass()) {
            return true
        }
        if (!user.email.validateEmail()) binding.txtEmailContainer.error =
            "Please enter a valid E-mail"
        if (!user.password!!.validatePass()) binding.txtPasswordContainer.error =
            "password should be at least 6 letters or numbers"
        binding.txtEmail.doOnTextChanged { _, _, _, _ ->
            binding.txtEmailContainer.error = null

        }
        binding.txtPassword.doOnTextChanged { _, _, _, _ ->
            binding.txtPasswordContainer.error = null
        }
        return false
    }

    private fun validateBtn() {
        binding.txtPassword.doOnTextChanged { s, _, _, _ ->
            if (s?.length != 0) {
                binding.txtPasswordContainer.error = null
                binding.btnSignIn.setBackgroundResource(R.drawable.checkbox_checked)
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.setTextColor(Color.WHITE)
            } else {
                binding.btnSignIn.setBackgroundResource(R.drawable.edittext_border)
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.setTextColor(resources.getColor(R.color.validateTextBButton))
            }

        }
    }

    private fun animation() {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.ftb)
        val an2 = AnimationUtils.loadAnimation(requireContext(), R.anim.fendtostart)
        val an3 = AnimationUtils.loadAnimation(requireContext(), R.anim.fstarttoendt)
        val an4 = AnimationUtils.loadAnimation(requireContext(), R.anim.frombottomtotop)
        binding.imgLogo.startAnimation(an)
        binding.txtWelcome.startAnimation(an)
        binding.txtLogin.startAnimation(an)
        binding.txtEmailContainer.startAnimation(an2)
        binding.txtPasswordContainer.startAnimation(an3)
        binding.checkBox.startAnimation(an4)
        binding.forgetPassword.startAnimation(an4)
        binding.btnSignIn.startAnimation(an4)

    }

    private suspend fun handleCheckBox(value: String) {

        if (binding.checkBox.isChecked) {
            saveToken("userToken", value)
        }

    }

    private suspend fun saveToken(key: String, value: String) {
        dataStore = requireContext().dataStore
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            dataStore = requireContext().dataStore
            val dataStoreKey = stringPreferencesKey("userToken")
            dataStore.edit {
                if (it.contains(dataStoreKey)) {
                    binding.frameLoading.visibility = View.VISIBLE
                    delay(1000L)
                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                    activity?.finish()
                    Constants.customToast(requireContext(), requireActivity(), "Welcome back")
                } else {
                        if (getIsLogging("Logging") != false) {
                            Constants.customToast(requireContext(), requireActivity(), "Welcome ")
                            Log.e( "onStart: ",getIsLogging("Logging").toString() )
                        }else{
                            binding.frameLoading.visibility = View.VISIBLE
                            delay(1000L)
                            binding.frameLoading.visibility = View.GONE
                            Constants.customToast(requireContext(), requireActivity(), "isn't sign in")
                            Log.e("onStartafter: ", RUN_ONCE.toString())
                        }

                }
            }
        }


    }

    private suspend fun getIsLogging(key: String): Boolean? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Boolean> = booleanPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private suspend fun saveIsLogging(key: String, value: Boolean) {
        dataStore = requireContext().dataStore
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

}