package com.example.smartpagesar.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserBook (
    val book_id: String,
    val user_id: String
)