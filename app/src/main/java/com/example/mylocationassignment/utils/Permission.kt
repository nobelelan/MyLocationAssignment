package com.example.mylocationassignment.utils

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.mylocationassignment.utils.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

object Permission {

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    fun requestLocationPermission(fragment: Fragment) =
        EasyPermissions.requestPermissions(
            fragment,
            "My Location application requires location permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
}