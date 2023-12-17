package com.example.weather.features.weatherforecast.presentation.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SearchHistoryManager(context: Context) {

    private val searchPreferences: SharedPreferences = context.getSharedPreferences("searchPreferences", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object{
        private var instance: SearchHistoryManager? = null

        fun getInstance(context: Context): SearchHistoryManager{
            if(instance == null){
                instance = SearchHistoryManager(context)
            }
            return instance!!
        }
    }

    fun addSearchHistory(location: String){
        val currentHistory = getSearchHistory().toMutableList()

        with(searchPreferences.edit()){
            putString("LastSearchedLocation", location)
            apply()
        }

        val lastSearchedLocation = searchPreferences.getString("LastSearchedLocation" , "")

        if (!lastSearchedLocation.isNullOrEmpty()) {
            currentHistory.add(0, lastSearchedLocation)
        }

        val maxHistorySize = 8

        while(currentHistory.size > maxHistorySize){
            currentHistory.removeAt(currentHistory.size -1)
        }

        with(searchPreferences.edit()){
            putString("searchHistory", gson.toJson(currentHistory))
            apply()
            Log.e("ItemChangeListener", "search Preference wrote $currentHistory")
        }
    }

    fun getSearchHistory(): List<String>{
        val jsonString = searchPreferences.getString("searchHistory", "")

        return if (jsonString.isNullOrEmpty()){
            emptyList<String>()
        } else{
            gson.fromJson(jsonString, Array<String>::class.java).toList()
        }
    }
}