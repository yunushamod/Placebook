package com.yunushamod.android.placebook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yunushamod.android.placebook.models.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class PlaceBookDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}