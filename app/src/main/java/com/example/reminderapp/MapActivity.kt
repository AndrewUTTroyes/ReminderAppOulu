package com.example.reminderapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.toast
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var gMap:GoogleMap
    lateinit var fusedLocationClient:FusedLocationProviderClient
    var selectedLocation:LatLng? = null
    var selectedLocationAdress = ""

    lateinit var geofencingClient: GeofencingClient
    val CAMERA_ZOOM_LEVEL = 14f
    val GEOFENCE_ID ="REMINDER_GEOFENCE_ID"
    val GEOFENCE_RADIUS = 500
    val GEOFENCE_EXPIRATION = 120*24*60*60*1000
    val GEOFENCE_DWELL_DELAY = 2*60*1000

    var autoCompleteList = arrayListOf<String>("")
    lateinit var autoCompletAdaptor: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        (map_fragment as SupportMapFragment).getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)

        searchMap.setOnClickListener {
            val geocoder = Geocoder(applicationContext,Locale.getDefault())
            try {
                val searchText = searchAutoComplete.text.toString()
                val adresses = geocoder.getFromLocationName(searchText,1)
                val adress = adresses.get(0).getAddressLine(0)

                val lat = adresses.get(0).latitude
                val long = adresses.get(0).longitude

                selectedLocation = LatLng(lat,long)
                selectedLocationAdress = String.format("%s (%s)",searchText, adress)

                with(gMap){
                    clear()
                    animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            selectedLocation, CAMERA_ZOOM_LEVEL
                        )
                    )
                    val marker = addMarker(
                        MarkerOptions().position(
                            LatLng(lat,long)
                        ).snippet(adress).title(searchText)
                    )
                    marker.showInfoWindow()
                }
            }
            catch (e: Exception){

            }
        }
        mapButton.setOnClickListener{
            val reminderText = editText.text.toString()
            if(reminderText.isEmpty())
            {
                toast("Please provide reminder text")
                return@setOnClickListener
            }
            if(selectedLocation==null) {
                toast("Please select a location")
                return@setOnClickListener
            }

            val reminder = ReminderEntity(
                null,null,String.format("%.3f,%.3f", selectedLocation!!.latitude,
                    selectedLocation!!.longitude),reminderText
            )
            val reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
            reminderViewModel.insert(reminder)
            reminder.uid = reminderViewModel.lastUid
            CreateGeoFence(selectedLocation!!,reminder,geofencingClient)
            finish()
        }
    }
    private fun CreateGeoFence(selectedLocation:LatLng,reminder: ReminderEntity,geofencingClient: GeofencingClient){
        val geofence = Geofence.Builder().setRequestId(GEOFENCE_ID)
            .setCircularRegion(selectedLocation.latitude,
                selectedLocation.longitude,
                GEOFENCE_RADIUS.toFloat()).setExpirationDuration(GEOFENCE_EXPIRATION.toLong()).setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY).build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .addGeofence(geofence).build()
        val intent = Intent(this,GeofenceReceiver::class.java)
            .putExtra("uid",reminder.uid)
            .putExtra("mesage",reminder.message).putExtra("location",reminder.location)
        val pendingIntent = PendingIntent
            .getBroadcast(applicationContext, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==123){
            if(grantResults.isNotEmpty() && (grantResults[0]==PackageManager.PERMISSION_DENIED ||
                        grantResults[1]==PackageManager.PERMISSION_DENIED)){
                toast("The reminder needs all permissions to function")
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                if(grantResults.isNotEmpty() && (grantResults[2]==PackageManager.PERMISSION_DENIED)){
                    toast("The reminder needs all permissions to function")
                }
            }
        }
    }
    override fun onMapReady(map: GoogleMap?) {
        gMap=map?:return
        if((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            gMap.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location ->
                if (location != null) {
                    var latLong = LatLng(location.latitude, location.longitude)
                    with(gMap) {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f))
                    }
                }
            }
        }
        else {
            var permission = mutableListOf<String>()
            permission.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permission.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 123)
        }

        gMap.setOnMapClickListener { location: LatLng ->
            with(gMap) {
                clear()
                animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                var title = ""
                var city = ""
                try {
                    val adressList =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = adressList.get(0).getAddressLine(0)
                    city = adressList.get(0).locality.toUpperCase(Locale.getDefault())
                    title = address.toString()
                } catch (e: Exception) {

                }
                val marker =
                    addMarker(MarkerOptions().position(location).snippet(title).title((city)))
                marker.showInfoWindow()
                addCircle(
                    CircleOptions().center(location)
                        .strokeColor(Color.argb(50, 70, 70, 70))
                        .fillColor(Color.argb(100, 150, 150, 150))
                )
                selectedLocation = location
                selectedLocationAdress = String.format("%s (%s)",city,title)
            }
        }
    }
}
