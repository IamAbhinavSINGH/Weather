package com.example.weather.features.weatherforecast.presentation.ui.analysis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.ui.linechart.LineChart
import com.example.weather.R
import com.example.weather.features.weatherforecast.domain.weather.WeatherData
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.Dark_Blue
import com.example.weather.features.weatherforecast.presentation.ui.analysis.ui.theme.WeatherTheme
import com.example.weather.features.weatherforecast.presentation.ui.search.SearchActivity
import com.example.weather.features.weatherforecast.presentation.ui.utils.SearchHistoryManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity : ComponentActivity() {

     val viewModel by viewModels<AnalysisViewModel>()
    private val weatherDataList = mutableListOf<Map<Int, Int>>()

    private val map = mutableStateOf<Map<Int, Int>>(emptyMap())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchHistoryManager = SearchHistoryManager.getInstance(applicationContext)
        val favLocation = searchHistoryManager.getFavLocation()

        setContent {
               Scaffold(
                   topBar = {
                       topAppBar(onBackPressed = {onBackPressedDispatcher.onBackPressed()},
                           onSearchBtnPressed ={moveToSearchActivity()})
                   },
                   modifier = Modifier
                       .fillMaxSize()
                       .background(Dark_Blue)
               ) {values->
                   Column(
                       modifier = Modifier
                           .fillMaxSize()
                           .background(Dark_Blue)
                   ){
                       Spacer(modifier = Modifier.height(20.dp))
                       Box(
                           modifier = Modifier
                               .fillMaxWidth()
                               .height(300.dp)
                               .background(Dark_Blue)
                       ){
                           if(map.value.isNotEmpty()){
                               Log.e("GhaziabadMap" ,  "Chart called should show")
                               ShowLineChart(city1WeatherData = map.value)
                           }
                       }
                       Box(
                           modifier = Modifier
                               .fillMaxWidth()
                               .padding(horizontal = 20.dp)

                       ){
                           Column {
                               Text(
                                   text = "Graph for $favLocation",
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(horizontal = 20.dp),
                                   fontSize = 25.sp,
                                   fontWeight = FontWeight.Bold,
                                   color = Color.White
                               )
                               Spacer(modifier = Modifier.height(20.dp))
                               Text(
                                   text = "The Graph is shown for your favorite Location which is $favLocation.\n" +
                                           "If you want to change the location of the graph then change your Favorite Location",
                                   color = Color.White,
                                   fontSize = 13.sp,
                                   fontWeight = FontWeight.Light
                               )
                           }
                       }

                   }

               }
        }

        // ONCREATE CODE:-
        getWeatherInfo(favLocation)
    }

    private fun moveToSearchActivity(){
        Intent(this, SearchActivity::class.java).also{intent->
            startActivity(intent)
        }
    }

    private fun getWeatherInfo(location: String){
        viewModel.getForecastWeather(location)

        Log.e("GhaziabadMap" , "Api called successfully waiting for result")

        val mapOfTemp = mutableMapOf<Int , Int>()

        viewModel.weatherDetailsState.observe(this){weatherState->
            var i = 0
            weatherState.weatherInfo?.weatherDataPerDay?.forEach { Day, listOfHourlyTemp ->
               listOfHourlyTemp.forEach{weatherData: WeatherData ->
                   i++
                   mapOfTemp[i] = weatherData.temperatureCelsius.toInt()
               }
            }
            map.value = mapOfTemp
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBar(modifier: Modifier = Modifier, onBackPressed : () -> Unit, onSearchBtnPressed: () -> Unit,){

    TopAppBar(title = { Text("Analysis" , color = Color.White)},
        navigationIcon = {
            IconButton(onClick = {onBackPressed()}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = Color.White,
                    contentDescription = "NavigationBack",
                    )
            } },
        actions = {
            IconButton(onClick = { onSearchBtnPressed() }) {
                Icon(
                    painter = painterResource(id = R.drawable.search_svg),
                    tint = Color.White,
                    contentDescription = "Add City Names")
            }
                  },
        colors = TopAppBarDefaults.topAppBarColors(Dark_Blue)
    )
}