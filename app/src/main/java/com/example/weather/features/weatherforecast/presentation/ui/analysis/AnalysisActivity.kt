package com.example.weather.features.weatherforecast.presentation.ui.analysis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weather.R
import com.example.weather.databinding.ActivityAnalysisBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}