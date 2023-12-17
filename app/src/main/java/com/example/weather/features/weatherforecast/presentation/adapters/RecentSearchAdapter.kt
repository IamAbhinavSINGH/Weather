package com.example.weather.features.weatherforecast.presentation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weather.databinding.ListItemRecentSearchesBinding
import com.example.weather.features.weatherforecast.presentation.ui.home.MainActivity
import com.example.weather.features.weatherforecast.presentation.ui.utils.RecentSearchHistoryDetails

class RecentSearchAdapter(val context: Context):
ListAdapter<RecentSearchHistoryDetails, RecentSearchAdapter.RecentSearchViewViewHolder>(RecentSearchesListCallBack()){

    class RecentSearchViewViewHolder(val binding: ListItemRecentSearchesBinding): RecyclerView.ViewHolder(binding.root){
    }

    private class RecentSearchesListCallBack: DiffUtil.ItemCallback<RecentSearchHistoryDetails>(){
        override fun areItemsTheSame(
            oldItem: RecentSearchHistoryDetails,
            newItem: RecentSearchHistoryDetails,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: RecentSearchHistoryDetails,
            newItem: RecentSearchHistoryDetails,
        ): Boolean = oldItem.toString() == newItem.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding : ListItemRecentSearchesBinding = ListItemRecentSearchesBinding.inflate(inflater)

        return RecentSearchViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewViewHolder, position: Int) {
        val recentSearchDetails = getItem(position)

        holder.binding.recentSearchesIV.load("https:${recentSearchDetails.tempImageURL}")
        holder.binding.recentSearchesLocationTV.text = recentSearchDetails.locationName
        holder.binding.recentSearchesTempTV.text = recentSearchDetails.temperature

        holder.binding.recentSearchesCV.setOnClickListener{
            Intent(context, MainActivity::class.java).also {
                it.putExtra("SearchResult", recentSearchDetails.locationName)
                context.startActivity(it)
            }
        }
    }
}