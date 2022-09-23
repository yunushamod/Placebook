package com.yunushamod.android.placebook

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.yunushamod.android.placebook.repositories.BookmarkRepository

class PlacesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        BookmarkRepository.initialize(this)
    }
}