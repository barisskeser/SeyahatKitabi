package com.example.seyahatkitabikotlin.Contract

import com.example.seyahatkitabikotlin.Model.Place

interface MainActivityContract {

    interface View {
        fun goMap()
        fun initClickListener()
        fun initDatabase()
    }

    interface Presenter {
        fun setView(view: View)
        fun created()
        fun onClickPlace(place: Place)
        fun onClickAddLocation()
    }

}