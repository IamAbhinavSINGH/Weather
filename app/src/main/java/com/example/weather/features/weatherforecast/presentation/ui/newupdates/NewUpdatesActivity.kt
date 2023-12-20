package com.example.weather.features.weatherforecast.presentation.ui.newupdates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.weather.R
import com.example.weather.databinding.ActivityNewUpdatesBinding
import com.example.weather.features.weatherforecast.presentation.ui.utils.DialogCredits

class NewUpdatesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewUpdatesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnSettings.setOnClickListener {
            Log.e("GhaziabadMap" , "it shld be working")
            onBackPressed()
        }

        binding.credits.setOnClickListener {
                DialogCredits().show(supportFragmentManager, "Dialog_Credits")
        }
    }
}