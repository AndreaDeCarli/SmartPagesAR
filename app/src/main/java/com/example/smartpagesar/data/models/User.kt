package com.example.smartpagesar.data.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.generateV7().toString(),
    val name: String? = null,
    val role: String? = null,
)