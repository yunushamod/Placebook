package com.yunushamod.android.placebook.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.yunushamod.android.placebook.db.BookmarkDao
import com.yunushamod.android.placebook.db.PlaceBookDatabase
import com.yunushamod.android.placebook.models.Bookmark
import java.util.*
import java.util.concurrent.Executors

class BookmarkRepository private constructor(context: Context) {
    private val database = Room.databaseBuilder(context.applicationContext, PlaceBookDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()
    private val bookmarkDao: BookmarkDao = database.bookmarkDao()

    fun addBookmark(bookmark: Bookmark) = bookmarkDao.insertBookmark(bookmark)
    fun getBookmark(bookmarkId: UUID) = bookmarkDao.loadBookmark(bookmarkId)

    fun updateBookmark(bookmark: Bookmark) = bookmarkDao.updateBookmark(bookmark)

    fun deleteBookmark(bookmark: Bookmark) = bookmarkDao.deleteBookmark(bookmark)

    fun createBookmark() = Bookmark()

    val allBookmarks: LiveData<List<Bookmark>>
    get() = bookmarkDao.loadAll()
    fun getLiveBookmark(bookmarkId: UUID): LiveData<Bookmark>
    = bookmarkDao.loadLiveBookmark(bookmarkId)
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