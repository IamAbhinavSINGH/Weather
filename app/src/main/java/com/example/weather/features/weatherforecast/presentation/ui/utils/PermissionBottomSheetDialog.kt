package com.example.weather.features.weatherforecast.presentation.ui.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.weather.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class PermissionBottomSheetDialog(
    val permissionTextProvider: PermissionTextProvider,
    val isPermanentlyDeclined: Boolean,
    val onOkClick: () -> Unit,
    val onGoToAppSettings: () -> Unit
): BottomSheetDialogFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.permission_bottom_sheet_dialog, container, false)

        val textPermission = view.findViewById<TextView>(R.id.textPermissionBottomSheet)
        val grantBtn= view.findViewById<MaterialButton>(R.id.grantBtnBottomSheet)

        textPermission.text = permissionTextProvider.getDescription(isPermanentlyDeclined)

        if(isPermanentlyDeclined){
            grantBtn.text = "Grant Permission"
            grantBtn.setOnClickListener {
                dismiss()
                onGoToAppSettings()
            }
        }else{
            grantBtn.text = "Ok"
            grantBtn.setOnClickListener {
                dismiss()
                onOkClick()
            }
        }



        return view
    }

    companion object{
        const val Tag = "PERMISSIONBOTTOMSHEET"
    }


}


interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "It seems like you permanently declined location permission " +
                    "You can go to app settings to grant it."
        }else{
            "This app needs access to your location so that it can " +
                    "show weather forecast."
        }
    }

}

