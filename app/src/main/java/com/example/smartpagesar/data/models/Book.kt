package com.example.smartpagesar.data.models

import kotlinx.serialization.Serializable


@Serializable
data class Book (
    val id: String,
    val short_id: Int,
    val title: String,
    val author: String,
    val subject: String? = null,
    val chapters: Int,
    val year: Int,
    val image: String? = null
)
