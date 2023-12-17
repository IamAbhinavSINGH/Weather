package com.example.weather.features.weatherforecast.presentation.ui.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.weather.R
import com.google.android.material.button.MaterialButton

class FavLocationInformationDialog(): DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_information_about_fav_location,
            container, false)

        val okBtn = view.findViewById<MaterialButton>(R.id.okFavLocationDialogBtn)

        okBtn.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        return dialog
    }
}