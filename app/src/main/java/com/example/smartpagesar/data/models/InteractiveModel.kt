package com.example.smartpagesar.data.models

import kotlinx.serialization.Serializable

@Serializable
data class InteractiveModel(
    val book_id: String,
    val id: String,
    val model: String,
    val image: String
)