package com.example.graduationproject.utils

//enum class Status1 {
//    SUCCESS,
//    ERROR,
//    LOADING,
//    NO_DATA
//}

sealed class Status<out T>{
    data class Success<T>(val data :T): Status<T>()
    data class Error(val message: String): Status<Nothing>()
    object Loading: Status<Nothing>()
}