package com.yunushamod.android.placebook.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.yunushamod.android.placebook.models.Bookmark
import com.yunushamod.android.placebook.repositories.BookmarkRepository
import com.yunushamod.android.placebook.utils.ImageUtils
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BookmarkDetailViewModel(application: Application): AndroidViewModel(application) {
    private val bookmarkRepository:  BookmarkRepository = BookmarkRepository.getInstance()
    private var bookmarkDetailsView: LiveData<BookmarkView>? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private fun mapBookmarkToBookmarkView(bookmarkId: UUID){
        val bookmark = bookmarkRepository.getLiveBookmark(bookmarkId)
        bookmarkDetailsView = Transformations.map(bookmark){
            bookmarkToBookmarkView(it)
        }
    }

    fun getBookmark(bookmarkId: UUID): LiveData<BookmarkView>?{
        if(bookmarkDetailsView == null) mapBookmarkToBookmarkView(bookmarkId)
        return bookmarkDetailsView
    }

    private fun bookmarkToBookmarkView(bookmark: Bookmark): BookmarkView{
        return BookmarkView(
            bookmark.id,
            bookmark.name,
            bookmark.phone,
            bookmark.address,
            bookmark.notes
        )
    }

    fun updateBookmark(bookmarkView: BookmarkView){
        executor.execute{
            val bookmark = bookmarkViewToBookmark(bookmarkView)
            bookmark?.let {
                bookmarkRepository.updateBookmark(it)
            }
        }
    }


    private fun bookmarkViewToBookmark(bookmarkView: BookmarkView): Bookmark?{
        val bookmark = bookmarkView.id?.let{
            bookmarkRepository.getBookmark(it)
        }
        if(bookmark != null){
            bookmark.id = bookmarkView.id!!
            bookmark.name = bookmarkView.name
            bookmark.phone = bookmark.phone
            bookmark.address = bookmark.address
            bookmark.notes = bookmark.notes
        }
        return bookmark
    }

    data class BookmarkView(
        var id: UUID? = null,
        var name: String = "",
        var phone: String = "",
        var address: String = "",
        var notes: String = ""
    ){
        fun getImage(context: Context): Bitmap?{
            return id?.let {
                ImageUtils.loadBitmapFromFile(context, Bookmark.generateFileName(it))
            }
        }
    }
}