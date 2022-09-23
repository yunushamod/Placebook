package com.yunushamod.android.placebook.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.yunushamod.android.placebook.db.BookmarkDao
import com.yunushamod.android.placebook.db.PlaceBookDatabase
import com.yunushamod.android.placebook.models.Bookmark
import java.util.concurrent.Executors

class BookmarkRepository private constructor(context: Context) {
    private val executor = Executors.newSingleThreadExecutor()
    private val database = Room.databaseBuilder(context.applicationContext, PlaceBookDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val bookmarkDao: BookmarkDao = database.bookmarkDao()

    fun addBookmark(bookmark: Bookmark) {
        executor.execute{
            bookmarkDao.insertBookmark(bookmark)
        }
    }

    fun updateBookmark(bookmark: Bookmark){
        executor.execute{
            bookmarkDao.updateBookmark(bookmark)
        }
    }

    fun deleteBookmark(bookmark: Bookmark){
        executor.execute{
            bookmarkDao.deleteBookmark(bookmark)
        }
    }

    fun createBoookmark(): Bookmark{
        return Bookmark()
    }

    val allBookmarks: LiveData<List<Bookmark>>
    get() = bookmarkDao.loadAll()

    companion object{
        private const val DATABASE_NAME: String = "Placebook"
        private var INSTANCE: BookmarkRepository? = null
        fun initialize(context: Context){
            val bookmarkRepository = BookmarkRepository(context)
            INSTANCE = bookmarkRepository
        }
        fun getInstance() = INSTANCE ?: throw IllegalStateException("BookmarkRepository cannot be null. Initialize it")
    }
}