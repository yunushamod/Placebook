package com.yunushamod.android.placebook.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.yunushamod.android.placebook.R
import com.yunushamod.android.placebook.databinding.ActivityBookmarkDetailBinding
import com.yunushamod.android.placebook.viewmodels.BookmarkDetailViewModel
import java.util.*

class BookmarkDetailActivity : AppCompatActivity() {
    private lateinit var databinding: ActivityBookmarkDetailBinding
    private val bookmarkDetailViewModel: BookmarkDetailViewModel by viewModels()
    private var bookmarkDetailsView:  BookmarkDetailViewModel.BookmarkView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = ActivityBookmarkDetailBinding.inflate(layoutInflater)
        setContentView(databinding.root)
        setupToolbar()
        getIntentData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bookmark_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_save -> {
                saveChanges()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveChanges(){
        val name = databinding.editTextName.text.toString()
        if(name.isEmpty()){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Name field cannot be null")
            builder.setPositiveButton("OK"){
                button, _ -> button.dismiss()
            }
            builder.create().show()
            return
        }
        bookmarkDetailsView?.let{
            it.name = databinding.editTextName.text.toString()
            it.notes = databinding.editTextNotes.text.toString()
            it.address = databinding.editTextAddress.text.toString()
            it.phone = databinding.editTextPhone.text.toString()
            bookmarkDetailViewModel.updateBookmark(it)
            finish()
        }
    }

    private fun setupToolbar(){
        setSupportActionBar(databinding.toolbar)
    }

    private fun populateImageView(){
        bookmarkDetailsView?.let{ bookmarkDetailView ->
            val placeImage = bookmarkDetailView.getImage(this)
            placeImage?.let {
                databinding.imageViewPlace.setImageBitmap(it)
            }
        }
    }

    private fun getIntentData(){
        val bookmarkId = intent.getSerializableExtra(EXTRA_BOOKMARK_ID) as UUID
        bookmarkDetailViewModel.getBookmark(bookmarkId)?.observe(this){
            it?.let{
                bookmarkDetailsView = it
                databinding.bookmarkDetailsView = it
                populateImageView()
            }
        }
    }

    companion object{
        private const val EXTRA_BOOKMARK_ID = "com.yunushamod.android.placebook.EXTRA_BOOKMARK_ID"
        fun newInstance(context: Context, bookmarkId: UUID?): Intent {
            return Intent(context, BookmarkDetailActivity::class.java).apply {
                putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
            }
        }
    }
}