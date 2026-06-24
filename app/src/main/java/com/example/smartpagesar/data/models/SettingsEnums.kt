package com.example.smartpagesar.data.models

import com.example.smartpagesar.R

enum class Theme(val label: Int) {
    Light (R.string.theme_light),
    Dark (R.string.theme_dark),
    System (R.string.theme_system)
}

enum class Lighting(val label: Int) {
    ENVIRONMENTAL_HDR(R.string.lighting_env_hdr),
    AMBIENT_INTENSITY(R.string.lighting_ambient),
    DISABLED(R.string.generic_disabled)
}