package com.example.unsplashcompose.screens.home

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.unsplashcompose.data.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    unsplashRepository: UnsplashRepository
) : ViewModel() {
    val getAllImages = unsplashRepository.getAllImages()
}