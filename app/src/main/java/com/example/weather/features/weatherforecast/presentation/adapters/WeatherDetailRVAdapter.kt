package com.example.weather.features.weatherforecast.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.ListItemWeatherDetailsBinding
import com.example.weather.features.weatherforecast.presentation.states.WeatherDetailFORRV

class WeatherDetailRVAdapter():
ListAdapter<WeatherDetailFORRV, WeatherDetailRVAdapter.WeatherDetailViewHolder>(WeatherClassDiffCallBack()){

    class WeatherDetailViewHolder(val binding: ListItemWeatherDetailsBinding): RecyclerView.ViewHolder(binding.root){}

    private class WeatherClassDiffCallBack(): DiffUtil.ItemCallback<WeatherDetailFORRV>(){
        override fun areItemsTheSame(oldItem: WeatherDetailFORRV, newItem: WeatherDetailFORRV): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherDetailFORRV, newItem: WeatherDetailFORRV): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDetailViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding = ListItemWeatherDetailsBinding.inflate(inflater)

        return WeatherDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherDetailViewHolder, position: Int) {
        val weatherDetailItem = getItem(position)

        holder.binding.weatherDetailNameTV.text = weatherDetailItem.weatherDetailName
        if(weatherDetailItem.weatherDetailName == "AQI"){
            if(weatherDetailItem.weatherDetail > "0" && weatherDetailItem.weatherDetail < "50")
                holder.binding.weatherDetailTV.text = "Good"
            else if(weatherDetailItem.weatherDetail > "50" && weatherDetailItem.weatherDetail < "100")
                    holder.binding.weatherDetailTV.text = "Satisfactory"
            else if(weatherDetailItem.weatherDetail > "100" && weatherDetailItem.weatherDetail < "150")
                holder.binding.weatherDetailTV.text = "Moderate"
            else holder.binding.weatherDetailTV.text = "Poor"
        }
        else
            holder.binding.weatherDetailTV.text = weatherDetailItem.weatherDetail


        when(weatherDetailItem.weatherDetailName){
            "AQI" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.aqi)
            "UV Index" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.uv)
            "Sunrise" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.sunrise)
            "Sunset" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.sunset)
            "Moonrise" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.moonrise)
            "Moonset" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.moonset)
            "Wind Speed" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.wind)
            "Humidity" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.humidity)
            "Visibility" -> holder.binding.weatherDetailIV.setBackgroundResource(R.drawable.visibility)
        }
    }
}