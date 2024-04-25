package com.example.finalproject

import LocationHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.view.Window
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    // Model
    lateinit var currentWeather: CurrentWeatherModel

    // Views
    private var simpleVideoView: NonClickableVideoView? = null
    private lateinit var temperature : TextView
    lateinit var locHelper : LocationHelper

    // State variables
    private var loadingData: Boolean = true

    // Location
    private val locationPermissionCode = 123
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationID : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this

        var window: Window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Retrieve persistent data (Celsius, Fahrenheit)
        loadPersistentCelciusPreference(this)

        // Retrieve location permission and location data
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) {
            // Permission granted! Get location data
            startLocationUpdates()
        } else {
            // Request location permission
            requestLocationPermission()
        }

        // Initialize temperature
        temperature = findViewById(R.id.temperature)
        temperature.text = "Loading..."
        locHelper = LocationHelper(this)
        locationID = findViewById(R.id.location)

        // Initialize switch GUI component & listeners
        var sw1: Switch = findViewById<Switch>(R.id.switch1)
        if(!DISPLAY_CELCIUS)
            sw1.isChecked = true
        sw1?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                DISPLAY_CELCIUS = false
                updateViews()
            } else {
                DISPLAY_CELCIUS = true
                updateViews()
            }
            savePersistentCelciusPreference(this)
        }
    }

    fun displayVideoView() {
        simpleVideoView = findViewById<View>(R.id.videoView) as NonClickableVideoView
        simpleVideoView!!.setMediaController(null)

        var videoToDisplay: Int = currentWeather.getVideoToDisplay()

        simpleVideoView!!.setVideoURI(
            Uri.parse(
                "android.resource://"
                        + packageName + "/" + videoToDisplay
            )
        )

        simpleVideoView!!.requestFocus()
        simpleVideoView!!.start()
        simpleVideoView!!.setOnPreparedListener { mp -> mp.isLooping = true }
        simpleVideoView!!.setOnErrorListener { _, _, _ ->
            Toast.makeText(
                applicationContext, "An error occurred while playing the background video !!!", Toast.LENGTH_LONG
            ).show()
            false
        }
    }

    fun updateViews() {
        temperature.text = currentWeather.getTemperatureString()
    }

    fun savePersistentCelciusPreference(context : Context) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putBoolean(CELSIUS_PREFERENCE, DISPLAY_CELCIUS)
        editor.commit()
    }

    fun loadPersistentCelciusPreference(context: Context) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        DISPLAY_CELCIUS = pref.getBoolean(CELSIUS_PREFERENCE, true)
    }


    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location tracking
                startLocationUpdates()
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                getLocationRequest(),
                locationCallback,
                null // Use the Looper from the main thread
            )
        }
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult?.lastLocation?.let { location ->
                // Handle the updated location
                locationChanged(location.latitude, location.longitude)
                locationID.text = locHelper.getLocationName(location.latitude, location.longitude)
                // ...
            }
        }
    }

    fun viewMorePressed(v: View) {
        // go to DataActivity
        if(loadingData)
            return
        var intent: Intent = Intent(this, CurrentWeatherActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun locationChanged(lat: Double, lon: Double) {
        if(MainActivity.lat == lat && MainActivity.lon == lon) {
            return
        }
        MainActivity.lat = lat
        MainActivity.lon = lon
        currentWeather = CurrentWeatherModel(lat, lon)
    }

    fun currentWeatherDataLoaded() {
        loadingData = false
        updateViews()
        displayVideoView()
    }

    override fun onResume() {
        super.onResume()
        if(!loadingData) {
            displayVideoView()
        }
    }

    fun goCalendar(v: View) {
        if(loadingData)
            return
        var intent : Intent = Intent(this, HistoricWeatherActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    companion object {
        var CELSIUS_PREFERENCE : String = "CELSIUS"
        var DISPLAY_CELCIUS: Boolean = true
        lateinit var instance : MainActivity
        var lat:Double = -1.0
        var lon:Double = -1.0
    }
}
