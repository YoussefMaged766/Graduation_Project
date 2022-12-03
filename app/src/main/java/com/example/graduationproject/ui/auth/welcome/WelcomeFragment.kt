package com.example.graduationproject.ui.auth.welcome


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.databinding.FragmentWelcomeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding
    lateinit var dataStore: DataStore<Preferences>
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
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
        Log.e( "onViewCreated1: ",RUN_ONCE.toString() )
        if (RUN_ONCE) {
            RUN_ONCE = false
            lifecycleScope.launch {
                saveIsLogging("isLogging", RUN_ONCE)
                Log.e( "onViewCreated2: ",RUN_ONCE.toString() )
            }
        }


    }

    private suspend fun saveIsLogging(key: String, value: Boolean) {
        dataStore = requireContext().dataStore
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    override fun onStart() {
        super.onStart()
        dataStore = requireContext().dataStore
        lifecycleScope.launch {
            Log.e( "onViewCreated3: ","start")
            if (getIsLogging("isLogging") == false) {
                binding.constraint.visibility = View.GONE
                Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
        }
        Log.e( "onViewCreated4: ","start")
    }

    override fun onResume() {
        super.onResume()
        Log.e( "onViewCreated5: ","onResume" )
    }

    private suspend fun getIsLogging(key: String): Boolean? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Boolean> = booleanPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

}