package com.example.seyahatkitabikotlin.RoomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.seyahatkitabikotlin.Model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {

    @Query("SELECT * FROM place")
    fun getAll(): Flowable<List<Place>>

    @Query("SELECT * FROM place where id = :id")
    fun findById(id: Int): Flowable<Place>

    @Insert
    fun insert(place: Place): Completable

    @Delete
    fun delete(place: Place?): Completable

}