package com.example.weather.features.weatherforecast.presentation.ui.settings


import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsBinding
    private var timePickedByUser: String = ""
    private lateinit var settingsSharedPreferences: SharedPreferences

    private var _tempUnit = "Celsius"
    private var _speedUnit = "Km/H"

    companion object{
        var tempUnit = ""
        var speedUnit = ""
        var timePicked = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBarSettings)


        settingsSharedPreferences = getSharedPreferences("SETTINGSPREFRENCES" , Context.MODE_PRIVATE)?: return

        binding.backBtnSettings.setOnClickListener{
            onBackPressed()
        }

        setSpinner(R.array.tempUnit, binding.tempUnitSpinner)
        setSpinner(R.array.speedUnit, binding.speedUnitSpinner)
        itemChangeListenerForSpinners()

        setPickTimeBtn()
        getValuesFromSharedPreference()
    }

    private fun setPickTimeBtn() {
        binding.pickTimeButton.setOnClickListener {
           setNotificationTimePickedByUser()
        }
        
        timePicked = settingsSharedPreferences.getString("TIME" , "PICK TIME").toString()
        if(timePicked != "PICK TIME") binding.pickTimeButton.text = timePicked
        else binding.pickTimeButton.text = "PICK TIME"
    }

    private fun getValuesFromSharedPreference() {
        tempUnit = settingsSharedPreferences.getString("TempUnit" , "Celsius").toString()
        speedUnit = settingsSharedPreferences.getString("SpeedUnit" , "Km/H").toString()

        if(tempUnit == "Fahrenheit"){
            binding.tempUnitSpinner.setSelection(1)
            Log.e("ItemChangeListener", "TempUnit Preferences changed")
        }
        if(speedUnit == "Mp/H") {
            binding.speedUnitSpinner.setSelection(1 )
            Log.e("ItemChangeListener", "SpeedUnit Preferences changed")
        }
    }

    private fun itemChangeListenerForSpinners(){
        binding.tempUnitSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (parent != null) {
                    _tempUnit = parent.getItemAtPosition(position).toString()
                    with(settingsSharedPreferences.edit()){
                        putString("TempUnit" , _tempUnit)
                        apply()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.speedUnitSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (parent != null) {
                    _speedUnit = parent.getItemAtPosition(position).toString()
                    with(settingsSharedPreferences.edit()){
                        putString("SpeedUnit", _speedUnit)
                        apply()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }

    private fun setNotificationTimePickedByUser(){
        val calender = Calendar.getInstance()

        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val min = calender.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                timePickedByUser = "$hourOfDay:$minute"
                with(settingsSharedPreferences.edit()){
                    putString("TIME", timePickedByUser)
                    apply()
                }
                binding.pickTimeButton.text = timePickedByUser
            },
            hour,
            min,
            false
        )
        timePickerDialog.show()
    }

    private fun setSpinner(stringArray: Int, spinner: Spinner){

        ArrayAdapter.createFromResource(
            this,
            stringArray,
            android.R.layout.simple_spinner_item
        ).also{adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

    }

}

