package com.example.seyahatkitabikotlin.RoomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.seyahatkitabikotlin.Model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDatabase: RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}