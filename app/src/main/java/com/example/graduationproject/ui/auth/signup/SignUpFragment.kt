package com.example.graduationproject.ui.auth.signup

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.constants.Constants.Companion.validateFirstname
import com.example.graduationproject.constants.Constants.Companion.validateLastname
import com.example.graduationproject.constants.Constants.Companion.validatePass
import com.example.graduationproject.databinding.FragmentSignUpBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    val viewModel: SignUpViewModel by viewModels()

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
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            lifecycleScope.launch {
                if (userDataValidation(getUserData())) {
                    signUpUser(getUserData())
                }else{
                    Constants.customToast(requireContext(),requireActivity(),"error")
                }
            }
        }
        validateBtn()
            }

            suspend fun signUpUser(user: User) {
                viewModel.signUpUser(user).collect {
                    it.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                                Constants.customToast(
                                    requireContext(), requireActivity(),
                                    it.data?.body()?.message.toString()
                                )
                                binding.frameLoading.visibility = View.GONE
                            }
                            Status.LOADING -> {
                                binding.frameLoading.visibility = View.VISIBLE
                            }
                            Status.ERROR -> {
                                Constants.customToast(
                                    requireContext(), requireActivity(),
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
                val firstName = binding.txtFirstName.text?.trim().toString()
                val lastName = binding.txtLastName.text.toString().trim()
                val confirmPassword = binding.txtPasswordConfirm.text?.trim().toString()

                return User(email, password, confirmPassword, firstName, lastName)
            }


            fun userDataValidation(user: User): Boolean {
                if (user.email.validateEmail() && user.password.validatePass() && user.password == user.confirmPassword
                    && user.firstName?.validateFirstname()!! && user.lastName?.validateLastname()!!
                ) {
                    return true
                }
                if (!user.email.validateEmail()) binding.txtEmailContainer.error =
                    "Please enter a valid E-mail"
                if (!user.password.validatePass()) binding.txtPasswordContainer.error =
                    "password should be at least 6 letters or numbers"
                if (!user.confirmPassword?.validatePass()!!) binding.txtPasswordConfirmContainer.error =
                    "confirm password is not equal to password "
                if (!user.firstName?.validateFirstname()!!) binding.txtFirstNameContainer.error =
                    "Firstname should be at least 3 letters "

                if (!user.lastName?.validateLastname()!!) binding.txtLastNameContainer.error =
                    "Lastname should be at least 4 letters "

                binding.txtEmail.doOnTextChanged { _, _, _, _ ->
                    binding.txtEmailContainer.error = null
                }
                binding.txtPassword.doOnTextChanged { _, _, _, _ ->
                    binding.txtPasswordContainer.error = null
                }
                binding.txtPasswordConfirm.doOnTextChanged { _, _, _, _ ->
                    binding.txtPasswordConfirmContainer.error = null
                }
                binding.txtFirstName.doOnTextChanged { _, _, _, _ ->
                    binding.txtFirstNameContainer.error = null
                }
                binding.txtLastName.doOnTextChanged { _, _, _, _ ->
                    binding.txtLastNameContainer.error = null
                }
                return false
            }


            fun validateBtn() {
                binding.txtPasswordConfirm.doOnTextChanged { s, _, _, _ ->
                    if (s?.length != 0) {
                        binding.txtPasswordContainer.error = null
                        binding.btnSignUp.setBackgroundResource(R.drawable.checkbox_checked)
                        binding.btnSignUp.isEnabled = true
                        binding.btnSignUp.setTextColor(Color.WHITE)
                    } else {
                        binding.btnSignUp.setBackgroundResource(R.drawable.edittext_border)
                        binding.btnSignUp.isEnabled = false
                        binding.btnSignUp.setTextColor(resources.getColor(R.color.validateTextBButton))
                    }

                }
            }
        }