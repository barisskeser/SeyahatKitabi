package com.example.seyahatkitabikotlin.Presenter

import com.example.seyahatkitabikotlin.Contract.MainActivityContract
import com.example.seyahatkitabikotlin.Model.Place
import com.example.seyahatkitabikotlin.Singleton.PlaceSingeton

class MainActivityPresenter : MainActivityContract.Presenter {

    private var view : MainActivityContract.View? = null


    override fun setView(view: MainActivityContract.View) {
        this.view = view
    }

    override fun created() {
        this.view?.initDatabase()
        this.view?.initClickListener()
    }

    override fun onClickPlace(place: Place) {
        PlaceSingeton.selectedPlace = place
        this.view?.goMap()
    }

    override fun onClickAddLocation() {
        PlaceSingeton.selectedPlace = null
        this.view?.goMap()
    }

}