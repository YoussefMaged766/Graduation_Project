package com.example.graduationproject.ui.auth.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.databinding.FragmentSignUpBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.utils.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


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
                signUpUser(getUserData())
            }
        }
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
}