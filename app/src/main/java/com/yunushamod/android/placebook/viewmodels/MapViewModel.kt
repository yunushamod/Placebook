package com.yunushamod.android.placebook.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.yunushamod.android.placebook.models.Bookmark
import com.yunushamod.android.placebook.repositories.BookmarkRepository
import com.yunushamod.android.placebook.utils.ImageUtils
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MapViewModel(application: Application): AndroidViewModel(application) {
    private val bookmarkRepo = BookmarkRepository.getInstance()
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var bookmarks: LiveData<List<BookmarkView>>? = null
    fun addBookmarkFromPlace(place: Place, image: Bitmap?){
        executor.execute{
            val bookmark = bookmarkRepo.createBookmark()
            bookmark.placeId = place.id
            bookmark.name = place.name.toString()
            bookmark.longitude = place.latLng?.longitude ?: 0.0
            bookmark.latitude = place.latLng?.latitude ?: 0.0
            bookmark.phone = place.phoneNumber?.toString() ?: ""
            bookmark.address = place.address
            bookmarkRepo.addBookmark(bookmark)
            image?.let {
                bookmark.setImage(it, getApplication())
            }
            Log.i(TAG, "New bookmark added to database. Id: ${bookmark.id}")
        }
    }



    private fun bookmarkToMarkerView(bookmark: Bookmark) : BookmarkView{
        return BookmarkView(bookmark.id, LatLng(bookmark.latitude, bookmark.longitude), bookmark.name,
        bookmark.phone)
    }

    private fun mapBookmarksToMarkerView(){
        bookmarks = Transformations.map(bookmarkRepo.allBookmarks){ list ->
            list?.let{bookmark ->
                bookmark.map {
                    bookmarkToMarkerView(it)
                }
            }
        }
    }

    fun getBookmarkMarkerView(): LiveData<List<BookmarkView>>?{
        if(bookmarks == null) mapBookmarksToMarkerView()
        return bookmarks
    }

    private val TAG = "Map_View_Model"
    data class BookmarkView(var id: UUID? = null, var location: LatLng = LatLng(0.0, 0.0),
                            var name: String, var phone: String){
        fun getImage(context: Context) = id?.let{
            ImageUtils.loadBitmapFromFile(context, Bookmark.generateFileName(it))
        }
    }
}