package com.example.finalproject

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class HistoricWeatherModel: Weather {
    lateinit var weatherDataJsonString: String

    // Data
    private var dataSet: Boolean = false
    private var tempHigh: Double = 0.0
    private var tempLow: Double = 0.0
    private var tempAverage: Double = 0.0
    private var totalPrecipitation: Double = 0.0
    private var totalPrecipitationUnits: String = ""
    private var windSpeedHigh = 0.0
    private var windSpeedHighUnits: String = ""
    private var windDirection = 0.0
    private var windDirectionUnits: String = ""
    private var h: HistoricWeatherActivity

    constructor(h:HistoricWeatherActivity, latitude: Double, longitude: Double, date: String) {
        this.h = h
        var request = WeatherDataRequest(this, latitude, longitude, date, true)
        request.start()
    }

    override fun processData(data: String) {
        weatherDataJsonString = data
        h.runOnUiThread(UpdateGUI())
    }

    inner class UpdateGUI: Runnable {
        override fun run() {
            try {
                var jsonObject: JSONObject = JSONObject(weatherDataJsonString)
                var daily = jsonObject.getJSONObject("daily")

                // Retrieve raw values
                var dataArray: JSONArray = daily.getJSONArray("temperature_2m_max")
                tempHigh = dataArray.getDouble(0)
                dataArray = daily.getJSONArray("temperature_2m_min")
                tempLow = dataArray.getDouble(0)
                dataArray = daily.getJSONArray("temperature_2m_mean")
                tempAverage = dataArray.getDouble(0)
                dataArray = daily.getJSONArray("precipitation_sum")
                totalPrecipitation = dataArray.getDouble(0)
                dataArray = daily.getJSONArray("wind_speed_10m_max")
                windSpeedHigh = dataArray.getDouble(0)
                dataArray = daily.getJSONArray("wind_direction_10m_dominant")
                windDirection = dataArray.getDouble(0)

                // Retrieve units
                var units: JSONObject = jsonObject.getJSONObject("daily_units")
                totalPrecipitationUnits = units.getString("precipitation_sum")
                windSpeedHighUnits = units.getString("wind_speed_10m_max")
                windDirectionUnits = units.getString("wind_direction_10m_dominant")

                dataSet = true

                h.historicWeatherDataLoaded()
            } catch (e: Error ) {
                Log.w("MainActivity", "Error: $e")
            }
        }
    }

    fun convertTempToPreference(temp: Double): String {
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

    fun getTempHighString(): String {
        return "🔥 Highest Temp: ${convertTempToPreference(tempHigh)}"
    }

    fun getTempLowString(): String {
        return "🧊 Lowest Temp: ${convertTempToPreference(tempLow)}"
    }

    fun getTempAverageString(): String {
        return "🌡 Average Temp: ${convertTempToPreference(tempAverage)}"
    }

    fun getTotalPrecipitationString(): String {
        return "🌧 Total Precipitation: ${totalPrecipitation} ${totalPrecipitationUnits}"
    }

    fun getWindSpeedHighString(): String {
        return "💨 Highest Wind Speed: ${windSpeedHigh} ${windSpeedHighUnits}"
    }

    fun getWindDirectionString(): String {
        return "🌪 Wind Direction: ${windDirection} ${windDirectionUnits}"
    }
}