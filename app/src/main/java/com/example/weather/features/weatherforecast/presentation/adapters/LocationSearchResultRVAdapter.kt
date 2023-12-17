package com.example.weather.features.weatherforecast.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ListItemSearchResultsBinding
import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem

class LocationSearchResultRVAdapter(private val itemClickListener: OnItemClickListener) :
    ListAdapter<AutocompleteItem, LocationSearchResultRVAdapter.LocationSearchResultViewHolder>
        (DiffUtilLocationSearchResults()){

    class LocationSearchResultViewHolder(val binding: ListItemSearchResultsBinding)
        : RecyclerView.ViewHolder(binding.root){
    }

    private class DiffUtilLocationSearchResults: DiffUtil.ItemCallback<AutocompleteItem>() {
        override fun areItemsTheSame(
            oldItem: AutocompleteItem,
            newItem: AutocompleteItem,
        ): Boolean =  oldItem == newItem

        override fun areContentsTheSame(
            oldItem: AutocompleteItem,
            newItem: AutocompleteItem,
        ): Boolean = oldItem.toString() == newItem.toString()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LocationSearchResultViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding = ListItemSearchResultsBinding.inflate(inflater)
        return LocationSearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationSearchResultViewHolder, position: Int) {
        val searchResult = getItem(position)

        holder.binding.locationNameTV.text = searchResult.name
        holder.binding.regionNameTV.text = searchResult.region

        holder.binding.searchResultLayout.setOnClickListener{
            itemClickListener.onItemClick(searchResult)
        }
    }

}

interface OnItemClickListener {
    fun onItemClick(selectedItem: AutocompleteItem)
}