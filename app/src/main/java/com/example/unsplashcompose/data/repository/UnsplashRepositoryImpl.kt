package com.example.unsplashcompose.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.unsplashcompose.data.local.database.UnsplashDatabase
import com.example.unsplashcompose.data.paging.SearchPagingSource
import com.example.unsplashcompose.data.paging.UnsplashRemoteMediator
import com.example.unsplashcompose.data.remote.UnsplashService
import com.example.unsplashcompose.model.UnsplashImage
import com.example.unsplashcompose.util.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class UnsplashRepositoryImpl @Inject constructor(
    private val unsplashService: UnsplashService,
    private val unsplashDatabase: UnsplashDatabase
) : UnsplashRepository {

    override fun getAllImages(): Flow<PagingData<UnsplashImage>> {
        val pagingSourceFactory = { unsplashDatabase.unsplashImageDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = Constants.ITEM_PER_PAGE),
            remoteMediator = UnsplashRemoteMediator(
                unsplashService = unsplashService,
                unsplashDatabase = unsplashDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun searchImages(query: String): Flow<PagingData<UnsplashImage>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                SearchPagingSource(unsplashService = unsplashService, query = query)
            }
        ).flow
}