package com.example.graduationproject.ui.auth.verify

import android.content.ContentValues.TAG
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args : VerifyAccountFragmentArgs by navArgs()
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
                verify()
                Log.d( "onCreateView: ", getCode("code").toString())

            }
        }
        binding.txtEmail.text = args.email.email


    }

    private suspend  fun verify() {
        withContext(Dispatchers.Main){
            Constants.showCustomAlertDialog(requireActivity(),R.layout.custom_alert_dailog,false)
//            binding.frameLoading.visibility =View.VISIBLE
            delay(500)
        }
        if (getCode("code").toString() == binding.pin.text.toString()) {
            Constants.customToast(requireContext(), requireActivity(), "Verified Success")
            findNavController().navigate(R.id.action_verifyAccountFragment_to_createNewPasswordFragment)
            Constants.hideCustomAlertDialog()
//            binding.frameLoading.visibility =View.GONE
            clearDataStore()
        } else {
            Constants.customToast(requireContext(), requireActivity(), "Code Isn't Correct")
            Constants.hideCustomAlertDialog()
//            binding.frameLoading.visibility =View.GONE
        }

    }


    private suspend fun getCode(key: String): Int? {
        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey(key)
        val preference = dataStore.data.first()
        return preference[dataStoreKey]
    }
    private fun validateBtn() {
        binding.pin.doOnTextChanged { s, _, _, _ ->
            if (s?.length == 6) {
//                binding.txtCodeContainer.error = null
                binding.btnVerify.setBackgroundResource(R.drawable.checkbox_checked)
                binding.btnVerify.isEnabled = true
                binding.btnVerify.setTextColor(Color.WHITE)
                val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                // on below line hiding our keyboard.
                inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            } else {
                binding.btnVerify.setBackgroundResource(R.drawable.edittext_border)
                binding.btnVerify.isEnabled = false
                binding.btnVerify.setTextColor(resources.getColor(R.color.validateTextBButton))
            }

        }
    }



    private suspend fun clearDataStore(){

        dataStore = requireContext().dataStore
        val dataStoreKey: Preferences.Key<Int> = intPreferencesKey("code")
        dataStore.edit {
            it.remove(dataStoreKey)
        }
    }

}