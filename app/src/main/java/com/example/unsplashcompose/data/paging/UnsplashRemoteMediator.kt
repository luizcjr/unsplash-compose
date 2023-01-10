package com.example.unsplashcompose.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.unsplashcompose.data.local.database.UnsplashDatabase
import com.example.unsplashcompose.data.remote.UnsplashService
import com.example.unsplashcompose.model.UnsplashImage
import com.example.unsplashcompose.model.UnsplashRemoteKeys
import com.example.unsplashcompose.util.Constants

@ExperimentalPagingApi
class UnsplashRemoteMediator(
    private val unsplashService: UnsplashService,
    private val unsplashDatabase: UnsplashDatabase
) : RemoteMediator<Int, UnsplashImage>() {

    private val unsplashImageDao = unsplashDatabase.unsplashImageDao()
    private val unsplashRemoteKeyDao = unsplashDatabase.unsplashRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashImage>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    nextPage
                }
            }

            val response =
                unsplashService.getAllImages(page = currentPage, perPage = Constants.ITEM_PER_PAGE)
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            unsplashDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    unsplashImageDao.deleteAllImages()
                    unsplashRemoteKeyDao.deleteAllRemoteKeys()
                }

                val keys = response.map { unsplashImage ->
                    UnsplashRemoteKeys(
                        id = unsplashImage.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                unsplashRemoteKeyDao.addAllRemoteKeys(keys)
                unsplashImageDao.addAllImages(images = response)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                unsplashRemoteKeyDao.getRemoteKeys(id = id)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKeys(id = unsplashImage.id)
            }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKeys(id = unsplashImage.id)
            }
}