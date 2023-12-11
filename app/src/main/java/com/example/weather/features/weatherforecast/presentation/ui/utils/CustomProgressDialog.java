package com.example.weather.features.weatherforecast.presentation.ui.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;


import com.example.weather.R;

public class CustomProgressDialog extends Dialog {


    public CustomProgressDialog( Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_dialog);
        setCancelable(false); // Prevent users from dismissing the dialog by tapping outside
    }


}
