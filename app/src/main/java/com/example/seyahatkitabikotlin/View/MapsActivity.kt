package com.example.seyahatkitabikotlin.View

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.seyahatkitabikotlin.Contract.MapsActivityContract
import com.example.seyahatkitabikotlin.Model.Place
import com.example.seyahatkitabikotlin.Presenter.MapsActivityPresenter
import com.example.seyahatkitabikotlin.R
import com.example.seyahatkitabikotlin.RoomDB.PlaceDao
import com.example.seyahatkitabikotlin.RoomDB.PlaceDatabase
import com.example.seyahatkitabikotlin.Singleton.PlaceSingeton

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.seyahatkitabikotlin.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.disposables.ArrayCompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsActivityContract.View,
    GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    override lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var presenter: MapsActivityPresenter
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var marker: Marker? = null
    private lateinit var selectedPlaceMarker: Marker
    private lateinit var selectedLatLng: LatLng
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private lateinit var selectedMarker: Marker
    private lateinit var compositeDisposable: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Kullan at objesi
        compositeDisposable = CompositeDisposable()

        presenter = MapsActivityPresenter()
        presenter.setView(this)
        presenter.created()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)

        presenter.mapReady()
    }

    override fun initListener() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                println(location)
            }
        }
    }

    override fun initClickListener() {
        binding.saveButtonMap.setOnClickListener {
            save(it)
        }

        binding.deleteButtonMap.setOnClickListener {
            delete(it)
        }
    }

    private fun save(view: View){
        if (selectedLatLng != null) {
            val name = binding.nameEditTextMap.text
            val latitude = selectedLatLng.latitude
            val longitude = selectedLatLng.longitude

            val place = Place(name.toString(), latitude, longitude)

            // IO Thread Internet/Database

            compositeDisposable.add(
                placeDao.insert(place)
                    .subscribeOn(Schedulers.io()) // Ne kullanılacağı
                    .observeOn(AndroidSchedulers.mainThread()) // Nerde kullanılacağı
                    .subscribe(this::handleResponse) // Sonra olacak şey, bitince fonksiyonu çalıştır
            )

        } else {
            Toast.makeText(
                this,
                "Please, select a place first with long click.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleResponse(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun delete(view: View){
        compositeDisposable.add(
            placeDao.delete(PlaceSingeton.selectedPlace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    override fun initDatabase() {
        db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java, "Places"
        ).allowMainThreadQueries()
            .build()

        placeDao = db.placeDao()
    }

    override fun initManager() {
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                Snackbar.make(
                    binding.root,
                    "Permission needed for location",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission") {
                    // request permission
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()

            } else {
                // request permission
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            // Permission Granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0f,
                locationListener
            )

            presenter.goLocation()
        }
    }

    override fun registerLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result && ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission Granted
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0f,
                        locationListener
                    )

                    presenter.goLocation()

                } else {
                    // Denied
                    Toast.makeText(this@MapsActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun addMarkerSelectedPlace() {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    PlaceSingeton.selectedPlace!!.latitude,
                    PlaceSingeton.selectedPlace!!.longitude
                )
            )
        )

        binding.nameEditTextMap.setText(PlaceSingeton.selectedPlace?.name)
    }

    override fun showLocation(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        marker = mMap.addMarker(MarkerOptions().position(latLng))
        selectedLatLng = latLng
        if (PlaceSingeton.selectedPlace != null)
            selectedPlaceMarker = mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        PlaceSingeton.selectedPlace!!.latitude,
                        PlaceSingeton.selectedPlace!!.longitude
                    )
                )
            )
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        selectedMarker = p0
        if (p0.equals(marker)) {
            binding.nameEditTextMap.setText("")
            return true
        } else if (p0.equals(selectedPlaceMarker)) {
            binding.nameEditTextMap.setText(PlaceSingeton.selectedPlace?.name as Editable)
            return true
        }

        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        PlaceSingeton.selectedPlace = null
        compositeDisposable.clear()
    }
}