package com.yunushamod.android.placebook.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.yunushamod.android.placebook.databinding.ContentBookmarkInfoBinding

class BookmarkWindowInfoAdapter(context: Activity) : GoogleMap.InfoWindowAdapter{
    private val binding: ContentBookmarkInfoBinding = ContentBookmarkInfoBinding.inflate(context.layoutInflater)
    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        binding.title.text = p0.title ?: ""
        binding.phone.text = p0.snippet ?: ""
        val imageView = binding.photo
        imageView.setImageBitmap((p0.tag as Bitmap))
        return binding.root
    }

}