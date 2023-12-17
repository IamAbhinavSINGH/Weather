package com.example.weather.features.weatherforecast.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weather.databinding.ListItemFavSearchesBinding
import com.example.weather.features.weatherforecast.presentation.ui.utils.DialogForChangeFavLocation
import com.example.weather.features.weatherforecast.presentation.ui.utils.RecentSearchHistoryDetails

class RecentFAVSearchesRVAdapter(
    private val onOtherFavLocationClickListener: onOtherFavLocationClickListener,
    val fragmentManager: FragmentManager
):
ListAdapter<RecentSearchHistoryDetails , RecentFAVSearchesRVAdapter.RecentFAVSearchesViewHolder>
    (DiffUtilRecentFAVSearches()){

        private lateinit var  changeLocationDialog : DialogForChangeFavLocation

    class RecentFAVSearchesViewHolder(val binding : ListItemFavSearchesBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class DiffUtilRecentFAVSearches() : DiffUtil.ItemCallback<RecentSearchHistoryDetails>(){
        override fun areItemsTheSame(
            oldItem: RecentSearchHistoryDetails,
            newItem: RecentSearchHistoryDetails,
        ): Boolean  = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: RecentSearchHistoryDetails,
            newItem: RecentSearchHistoryDetails,
        ): Boolean = oldItem.toString() == newItem.toString()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentFAVSearchesViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding : ListItemFavSearchesBinding = ListItemFavSearchesBinding.inflate(inflater)
        return RecentFAVSearchesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentFAVSearchesViewHolder, position: Int) {
        val recentSearchHistory = getItem(position)

        holder.binding.otherSearchFavTV.text = recentSearchHistory.locationName
        holder.binding.otherSearchFavTempTV.text = recentSearchHistory.temperature
        holder.binding.otherSearchFavIV.load("https://${recentSearchHistory.tempImageURL}")

        holder.binding.otherSearchFavCV.setOnClickListener {
            inflateChangeLocationDialog(recentSearchHistory.locationName)
        }
    }

    private fun inflateChangeLocationDialog(locationName:String){
       changeLocationDialog = DialogForChangeFavLocation(
           yes = {changeLocation(locationName)},
           no = {changeLocationDialog.dismiss()}
       )
        changeLocationDialog.show(fragmentManager, "ChangeLocationDialog")
    }

    private fun changeLocation(locationName: String){
        onOtherFavLocationClickListener.onOtherLocationClick(locationName)
    }
}

interface onOtherFavLocationClickListener{
    fun onOtherLocationClick(locationName : String)
}
