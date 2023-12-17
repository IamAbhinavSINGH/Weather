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

/*
    This Dialog is used in the FavLocationActivity, it is created whenever
    whenever the user touch a other location.
   -> The "YES" function signifies that the user wants to change his fav location
   -> The "NO" function signifies that the user does not want to change his fav location
 */
class DialogForChangeFavLocation (
    val yes : () -> Unit,
    val no : () -> Unit
): DialogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_change_fav_location,container, false)

        val yesBtn = view.findViewById<MaterialButton>(R.id.yesFavChangeLocationBtn)
        val noBtn = view.findViewById<MaterialButton>(R.id.noFavChangeLocationBtn)

        yesBtn.setOnClickListener {
            yes()
            dismiss()
        }

        noBtn.setOnClickListener {
            no()
            dismiss()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        return dialog
    }
}