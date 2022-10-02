package com.yunushamod.android.placebook.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.yunushamod.android.placebook.R
import com.yunushamod.android.placebook.adapter.BookmarkListAdapter
import com.yunushamod.android.placebook.adapter.BookmarkWindowInfoAdapter
import com.yunushamod.android.placebook.databinding.ActivityMapsBinding
import com.yunushamod.android.placebook.viewmodels.MapViewModel
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var bookmarkListAdapter: BookmarkListAdapter
    private val mapsViewModel: MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocationClient()
        setupPlacesClient()
        setupToolbar()
        setupNavigationDrawer()
    }

    private fun setupToolbar(){
        setSupportActionBar(binding.mainViewMap.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout,
            binding.mainViewMap.toolbar, R.string.open_drawer, R.string.close_drawer)
        toggle.syncState()
    }

    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
    }

    private fun setupLocationClient(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupPlacesClient(){
        placesClient = Places.createClient(this)
    }

    private fun setupMapListeners(){
        mMap.setInfoWindowAdapter(BookmarkWindowInfoAdapter(this))
        mMap.setOnPoiClickListener{
            displayPoi(it)
        }
        mMap.setOnInfoWindowClickListener{
            handleInfoWindowClick(it)
        }
    }

    private fun displayPoi(pointOfInterest: PointOfInterest){
        displayPoiGetStep(pointOfInterest)

    }

    private fun displayPoiGetStep(pointOfInterest: PointOfInterest){
        val placeId = pointOfInterest.placeId
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
        )
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                response?.let {
                    val place = it.place
                    displayPoiGetPhotoStep(place)
                }
            }.addOnFailureListener{
                if(it is ApiException){
                    val statusCode = it.statusCode
                    Log.e(TAG, "Places not found:${it.message}, statusCode: $statusCode")
                }
            }
    }

    private fun displayPoiGetPhotoStep(place: Place){
        val photoMetadata = place.photoMetadatas?.get(0)
        if(photoMetadata == null){
            displayPoiDisplayStep(place, null)
            return
        }
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
            .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
            .build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { response ->
            response?.let {
                val bitmap = it.bitmap
                displayPoiDisplayStep(place, bitmap)
            }
        }.addOnFailureListener{

            if(it is ApiException){
                val statusCode = it.statusCode
                Log.e(TAG, "Place not found: ${it.message}. statusCode: $statusCode")
            }
        }
    }

    private fun setupNavigationDrawer(){
        val layoutManager = LinearLayoutManager(this)
        binding.drawerViewMaps.recyclerView.layoutManager = layoutManager
        bookmarkListAdapter = BookmarkListAdapter(null, this)
        binding.drawerViewMaps.recyclerView.adapter = bookmarkListAdapter
    }

    private fun displayPoiDisplayStep(place: Place, photo: Bitmap?){
        place.latLng?.let {
            val marker = mMap.addMarker(MarkerOptions().position(it)
                .title(place.name)
                //.icon(iconPhoto)
                .snippet(place.phoneNumber))
            val placeInfo = PlaceInfo(place, photo)
            marker?.tag = placeInfo
            marker?.showInfoWindow()
        }
    }

    private fun handleInfoWindowClick(marker: Marker){
        when (marker.tag){
            is PlaceInfo  -> {
                val placeInfo = (marker.tag) as PlaceInfo
                if(placeInfo.place != null && placeInfo.image !=  null){
                    mapsViewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
                }
                marker.remove()
            }
            is MapViewModel.BookmarkView ->{
                val bookmarkView = (marker.tag as MapViewModel.BookmarkView)
                marker.hideInfoWindow()
                bookmarkView.id?.let{
                    startBookmarkDetail(it)
                }
            }
        }
    }

    private fun addPlaceMarker(bookmark:MapViewModel.BookmarkView): Marker?{
        val marker = mMap.addMarker(MarkerOptions().position(bookmark.location)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .title(bookmark.name)
            .snippet(bookmark.phone)
            .alpha(8.0f))
        marker?.tag = bookmark
        return marker
    }
    private fun displayAllBookmarks(bookmarks: List<MapViewModel.BookmarkView>){
        bookmarks.forEach { addPlaceMarker(it) }
    }

    private fun createBookmarkMarkerObserver(){
        mapsViewModel.getBookmarkMarkerView()?.observe(this){
            mMap.clear()
            it?.let{
                displayAllBookmarks(it)
                bookmarkListAdapter.setBookmarkData(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION){
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                Log.e(TAG, "Location permission denied")
            }
        }
    }

    private fun getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocationPermission()
        }else{
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnCompleteListener{
                val location = it.result
                if(location != null){
                    val latLng = LatLng(location.latitude, location.longitude)
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
                    mMap.moveCamera(update)
                }else {
                    Log.e(TAG, "Location not found")
                }
            }
        }
    }
    private fun startBookmarkDetail(bookmarkId: UUID){
        val intent = BookmarkDetailActivity.newInstance(this, bookmarkId)
        startActivity(intent)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapListeners()
        createBookmarkMarkerObserver()
        getCurrentLocation()
    }

    class PlaceInfo(val place: Place? = null, val image: Bitmap? = null)

    companion object{
        private const val TAG = "MAINACTIVITY"
        private const val REQUEST_LOCATION = 1
    }
}