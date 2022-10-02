package com.yunushamod.android.placebook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yunushamod.android.placebook.R
import com.yunushamod.android.placebook.databinding.BookmarkItemBinding
import com.yunushamod.android.placebook.ui.MapsActivity
import com.yunushamod.android.placebook.viewmodels.BookmarkDetailViewModel
import com.yunushamod.android.placebook.viewmodels.MapViewModel

class BookmarkListAdapter(private var bookmarkData: List<MapViewModel.BookmarkView>?,
private val mapsActivity: MapsActivity) : RecyclerView.Adapter<BookmarkListAdapter.ViewHolder>() {
    class ViewHolder(
        val binding: BookmarkItemBinding,
        private val mapsActivity: MapsActivity
    ): RecyclerView.ViewHolder(binding.root)

    fun setBookmarkData(bookmarks: List<MapViewModel.BookmarkView>){
        this.bookmarkData = bookmarks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = bookmarkData?.size ?: 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookmarkItemBinding.inflate(LayoutInflater.from(parent.context))
        return BookmarkListAdapter.ViewHolder(binding, mapsActivity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarkData?.get(position)
        bookmark?.let {
            holder.binding.root.tag = it
            holder.binding.bookmark = it
            holder.binding.bookmarkIcon.setImageResource(R.drawable.ic_other)
        }
    }
}