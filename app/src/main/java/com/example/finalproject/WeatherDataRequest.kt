package com.example.finalproject

import android.util.Log
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class WeatherDataRequest: Thread {
    var result: String = "NOT YET SET"
    var weather: Weather
    var latitude: Double
    var longitude: Double
    var date: String
    var historicData: Boolean

    constructor(weather: Weather, latitude: Double, longitude: Double, date: String, historicData: Boolean) {
        this.weather = weather
        this.latitude = latitude
        this.longitude = longitude
        this.date = date
        this.historicData = historicData
    }

    override fun run() {
        var url: URL = URL("https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current=temperature_2m,relative_humidity_2m,is_day,precipitation,cloud_cover,wind_speed_10m,wind_direction_10m")
        if(historicData) {
            url = URL("https://archive-api.open-meteo.com/v1/archive?latitude=${latitude}&longitude=${longitude}&start_date=${date}&end_date=${date}&daily=temperature_2m_max,temperature_2m_min,temperature_2m_mean,precipitation_sum,wind_speed_10m_max,wind_direction_10m_dominant")
        }
        var iStream: InputStream = url.openStream()
        var scan: Scanner = Scanner(iStream)

        result = ""
        while(scan.hasNext()) {
            result += scan.nextLine()
        }

        Log.w("MainActivity", "Read JSON 'as is:' $result")

        MainActivity.instance.runOnUiThread(UpdateGUI())
    }

    inner class UpdateGUI: Runnable {
        override fun run() {
            weather.processData(result)
        }
    }
}