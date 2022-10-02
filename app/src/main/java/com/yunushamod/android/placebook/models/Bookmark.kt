package com.yunushamod.android.placebook.models

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yunushamod.android.placebook.utils.ImageUtils
import java.util.*

@Entity
class Bookmark(
        @PrimaryKey var id: UUID = UUID.randomUUID(),
        var placeId: String? = null,
        var name: String = "",
        var address: String = "",
        var phone: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var notes: String = ""
) {
        fun setImage(image: Bitmap, context: Context){
                ImageUtils.saveBitmapToFile(context, image, generateFileName(id))
        }

        companion object{
                fun generateFileName(id: UUID): String{
                        return "bookmark${id}.png"
                }
        }
}