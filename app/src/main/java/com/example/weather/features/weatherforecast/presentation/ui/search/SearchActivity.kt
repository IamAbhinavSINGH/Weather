package com.example.weather.features.weatherforecast.presentation.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.databinding.ActivitySearchBinding
import com.example.weather.features.weatherforecast.presentation.adapters.SearchResultsRVAdatper
import com.example.weather.features.weatherforecast.presentation.ui.home.MainActivity
import com.example.weather.features.weatherforecast.presentation.weatherviewmodel.WeatherViewModel
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: WeatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var  searchResultAdapter : SearchResultsRVAdatper
    private lateinit var searchManager: SearchHistoryManager
    private var clickedOnSearchBolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchResultAdapter = SearchResultsRVAdatper(applicationContext)
        searchManager = SearchHistoryManager.getInstance(applicationContext)

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        binding.SearchResultsRV.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = searchResultAdapter
        }

         configureSearchView()
    }

    private fun configureSearchView(){

        binding.searchBar.requestFocus()

        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    clickedOnSearchBolean = true
                    findLocationBasedOnSearchQuery(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getSearchResults(newText)
                }
                return true
            }

        })
    }

    private fun findLocationBasedOnSearchQuery(locationName: String) {
        viewModel.getAutoCompleteResults(locationName)

        viewModel.state.observe(this){

            if(!it.searchResults.isNullOrEmpty() && clickedOnSearchBolean){
                clickedOnSearchBolean = false

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("SearchResult", locationName)

                addToSearchHistory(locationName)

                startActivity(intent)
            }
            if(it.error != null){
                Toast.makeText(this@SearchActivity, "Couldn't find location", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun getSearchResults(text: String){
        viewModel.getAutoCompleteResults(text)

        viewModel.state.observe(this){
            searchResultAdapter.submitList(it.searchResults)
        }
    }

    private fun addToSearchHistory(location:String){
        Log.e("ItemChangeListener" , "Method called history should be added!!")
        searchManager.addSearchHistory(location)
    }

}
