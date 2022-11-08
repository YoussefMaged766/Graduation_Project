package com.example.graduationproject.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.databinding.FragmentLoginBinding
import com.example.graduationproject.models.User
import com.example.graduationproject.ui.main.home.HomeActivity
import com.example.graduationproject.utils.Status
import kotlinx.coroutines.launch


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
                loginUser(getUserData())

            }
        }


    }

    suspend fun loginUser(user: User) {
        viewModel.loginUser(user).collect {
            it.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        Toast.makeText(requireContext(), it.data?.body()?.message.toString(), Toast.LENGTH_SHORT).show()

                    }
                    Status.LOADING -> {}
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
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

}