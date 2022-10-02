package com.yunushamod.android.placebook.db

import androidx.room.TypeConverter
import java.util.*

class BookmarkTypeConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }
    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }
}