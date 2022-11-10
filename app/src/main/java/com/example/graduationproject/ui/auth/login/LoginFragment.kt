package com.example.graduationproject.ui.auth.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.constants.Constants.Companion.validatePass
import com.example.graduationproject.databinding.FragmentLoginBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.ui.main.home.HomeActivity
import com.example.graduationproject.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    val viewModel: LoginViewModel by viewModels()


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

        binding.btnSignIn.setOnClickListener {
            lifecycleScope.launch {
                if (emailAndPassValidation(getUserData()))
                    loginUser(getUserData())

            }
        }
        validateBtn()

    }

    suspend fun loginUser(user: User) {
        viewModel.loginUser(user).collect {
            it.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        activity?.finish()
                        Constants.customToast(
                            requireContext(),
                            requireActivity(),
                            it.data?.body()?.message.toString()
                        )
                        binding.frameLoading.visibility = View.GONE

                    }
                    Status.LOADING -> {
                        binding.frameLoading.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        Constants.customToast(
                            requireContext(),
                            requireActivity(),
                            it.message.toString()
                        )
                        binding.frameLoading.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }


    fun getUserData(): User {
        val email = binding.txtEmail.text?.trim().toString()
        val password = binding.txtPassword.text.toString().trim()
        return User(email, password)
    }

    private fun emailAndPassValidation(user: User): Boolean {
        if (user.email.validateEmail() && user.password.validatePass()) {
            return true
        }
        if (!user.email.validateEmail()) binding.txtEmailContainer.error =
            "Please enter a valid E-mail"
        if (!user.password.validatePass()) binding.txtPasswordContainer.error =
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

}