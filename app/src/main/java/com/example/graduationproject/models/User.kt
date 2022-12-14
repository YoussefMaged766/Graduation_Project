package com.example.graduationproject.models

import com.google.gson.annotations.SerializedName

data class User(
    val email: String="",
    val password: String="",
    val confirmPassword: String?=null,
    val firstName: String?=null,
    val lastName: String?=null,



)

data class UserResponseSignUp(

    @field:SerializedName("savedUser")
    val savedUser: SavedUser? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
val data: List<DataItem?>? = null,
)

data class SavedUser(

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("confirmPassword")
    val confirmPassword: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)


data class DataItem(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("param")
    val param: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("value")
    val value: String? = null
)


data class UserResponseLogin(

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)