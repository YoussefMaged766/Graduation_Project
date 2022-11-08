package com.example.graduationproject.models

data class User(
    val email: String?=null,
    val password: String?=null,
    val confirmPassword: String?=null,
    val firstName: String?=null,
    val lastName: String?=null
)