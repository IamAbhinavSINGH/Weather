package com.example.weather.features.weatherforecast.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weather.databinding.ListItemFutureForecastBinding
import com.example.weather.features.weatherforecast.domain.weather.WeatherData

class FutureForecastRVAdapter():
    ListAdapter<WeatherData, FutureForecastRVAdapter.FutureForecastViewHolder>(FutureForecastDiffCallBack()){

    class FutureForecastViewHolder(val binding: ListItemFutureForecastBinding): RecyclerView.ViewHolder(binding.root){}

    private class FutureForecastDiffCallBack: DiffUtil.ItemCallback<WeatherData>(){
        override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureForecastViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding: ListItemFutureForecastBinding = ListItemFutureForecastBinding.inflate(inflater)

        return FutureForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FutureForecastViewHolder, position: Int) {
        val weatherData = getItem(position)

        holder.binding.apply{

            val avgMaxMinTemperature = "${weatherData.maxTemperatureInCelsius}/${weatherData.minTemperatureInCelsius}"

            dayTV.text = convertDateFormat(weatherData.date)
            chanceOfRainTV.text = weatherData.chanceOfRain.toString().plus("%")
            avgMaxMinTempTV.text = avgMaxMinTemperature
            imageIV.load("https:${weatherData.dayImage}")
        }

    }

    fun convertDateFormat(inputDate: String): String {
        // Split the input string by "-"
        val parts = inputDate.split("-")

        // Check if the input string has three parts (year, month, and day)
        if (parts.size == 3) {
            val year = parts[0]
            val month = parts[1]
            val day = parts[2]

            // Format the date as "dd/MM"
            return "$day/$month"
        }
        // If the input string doesn't have three parts, return an error message or handle it as needed
        return "Couldn't convert Date"
    }
}