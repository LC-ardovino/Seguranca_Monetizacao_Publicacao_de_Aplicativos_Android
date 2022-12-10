package com.jafapps.firebasecursosf

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jafapps.firebasecursosf.firestore.FirestoreGravarAlterarRemoverActivity
import com.jafapps.firebasecursosf.firestore_lista_categoria.FirestoreListaCategoriaActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        cardView_Main_GravarAlterarRemoverDados.setOnClickListener(this)
        cardView_Main_Categorias.setOnClickListener(this)
        cardView_Main_Deslogar.setOnClickListener(this)

        permissao()
        ouvinteAutenticacao()


    }




    //---------------------------------------PERMISSAO------------------------------------


    fun permissao(){

        val permissoes = arrayOf<String >(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA
        )

        Util.permissao(this,100,permissoes)

    }







    //---------------------------------------OUVINTE AUTENTICACAO-------------------------------------


    fun ouvinteAutenticacao(){

        Firebase.auth.addAuthStateListener { authAtual ->

            if(authAtual.currentUser != null ){

                Util.exibirToast(baseContext,"Usuário Logado")
            }else{

                Util.exibirToast(baseContext,"Usuário Deslogado")
            }
        }

    }















    //---------------------------------------AÇÕES DE CLICK-------------------------------------

    override fun onClick(p0: View?) {

        when(p0?.id){


            cardView_Main_GravarAlterarRemoverDados.id -> {
                startActivity(Intent(this,FirestoreGravarAlterarRemoverActivity::class.java))
            }

            cardView_Main_Categorias.id-> {
                startActivity(Intent(this,FirestoreListaCategoriaActivity::class.java))

            }

            cardView_Main_Deslogar.id -> {

                finish()
                Firebase.auth.signOut()
                startActivity(Intent(this,AberturaActivity::class.java))
            }
            else -> false

        }

    }


















    //---------------------------------------PERMISSAO------------------------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for( result in grantResults){
            if(result == PackageManager.PERMISSION_DENIED){

                Util.exibirToast(baseContext,"Aceite as permissões para funcionar o aplicativo")
                finish()
                break
            }
        }


    }




}