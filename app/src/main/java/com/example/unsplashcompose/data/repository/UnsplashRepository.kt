package com.example.unsplashcompose.data.repository

import androidx.paging.PagingData
import com.example.unsplashcompose.model.UnsplashImage
import kotlinx.coroutines.flow.Flow

interface UnsplashRepository {
    fun getAllImages(): Flow<PagingData<UnsplashImage>>
    fun searchImages(query: String): Flow<PagingData<UnsplashImage>>
}