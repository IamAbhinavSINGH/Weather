package com.example.weather.features.weatherforecast.presentation.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weather.databinding.ListItemHourlyWeatherBinding
import com.example.weather.features.weatherforecast.domain.weather.WeatherData
import java.time.format.DateTimeFormatter
import java.util.Locale

class HourlyWeatherRVAdapter():
    ListAdapter<WeatherData, HourlyWeatherRVAdapter.HourlyWeatherViewHolder>(HourlyWeatherDiffCallBack()) {

        class HourlyWeatherViewHolder(val binding: ListItemHourlyWeatherBinding): RecyclerView.ViewHolder(binding.root){

        }

    private class HourlyWeatherDiffCallBack: DiffUtil.ItemCallback<WeatherData>(){
        override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
         val inflater = parent.context.getSystemService(LayoutInflater::class.java)
         val binding = ListItemHourlyWeatherBinding.inflate(inflater)

         return HourlyWeatherViewHolder(binding)
     }

     override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {

         val weatherData = getItem(position)

         holder.binding.apply {
             val amPm = if (weatherData.time.hour < 12) "am" else "pm"
             val formatter = DateTimeFormatter.ofPattern("hh:mm" , Locale.getDefault())
             val formattedTime = weatherData.time.format(formatter)
             val finalTime = "$formattedTime $amPm"

             hourlyTimeTV.text = finalTime

             imageIV.load("https:${weatherData.image}")

             hourlyTemperatureTV.text = weatherData.temperatureCelsius.toString()
             chanceOfRainTV.text = "${weatherData.chanceOfRain.toString()}%"
         }
     }
 }