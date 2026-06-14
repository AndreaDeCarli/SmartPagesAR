package com.example.smartpagesar.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val role: String? = null,
    val created_at: String? = null,
)