package com.yunushamod.android.placebook.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.yunushamod.android.placebook.models.Bookmark
import com.yunushamod.android.placebook.repositories.BookmarkRepository

class MapViewModel : ViewModel() {
    private val bookmarkRepo = BookmarkRepository.getInstance()
    private var bookmarks: LiveData<List<BookmarkMarkerView>>? = null
    fun addBookmarkFromPlace(place: Place, image: Bitmap?){
        val bookmark = bookmarkRepo.createBoookmark()
        bookmark.placeId = place.id
        bookmark.name = place.name.toString()
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.latitude = place.latLng?.latitude ?: 0.0
        bookmark.phone = place.phoneNumber?.toString() ?: ""
        bookmark.address = place.address
        bookmarkRepo.addBookmark(bookmark)
        Log.i(TAG, "New bookmark added to database. Id: ${bookmark.id}")
    }

    private fun bookmarkToMarkerView(bookmark: Bookmark){
        BookmarkMarkerView(bookmark.id, LatLng(bookmark.latitude, bookmark.longitude))
    }

    private fun mapBookmarksToMarkerView(){
        bookmarks = Transformations.map(bookmarkRepo.allBookmarks){
            it?.let{boomarks ->
                bookmarks.map{
                        bookmark -> {
                            bookmarkToMarkerView(bookmark)
                    }
                }
            }
        }
    }

    fun getBookmarkMarkerView(): LiveData<List<BookmarkMarkerView>>?{
        if(bookmarks == null) mapBookmarksToMarkerView()
        return bookmarks
    }

    private val TAG = "Map_View_Model"
    data class BookmarkMarkerView(var id: Long? = null, var location: LatLng = LatLng(0.0, 0.0))
}