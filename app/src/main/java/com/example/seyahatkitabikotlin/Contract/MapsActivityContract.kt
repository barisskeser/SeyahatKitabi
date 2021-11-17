package com.example.seyahatkitabikotlin.Contract

import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

interface MapsActivityContract {

    interface View {
        var locationManager: LocationManager
        fun initManager()
        fun initListener()
        fun initClickListener()
        fun initDatabase()
        fun showLocation(latLng: LatLng)
        fun registerLauncher()
        fun addMarkerSelectedPlace()
    }

    interface Presenter {
        fun setView(view: View)
        fun created()
        fun mapReady()
        fun goLocation()
    }

}