package com.example.finalproject

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CurrentWeatherActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_weather_more)

        var humidity : TextView = findViewById(R.id.humidity)
        var pprobability : TextView = findViewById(R.id.pprobability)
        var windspeed : TextView = findViewById(R.id.windspeed)
        var winddirection : TextView = findViewById(R.id.winddirection)
        var cloudcovertotal : TextView = findViewById(R.id.cloudcovertotal)
        var currentWeather = MainActivity.instance.currentWeather
        humidity.text = currentWeather.getHumidityString()
        pprobability.text = currentWeather.getPrecipitationString()
        cloudcovertotal.text = currentWeather.getCloudCoverString()
        windspeed.text = currentWeather.getWindSpeedString()
        winddirection.text = currentWeather.getWindDirectionString()


    }
    fun goBack(v: View) {
        finish()
    }
}