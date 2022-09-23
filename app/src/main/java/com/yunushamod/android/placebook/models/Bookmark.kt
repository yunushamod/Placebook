package com.yunushamod.android.placebook.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Bookmark(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        var placeId: String? = null,
        var name: String = "",
        var address: String = "",
        var phone: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
) {
}