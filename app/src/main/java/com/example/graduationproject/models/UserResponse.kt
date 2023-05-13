package com.example.graduationproject.models


import com.google.gson.annotations.SerializedName


data class UserResponse(
    @field: SerializedName("message")
    val message: String,
    @field: SerializedName("results")
    val results: ResultsUser?=null
)

data class ResultsUser(
    @field: SerializedName("email")
    val email: String?=null,
    @field: SerializedName("firstName")
    val firstName: String?=null,
    @field: SerializedName("_id")
    val id: String?=null,
    @field: SerializedName("image")
    val image: String?=null,
    @field: SerializedName("lastName")
    val lastName: String?=null


    )