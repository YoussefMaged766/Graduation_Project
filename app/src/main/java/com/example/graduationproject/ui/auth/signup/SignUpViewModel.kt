package com.example.graduationproject.ui.auth.signup

import android.content.Context
import android.net.Uri
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.R
import com.example.graduationproject.models.GenerationCodeResponse
import com.example.graduationproject.models.User
import com.example.graduationproject.models.UserResponseSignUp
import com.example.graduationproject.data.repository.UserRepo
import com.example.graduationproject.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val userRepo: UserRepo) : ViewModel() {

//    suspend fun signUpUser(user: User) = userRepo.signUpUser(user)

    private val _response = Channel<UserResponseSignUp>()
    val response = _response.receiveAsFlow()

    private val _progress = Channel<Boolean>()
    val progress = _progress.receiveAsFlow()

    private val eventChannel = Channel<Int>()

    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()


    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    private val _fileName = MutableLiveData("")

    // new added
    val fileName: LiveData<String>
        get() = _fileName

    // new added
    fun setFileName(name:String) {
        _fileName.value = name
    }
    fun signUpUser(fileUri: Uri,
                   fileRealPath: String, firstName: String, lastName:String, email:String, password:String , cnx:Context) = viewModelScope.launch(Dispatchers.IO) {

        userRepo.signUpUser(fileUri, fileRealPath, firstName, lastName, email, password , cnx).collect {
            when (it) {
                is  Status.Loading -> {
                    _progress.send(true)
                }

                is Status.Success -> {
                    _progress.send(false)
                    _response.send(it.data!!)
                    eventChannel.send(R.id.action_signUpFragment_to_loginFragment)

                }

                is Status.Error -> {
                    _progress.send(false)
                    _error.send(it.message.toString())

                }


            }

        }

    }

}