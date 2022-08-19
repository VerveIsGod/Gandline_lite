package com.example.gandline.objects

data class Account(
    val email: String,
    val nickname: String,
    val password: String,
    val sid: String,
    val uid: String,
    val auid: String,
    val aminoId: String,
    val isAminoIdEditable: Boolean,
    val createdTime: String,
    val phoneNumber: String?,
    val secret: String,
    val authorizedDeviceId: String
)