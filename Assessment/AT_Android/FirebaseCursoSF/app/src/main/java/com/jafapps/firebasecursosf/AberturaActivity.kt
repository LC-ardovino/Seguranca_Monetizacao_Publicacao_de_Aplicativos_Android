package com.jafapps.firebasecursosf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_abertura.*


class AberturaActivity : AppCompatActivity(), View.OnClickListener {


    var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abertura)

        button_Login.setOnClickListener(this)

        auth = Firebase.auth

        val user = auth?.currentUser



        if(user != null){


           val uid = user.uid

            finish()


            val intent =  Intent(this,MainActivity::class.java)


            startActivity(intent)
        }



    }





    override fun onClick(p0: View?) {

        when(p0?.id){

            button_Login.id -> {

                buttonLogin()
            }
            else -> false
        }

    }




    fun buttonLogin(){

        val email = editText_Login_Email.text.toString()
        val senha = editText_Login_Senha.text.toString()

        if( !email.trim().equals("") && !senha.trim().equals("")){

            if (Util.statusInternet(this)){

               login(email, senha)

            }else{
                Util.exibirToast(this,"Você não possui uma conexão com a internet")
            }
        }else{

            Util.exibirToast(this,"Preencha todos os dados")
        }
    }




    fun login(email:String, senha:String) {

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager,"1")

        auth?.signInWithEmailAndPassword(email,senha)?.addOnCompleteListener(this){  task ->

            dialogProgress.dismiss()

            if (task.isSuccessful){

                // Util.exibirToast(baseContext,"Sucesso ao fazer Login")

                finish()
                val intent =  Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else{


                val erro = task.exception.toString()
                errosFirebase(erro)
            }

        }





    }















    fun errosFirebase(erro: String){


        if( erro.contains("There is no user record corresponding to this identifier")){

            Util.exibirToast(baseContext,"Esse e-mail não está cadastrado ainda")

        }
        else if( erro.contains("The password is invalid")){

            Util.exibirToast(baseContext,"Senha inválida")

        }
        else if(erro.contains("The email address is badly ")){

            Util.exibirToast(baseContext,"Este e-mail não é válido")

        }


    }




}