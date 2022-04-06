package com.harshad.projectclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.harshad.projectclean.databinding.ActivityMainBinding
import java.lang.Exception

//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var sharedPref : SharedPreferences
    private var isRemembered = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnLogout.setVisibility(View.INVISIBLE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        sharedPref = getSharedPreferences("SP", Context.MODE_PRIVATE)
        binding.btnDisplayGri.setOnClickListener {
            val intent = Intent(this, DisplayGrievance::class.java)
            startActivity(intent)
            finish()
            //create_gri()
        }

//        binding.fabNew.setOnClickListener {
//            val intent = Intent(this, CreatePost::class.java)
//            startActivity(intent)
//            finish()
//        }

        binding.btnProfileSetup.setOnClickListener {
            val intent = Intent(this, ProfileSetup::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //isRemembered = sharedPref.getBoolean("REMEMBER", false)

//        if (!isRemembered){
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        else{
//
//            binding.btnLogin.setVisibility(View.INVISIBLE)
//        }
        binding.btnLogout.setVisibility(View.VISIBLE)
        binding.btnLogout.setOnClickListener{

            val editor :SharedPreferences.Editor =sharedPref.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.tvDemoText.text = sharedPref.getString("auth_token","No auth token").toString()

    }



    private fun fetchLocation(){
        val mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10 * 1000)
            .setFastestInterval(1 * 1000)
        val task = fusedLocationProviderClient.lastLocation
        Log.d("fetchLocation","In task listener")
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),101)
            return
        }



        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                   Log.d("fetchLocation1","${location.latitude}" )
                   Log.d("fetchLocation2","${location.longitude}" )
                    binding.tvDemoText.setText("${location.latitude}")
                }

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_LONG).show()
                binding.tvDemoText.setText("Fail")
            }
        Log.d("fetchLocation","In IF task listener")
        task.addOnSuccessListener {location->
            Log.d("fetchLocation","${location}")
            if (location!=null){

                Toast.makeText(applicationContext,"${location.latitude} ${location.longitude}", Toast.LENGTH_LONG).show()
                lateinit var add : String
                try {
                    add = getAddress(location.latitude, location.longitude)
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
                //binding.tvLocation.setText(add)

                Log.d("fetchLocation found",add)
                Toast.makeText(applicationContext,add, Toast.LENGTH_LONG).show()
            }
            Log.d("fetchLocation","wrong IF")

        }
    }
    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].getAddressLine(0)
    }

}