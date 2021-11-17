package com.example.seyahatkitabikotlin.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.seyahatkitabikotlin.Adapter.RecyclerAdapter
import com.example.seyahatkitabikotlin.Contract.MainActivityContract
import com.example.seyahatkitabikotlin.Model.Place
import com.example.seyahatkitabikotlin.Presenter.MainActivityPresenter
import com.example.seyahatkitabikotlin.RoomDB.PlaceDao
import com.example.seyahatkitabikotlin.RoomDB.PlaceDatabase
import com.example.seyahatkitabikotlin.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity(), MainActivityContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter : RecyclerAdapter
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private val presenter : MainActivityPresenter = MainActivityPresenter()
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        compositeDisposable = CompositeDisposable()
        this.presenter.setView(this)
        this.presenter.created()
    }

    override fun goMap() {
        val intent = Intent(this, MapsActivity::class.java)
        this.startActivity(intent)
    }

    override fun initClickListener() {
        binding.goMapAddLocation.setOnClickListener{
            presenter.onClickAddLocation()
        }
    }

    override fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java, "Places"
        ).allowMainThreadQueries().build()
        placeDao = db.placeDao()

        compositeDisposable.add(
            placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(placeList: List<Place>){
        adapter = RecyclerAdapter(placeList, presenter)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}