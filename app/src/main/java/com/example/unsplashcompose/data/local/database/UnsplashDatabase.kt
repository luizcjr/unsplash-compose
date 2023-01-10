package com.example.unsplashcompose.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unsplashcompose.data.local.dao.UnsplashImageDao
import com.example.unsplashcompose.data.local.dao.UnsplashRemoteKeysDao
import com.example.unsplashcompose.model.UnsplashImage
import com.example.unsplashcompose.model.UnsplashRemoteKeys

@Database(entities = [UnsplashImage::class, UnsplashRemoteKeys::class], version = 1)
abstract class UnsplashDatabase: RoomDatabase() {

    abstract fun unsplashImageDao() : UnsplashImageDao
    abstract fun unsplashRemoteKeysDao() : UnsplashRemoteKeysDao
}