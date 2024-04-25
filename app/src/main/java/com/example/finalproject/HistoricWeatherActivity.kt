package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.util.Locale

class HistoricWeatherActivity : AppCompatActivity() {
    private lateinit var highestTempView: TextView
    private lateinit var lowestTempView: TextView
    private lateinit var averageTempView: TextView
    private lateinit var totalPrecipitationView: TextView
    private lateinit var windSpeedHighView: TextView
    private lateinit var windDirectionView: TextView
    lateinit var hWeather: HistoricWeatherModel
    private lateinit var adView : AdView
    var loadingData: Boolean = true
    var dateSelected: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_weather)
        var calendar: CalendarView = findViewById(R.id.calendarView)
        highestTempView = findViewById(R.id.highestTemp)
        lowestTempView = findViewById(R.id.lowestTemp)
        averageTempView = findViewById(R.id.averageTemp)
        totalPrecipitationView = findViewById(R.id.totalPrecipitation)
        windSpeedHighView = findViewById(R.id.windSpeedHigh)
        windDirectionView = findViewById(R.id.windDirection)

        calendar.maxDate = getMaxDate()

        loadPersistentDateSelected(this)
        calendar.date = dateStringToLong(dateSelected)

        loadHistoricData(dateSelected)
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle the selected date
            var dayFormatted: String = String.format("%02d", dayOfMonth)
            var monthFormatted: String = String.format("%02d", month+1)
            dateSelected = "$year-${monthFormatted}-$dayFormatted"
            loadHistoricData(dateSelected)
            savePersistentDateSelected(this)
        }
        createAd()
    }

    fun dateStringToLong(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        return date.time
    }

    fun savePersistentDateSelected(context : Context) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putString(HistoricWeatherActivity.DATE_PREFERENCE, dateSelected)
        editor.commit()
    }

    fun loadPersistentDateSelected(context: Context) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        dateSelected = pref.getString(HistoricWeatherActivity.DATE_PREFERENCE, "2023-12-03").toString()
    }

    fun loadHistoricData(date: String) {
        hWeather = HistoricWeatherModel(this, MainActivity.lat, MainActivity.lon, date)
        loadingData = true
        updateViews()
    }

    fun historicWeatherDataLoaded() {
        loadingData = false
        updateViews()
    }

    fun updateViews() {
        if(loadingData) {
            highestTempView.text = "Loading"
            lowestTempView.text = "Loading"
            averageTempView.text = "Loading"
            totalPrecipitationView.text = "Loading"
            windSpeedHighView.text = "Loading"
            windDirectionView.text = "Loading"
        } else {
            highestTempView.text = "${hWeather.getTempHighString()}"
            lowestTempView.text = "${hWeather.getTempLowString()}"
            averageTempView.text = "${hWeather.getTempAverageString()}"
            totalPrecipitationView.text = "${hWeather.getTotalPrecipitationString()}"
            windSpeedHighView.text = "${hWeather.getWindSpeedHighString()}"
            windDirectionView.text = "${hWeather.getWindDirectionString()}"
        }
    }

    fun goBack(v: View) {
        finish()
    }

    fun createAd() {
        adView = AdView(this)
        var adSize: AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)

        var adUnitId: String = "ca-app-pub-3940256099942544/6300978111"

        adView.adUnitId = adUnitId

        var builder: AdRequest.Builder = AdRequest.Builder()

        var request: AdRequest = builder.build()

        // add adView to linear layout
        var layout: RelativeLayout = findViewById(R.id.ad_view)
        layout.addView(adView)

        // load the ad
        adView.loadAd(request)
    }
    override fun onPause() {
        if( adView != null )
            adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if( adView != null )
            adView.resume()
    }

    override fun onDestroy() {
        if( adView != null )
            adView.destroy()
        super.onDestroy()
    }

    fun getMaxDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -6) // Subtract 6 days

        return calendar.timeInMillis
    }

    companion object {
        var DATE_PREFERENCE : String = "DATE"
    }
}