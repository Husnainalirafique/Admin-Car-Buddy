package com.husnain.admincarbuddy.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.husnain.admincarbuddy.R
import com.husnain.admincarbuddy.databinding.ActivityMapBinding
import com.husnain.admincarbuddy.utils.LatLngUtil
import com.husnain.admincarbuddy.utils.toast

class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Task<Location>
    private lateinit var latLng: LatLng
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inIt()
    }

    private fun inIt() {
        inItFusedLocation()
        obtainMapFragment()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnAddLocation.setOnClickListener {
            returnTheLatLng()
        }
    }

    private fun obtainMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun inItFusedLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lastLocation = fusedLocationClient.lastLocation
    }

    private fun returnTheLatLng(){
        if (::latLng.isInitialized) {
            val location = LatLngUtil.latLngToString(latLng)
            val resultIntent = Intent()
            resultIntent.putExtra("LOCATION", location)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            toast("Select a location first")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener {
            addMarker(it)
        }

        lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Location").draggable(true))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12f))
            }
        }

        if (permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED}){
            mMap.isMyLocationEnabled = true
        }
    }

    private fun addMarker(latLng: LatLng) {
        mMap.apply {
            clear()
            addMarker(MarkerOptions().position(latLng).title("Selected Location").draggable(true))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
        this.latLng = latLng
    }

}