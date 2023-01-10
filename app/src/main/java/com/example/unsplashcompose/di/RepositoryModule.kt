package com.example.unsplashcompose.di

import androidx.paging.ExperimentalPagingApi
import com.example.unsplashcompose.data.repository.UnsplashRepository
import com.example.unsplashcompose.data.repository.UnsplashRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@ExperimentalPagingApi
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindUnsplashRepository(repositoryImpl: UnsplashRepositoryImpl): UnsplashRepository
}