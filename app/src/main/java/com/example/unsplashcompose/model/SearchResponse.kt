package com.example.unsplashcompose.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchResponse(
    @SerialName("results")
    val images: List<UnsplashImage>
)
