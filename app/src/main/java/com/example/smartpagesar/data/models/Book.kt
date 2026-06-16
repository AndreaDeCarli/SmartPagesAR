package com.example.smartpagesar.data.models



data class Book (
    val id: String,
    val title: String,
    val author: String,
    val subject: String? = null,
    val chapters: Int,
    val year: Int,
    val image: String? = null
)
