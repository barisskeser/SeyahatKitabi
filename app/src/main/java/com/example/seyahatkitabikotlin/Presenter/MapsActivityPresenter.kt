package com.example.seyahatkitabikotlin.Presenter

import android.annotation.SuppressLint
import android.location.LocationManager
import com.example.seyahatkitabikotlin.Contract.MapsActivityContract
import com.example.seyahatkitabikotlin.Singleton.PlaceSingeton
import com.google.android.gms.maps.model.LatLng

class MapsActivityPresenter : MapsActivityContract.Presenter {

    private lateinit var view: MapsActivityContract.View

    override fun setView(view: MapsActivityContract.View) {
        this.view = view
    }

    override fun created() {
        view.registerLauncher()
        view.initClickListener()
        view.initDatabase()
    }

    override fun mapReady() {
        view.initListener()
        view.initManager()
    }

    @SuppressLint("MissingPermission")
    override fun goLocation() {
        var lastLocation =
            this.view.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (PlaceSingeton.selectedPlace != null) {
            this.view.showLocation(
                LatLng(
                    PlaceSingeton.selectedPlace!!.latitude,
                    PlaceSingeton.selectedPlace!!.longitude
                )
            )
            this.view.addMarkerSelectedPlace()
        } else if (lastLocation != null) {
            this.view.showLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
        }
    }
}