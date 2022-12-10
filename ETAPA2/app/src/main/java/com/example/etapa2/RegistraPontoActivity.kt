package com.example.etapa2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.etapa2.PacoteUtil.showSnackbar
import com.example.etapa2.databinding.ActivityRegistraPontoBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RegistraPontoActivity : AppCompatActivity(), LocationListener {
    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>

    // TAG para log
    val TAG = "ReadWrite"

    // Variáveis auxiliares
    var nomeArquivo = ""
    var textoArquivo1 = ""
    var textoArquivo2 = ""

    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityRegistraPontoBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistraPontoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupInitialData()
        setupViews()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setupInitialData(){
        setTitle("Registro de Ponto")
        getLocation()
        getDateTime()
        setupCheckPermissionArquivo()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupViews() {
        setupClickListeners()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupClickListeners(){
        binding.buttonSalvar.setOnClickListener{
            requestWritePermission()
        }
        binding.fabVoltar.setOnClickListener{
            Toast.makeText(this,"Voltando",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
        // Passo 04: Ler do disco
        binding.buttonLer.setOnClickListener {
            requestReadPermission()
        }
        // Passo 05: listar arquivos do SDCARD
        binding.buttonLista.setOnClickListener {
            listFiles()
        }
    }

    private fun getLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(
            (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            5000,
            5f,
            this)

    }

    override fun onLocationChanged(location: Location) {
        binding.campoLatitude.text = "%.5f".format(location.latitude)
        binding.campoLongitude.text = "%.5f".format(location.longitude)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

    lateinit var localDateTime: LocalDateTime

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateTime(){
        localDateTime = LocalDateTime.now(ZoneId.of("GMT-3"))
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        binding.campoData.text = localDateTime.format(dateFormatter)
        binding.campoHora.text = localDateTime.format(timeFormatter)
    }

    companion object{
        const val LOCATION_PERMISSION_CODE = 2
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Passo 01: Verificar se pode ler e escrever ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun isExternalStorageWritable(): Boolean {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Log.i(TAG, "Pode escrever no diretorio externo.")
            return true
        } else {
            Log.i(TAG, "Não pode escrever no diretorio externo.")
        }
        return false
    }

    fun isExternalStorageReadable(): Boolean {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            ||
            Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
        ) {
            Log.i(TAG, "Pode ler do diretorio externo.")
            return true
        } else {
            Log.i(TAG, "Não pode ler do diretorio externo.")
        }
        return false
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Passo 02: Solicitar permissões ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun setupCheckPermissionArquivo() {
        // Lançador de requisição de uma permissão:
        singlePermissionLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission() // Requer uma permissão
        ) { // Retorna verdadeiro se a permissão foi concedida
                isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "Granted")
            } else {
                Log.i(TAG, "Denied")
            }
        }
    }

    /////////////// Leitura ////////////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestWritePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                writeFile()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                askWriteRationale()
            }
            else -> {
                singlePermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    fun askWriteRationale() {
        binding.tableLayout.showSnackbar(
            binding.tableLayout,
            getString(R.string.permission_write_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ) {
            singlePermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /////////////// Escrita ////////////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestReadPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                readFile()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                askReadRationale()
            }
            else -> {
                singlePermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    fun askReadRationale() {
        binding.tableLayout.showSnackbar(
            binding.tableLayout,
            getString(R.string.permission_read_required),
            Snackbar.LENGTH_INDEFINITE,
            getString(R.string.ok)
        ) {
            singlePermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Passo 03: Escrever no disco //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun writeFile() {
        if (isExternalStorageWritable()) {
            val endereco =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            nomeArquivo = binding.campoData.text.toString()

            textoArquivo1 = binding.campoLatitude.text.toString()
            textoArquivo2 = binding.campoLongitude.text.toString()
            Log.i(TAG, "textoArquivo = $textoArquivo1")
            val file = File("$endereco/$nomeArquivo")
            Log.i(TAG, "Criando arquivo em")
            Log.i(TAG, "${file.absolutePath}")
            try {
                file.writeText(textoArquivo1)
                file.writeText(textoArquivo2)
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(
                this,
                "Não foi possível escrever no disco",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Passo 04: Ler do disco ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun readFile() {
        if (isExternalStorageReadable()) {
            val endereco =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            nomeArquivo = binding.campoData.text.toString()
            textoArquivo1 = binding.campoLatitude.text.toString()
            textoArquivo2 = binding.campoLongitude.text.toString()
            val path = "$endereco/$nomeArquivo"
            Log.i(TAG, "Lendo do arquivo em")
            Log.i(TAG, "${path}")

            try {
                binding.leituraTV.text = File(path).readText()
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(
                this,
                "Não foi possível ler do disco",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Passo 05: listar arquivos do SDCARD //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun listFiles() {
        if (isExternalStorageReadable()) {
            val endereco =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var listaArquivos = ""
            for (f in endereco.listFiles()) {
                listaArquivos += "${f.name} \n"
            }
            try {
                val msg = "Lista de arquivos da pasta downloads no SD CARD:\n $listaArquivos"
                binding.leituraTV.text = msg
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(
                this,
                "Não foi possível ler do disco",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


}