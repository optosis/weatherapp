package com.example.finalproject

import android.util.Log
import org.json.JSONObject

// Data about the current weather. Shared between main activity and current weather activity
class CurrentWeatherModel: Weather {
    lateinit var weatherDataString: String

    // Data
    private var dataSet: Boolean = false
    private var temp: Double = 0.0
    private var humidity: Double = 0.0
    private var humidityUnits: String = ""
    private var isDay: Boolean = false
    private var precipitation: Double = 0.0
    private var precipitationUnits: String = ""
    private var cloudCover: Int = 0
    private var cloudCoverUnits: String = ""
    private var windSpeed: Double = 0.0
    private var windSpeedUnits: String = ""
    private var windDirection: Int = 0
    private var windDirectionUnits: String = ""

    constructor(latitude: Double, longitude: Double) {
        var request = WeatherDataRequest( this, latitude, longitude, "", false)
        request.start()
    }

    override fun processData(data: String) {
        weatherDataString = data
        MainActivity.instance.runOnUiThread(UpdateGUI())
    }

    inner class UpdateGUI: Runnable {
        override fun run() {
            try {
                var jsonObject: JSONObject = JSONObject(weatherDataString)
                var current = jsonObject.getJSONObject("current")
                var units = jsonObject.getJSONObject("current_units")

                // Retrieve raw values
                temp = current.getDouble("temperature_2m")
                humidity = current.getDouble("relative_humidity_2m")
                var isDayInt = current.getInt("is_day")
                if(isDayInt == 1){
                    isDay = true
                }
                precipitation = current.getDouble("precipitation")
                cloudCover = current.getInt("cloud_cover")
                windSpeed = current.getDouble("wind_speed_10m")
                windDirection = current.getInt("wind_direction_10m")

                // Retrieve units
                humidityUnits = units.getString("relative_humidity_2m")
                precipitationUnits = units.getString("precipitation")
                cloudCoverUnits = units.getString("cloud_cover")
                windSpeedUnits = units.getString("wind_speed_10m")
                windDirectionUnits = units.getString("wind_direction_10m")

                dataSet = true

                MainActivity.instance.currentWeatherDataLoaded()
            } catch (e: Error ) {
                Log.w("MainActivity", "Error: $e")
            }
        }
    }
    fun getVideoToDisplay(): Int {
        var videoToDisplay: Int = 0
        if(precipitation > 0.0) {
            videoToDisplay = R.raw.rainy
        } else if(isDay) {
            videoToDisplay = R.raw.sunny
        } else {
            videoToDisplay = R.raw.night
        }
        return videoToDisplay
    }

    fun getTemperatureString() : String{
        var weatherVal: Double = -1.0
        if (MainActivity.DISPLAY_CELCIUS) {
            weatherVal = temp
        } else {
            weatherVal = (temp * (9.0/5.0)) + 32
        }

        var stringUnit = "C"
        if(!MainActivity.DISPLAY_CELCIUS)
            stringUnit = "F"

        return weatherVal.toInt().toString() + " " + stringUnit
    }

    fun getHumidityString(): String {
        return "💦 Humidity: ${humidity} ${humidityUnits}"
    }

    fun getPrecipitationString(): String {
        return "🌧 Precipitation: ${precipitation} ${precipitationUnits}"
    }

    fun getCloudCoverString(): String {
        return "☁️ Cloud Cover: ${cloudCover} ${cloudCoverUnits}"
    }

    fun getWindSpeedString(): String {
        return "💨 Wind Speed: ${windSpeed} ${windSpeedUnits}"
    }

    fun getWindDirectionString(): String {
        return "🌪 Wind Direction: ${windDirection} ${windDirectionUnits}"
    }
}
