package com.example.android.escritaleiturasd

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.android.escritaleiturasd.databinding.ActivityMainBinding
import com.example.android.escritaleiturasd.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception


////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Escrita e leitura no SD CARD ////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

///// Passo 01: Verificar se pode ler e escrever
///// Passo 02: Solicitar permissões
///// Passo 03: Escrever no disco
///// Passo 04: Ler do disco
///// Passo 05: listar arquivos do SDCARD

class MainActivity : AppCompatActivity() {

    // TAG para log
    val TAG = "ReadWrite"

    // Variáveis auxiliares
    var nomeArquivo = ""
    var textoArquivo = ""

    // Lançador de Activity para permissões
    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>

    // Binding do layout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setup()
    }

    fun setup() {
        setupViews()
        setupCheckPermissionArquivo()
    }

    fun setupViews() {
        setupClickListeners()
    }

    fun setupClickListeners() {
        // Passo 03: Escrever no disco
        binding.buttonSalvar.setOnClickListener {
            requestWritePermission()
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
        binding.layout.showSnackbar(
            binding.layout,
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
        binding.layout.showSnackbar(
            binding.layout,
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
            val endereco = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            nomeArquivo = binding.nomeTV.text.toString()
            textoArquivo = binding.textoTV.text.toString()
            Log.i(TAG, "textoArquivo = $textoArquivo")
            val file = File("$endereco/$nomeArquivo")
            Log.i(TAG, "Criando arquivo em")
            Log.i(TAG, "${file.absolutePath}")
            try {
                file.writeText(textoArquivo)
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
            val endereco = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            nomeArquivo = binding.nomeTV.text.toString()
            textoArquivo = binding.textoTV.text.toString()
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
            val endereco = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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