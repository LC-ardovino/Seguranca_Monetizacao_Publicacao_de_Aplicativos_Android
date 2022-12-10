package com.example.etapa2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.etapa2.PacoteUtil.showSnackbar
import com.example.etapa2.databinding.ActivityMainBinding
import com.example.etapa2.databinding.ActivityRegistraPontoBinding
import com.google.android.material.snackbar.Snackbar



class MainActivity : AppCompatActivity() {

    private lateinit var layout:View
    private lateinit var binding2: ActivityRegistraPontoBinding
    private lateinit var binding:ActivityMainBinding
    //Permissoes concedidas
    private var isLocationPermissionGranted = false
    private var isCameraPermisssionGranted = false

    //Lancador de permissoes:
    private lateinit var multiplePermissionsLauncher:ActivityResultLauncher<Array<String>>
    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        setups()

    }

    fun setups(){
        setupViews()
        setupCheckPermissions()
    }

    fun setupViews(){
        setupClickListeners()
    }

    fun setupClickListeners(){
        binding.button.setOnClickListener {
            requestPermissions()
        }
    }

    private fun setupCheckPermissions(){
        //Lancador de requisicao de uma permissao:
        singlePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted:Boolean -> if (isGranted){
            Log.i("Permission:","Granted")}else{
                Log.i("Permission:","Denied")
            }
        }

        multiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            permissions ->
            isCameraPermisssionGranted = permissions[android.Manifest.permission.CAMERA] ?: isCameraPermisssionGranted
            isLocationPermissionGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
        }
    }

    fun requestPermissions(){
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest:MutableList<String> = ArrayList()

        if(!isLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if(!permissionRequest.isEmpty()){
            when{
                !isLocationPermissionGranted->{
                    askLocationRationale()
                }
            }
        }else{
            startPontoActivity()
        }

    }

    fun askLocationRationale(){
        layout.showSnackbar(
            binding.mainLayout,
            getString(R.string.permission_location_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ){
            singlePermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////



    fun startPontoActivity(){
        startActivity(Intent(this, RegistraPontoActivity::class.java))
    }
}