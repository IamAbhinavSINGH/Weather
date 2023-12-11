package com.example.weather.features.weatherforecast.presentation.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ListItemSearchResultsBinding
import com.example.weather.features.weatherforecast.data.models.autocomplete.AutocompleteItem
import com.example.weather.features.weatherforecast.presentation.ui.home.MainActivity
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager

class SearchResultsRVAdatper(
    val context : Context
):
ListAdapter<AutocompleteItem, SearchResultsRVAdatper.SearchResultsViewHolder>(DiffUtilSearchResults()){

    class SearchResultsViewHolder(val binding: ListItemSearchResultsBinding): RecyclerView.ViewHolder(binding.root){
    }

    private class DiffUtilSearchResults: DiffUtil.ItemCallback<AutocompleteItem>(){
        override fun areItemsTheSame(oldItem: AutocompleteItem,newItem: AutocompleteItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AutocompleteItem,newItem: AutocompleteItem): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val inflater = parent.context.getSystemService(LayoutInflater::class.java)
        val binding = ListItemSearchResultsBinding.inflate(inflater)
        return SearchResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        val searchResult = getItem(position)

        holder.binding.locationNameTV.text = searchResult.name
        holder.binding.regionNameTV.text = searchResult.region

        val searchHistoryManger = SearchHistoryManager(context)

        holder.binding.searchResultLayout.setOnClickListener{

            searchResult.name?.let { location -> searchHistoryManger.addSearchHistory(location) }

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("SearchResult", searchResult.name)
            context.startActivity(intent)
        }

    }
}