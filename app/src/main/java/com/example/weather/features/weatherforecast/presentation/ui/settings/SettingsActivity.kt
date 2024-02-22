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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import co.yml.charts.common.extensions.isNotNull
import com.example.weather.R
import com.example.weather.databinding.ActivitySettingsBinding
import com.example.weather.features.weatherforecast.presentation.ui.notification.workmanager.NotificationWorkManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsBinding
    private var timePickedByUser: String = ""
    private lateinit var settingsSharedPreferences: SharedPreferences
    private lateinit var workManager: WorkManager

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
        workManager = WorkManager.getInstance(applicationContext)

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
        if(timePicked != "PICK TIME") {
            binding.pickTimeButton.text = convertTimeTo12HrFormat(timePicked)
        }
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
                binding.pickTimeButton.text = convertTimeTo12HrFormat(timePickedByUser)
                instantiateWorkManagerWithTime(timePickedByUser)
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

    private fun instantiateWorkManagerWithTime(timePickedByUser: String?){
        if(!timePickedByUser.isNullOrEmpty()) {
            workManager.cancelAllWork()
            setUpWorkManagerToShowNotificationTimely(timePickedByUser)
        }
    }

    private fun setUpWorkManagerToShowNotificationTimely(timePickedByUser: String){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorkManager>()
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(timePickedByUser), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(workRequest)
    }


    // This function is used to find the "REMAINING TIME" from current time to the next time picked by user
    private fun calculateInitialDelay(selectedTime: String): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance()

        // Parse the selected time
        val timeParts = selectedTime.split(":")
        if (timeParts.size == 2) {
            targetTime.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            targetTime.set(Calendar.MINUTE, timeParts[1].toInt())
            targetTime.set(Calendar.SECOND, 0)
        }

        // Calculate the time difference
        var timeDifference = targetTime.timeInMillis - currentTime.timeInMillis

        // If the selected time has already passed for today, schedule for the next day
        if (timeDifference <= 0) {
            timeDifference += 24 * 60 * 60 * 1000 // 24 hours in milliseconds
        }

        return timeDifference
    }

    private fun convertTimeTo12HrFormat(selectedTime: String): String{
        val timeParts = selectedTime.split(":")
        return if(timeParts[0].toInt() > 12){
            "${timeParts[0].toInt() - 12} : ${timeParts[1]} ${isAmOrPM(selectedTime)}"
        }else "$selectedTime ${isAmOrPM(selectedTime)}"
    }
    private fun isAmOrPM(time: String): String{
        // suppose the time is 3:43 , we will split the string by ":" and then if the first half is less than 12
       //       than its currently "AM" and if its more than 12 than its currently "PM"

        val split = time.split(":")
        return if(split[0].toInt() <= 12){
            "AM"
        } else "PM"
    }

}

