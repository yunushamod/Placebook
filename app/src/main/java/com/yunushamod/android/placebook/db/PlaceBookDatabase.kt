package com.yunushamod.android.placebook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yunushamod.android.placebook.models.Bookmark

@Database(entities = [Bookmark::class], version = 2)
@TypeConverters(BookmarkTypeConverter::class)
abstract class PlaceBookDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
