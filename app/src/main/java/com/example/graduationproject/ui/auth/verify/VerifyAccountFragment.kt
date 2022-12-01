package com.example.graduationproject.ui.auth.verify

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.constants.Constants
import com.example.graduationproject.constants.Constants.Companion.dataStore
import com.example.graduationproject.constants.Constants.Companion.validateEmail
import com.example.graduationproject.databinding.FragmentVerifyAccountBinding

import com.example.graduationproject.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VerifyAccountFragment : Fragment() {

    lateinit var binding: FragmentVerifyAccountBinding
    private lateinit var dataStore: DataStore<Preferences>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVerifyAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validateBtn()
        binding.btnVerify.setOnClickListener {
            lifecycleScope.launch {
                if (codeValidation()) {
                    verify()

                }
            }
        }


    }

    suspend fun verify() {
        val txtCode = binding.txtCode.text.toString().toInt()
        withContext(Dispatchers.Main){
            binding.frameLoading.visibility =View.VISIBLE
            delay(500)
        }
        if (getCode("code") == txtCode) {
            Constants.customToast(requireContext(), requireActivity(), "Verified Success")
            findNavController().navigate(R.id.action_verifyAccountFragment_to_loginFragment)
            binding.frameLoading.visibility =View.GONE
            clearDataStore()
        } else {
            Constants.customToast(requireContext(), requireActivity(), "Code Isn't Correct")
            binding.frameLoading.visibility =View.GONE
        }
    }


    private suspend fun getCode(key: String): Int? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }

    private fun validateBtn() {
        binding.txtCode.doOnTextChanged { s, _, _, _ ->
            if (s?.length == 6) {
                binding.txtCodeContainer.error = null
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

    private fun codeValidation(): Boolean {
        if (!binding.txtCode.text.isNullOrEmpty() && binding.txtCode.text!!.length == 6) {
            return true
        }
        if (binding.txtCode.text!!.length < 6) binding.txtCodeContainer.error =
            "Code Must be 6 Numbers"

        binding.txtCode.doOnTextChanged { _, _, _, _ ->
            binding.txtCodeContainer.error = null

        }

        return false
    }

    private suspend fun clearDataStore(){

        dataStore = requireContext().dataStore
        dataStore.edit {
            it.clear()
        }
    }

}