package com.jafapps.firebasecursosf.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jafapps.firebasecursosf.R
import kotlinx.android.synthetic.main.activity_firestore_gravar_alterar_remover.*

class FirestoreGravarAlterarRemoverActivity : AppCompatActivity(), View.OnClickListener {


    var bd: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_gravar_alterar_remover)


        button_Firestore_GravarAlterarRemover_Salvar.setOnClickListener(this)
        button_Firestore_GravarAlterarRemover_Alterar.setOnClickListener(this)
        button_Firestore_GravarAlterarRemover_Remover.setOnClickListener(this)


        bd = FirebaseFirestore.getInstance()

    }


    //------------------------------------------------CLICKS------------------------------------------------------------

    override fun onClick(p0: View?) {

        when (p0?.id) {


            button_Firestore_GravarAlterarRemover_Salvar.id -> {

                buttonSalvar()

            }
            button_Firestore_GravarAlterarRemover_Alterar.id -> {

                buttonAlterar()


            }
            button_Firestore_GravarAlterarRemover_Remover.id -> {


              buttonRemover()


            }
            else -> return


        }
    }


    //---------------------------------------------AÇÃO DE CLICKS------------------------------------------------------------


    fun buttonSalvar() {

        val nome = editText_Firestore_GravarAlterarRemover_GerenteNome.text.toString()
        val idadeString = editText_Firestore_GravarAlterarRemover_GerenteIdade.text.toString()
        val idNomePasta = editText_Firestore_GravarAlterarRemover_NomePasta.text.toString()


        if (!nome.trim().isEmpty() && !idadeString.trim().isEmpty() && !idNomePasta.trim().isEmpty()) {


            if (Util.statusInternet(baseContext)) {


                val idade = idadeString.toInt()


                salvarDados(nome, idade, idNomePasta)

            } else {

                Util.exibirToast(baseContext, "Sem conexão com a Internet")
            }
        } else {

            Util.exibirToast(baseContext, "Insira todos campos de forma correta")
        }

    }








    fun buttonAlterar(){


        val nome = editText_Firestore_GravarAlterarRemover_GerenteNome.text.toString()
        val idadeString = editText_Firestore_GravarAlterarRemover_GerenteIdade.text.toString()
        val idNomePasta = editText_Firestore_GravarAlterarRemover_NomePasta.text.toString()



        if (!nome.trim().isEmpty() && !idadeString.trim().isEmpty() && !idNomePasta.trim().isEmpty()) {


            if (Util.statusInternet(baseContext)) {


                val idade = idadeString.toInt()

                alterarDados(nome,idade, idNomePasta)

            } else {

                Util.exibirToast(baseContext, "Sem conexão com a Internet")
            }
        } else {

            Util.exibirToast(baseContext, "Insira todos campos de forma correta")
        }






    }





    fun buttonRemover(){



        val idNomePasta = editText_Firestore_GravarAlterarRemover_NomePasta.text.toString()



        if ( !idNomePasta.trim().isEmpty()) {


            if (Util.statusInternet(baseContext)) {


                removerDados(idNomePasta)

            } else {

                Util.exibirToast(baseContext, "Sem conexão com a Internet")
            }
        } else {

            Util.exibirToast(baseContext, "Insira todos campos de forma correta")
        }






    }




    //---------------------------------------------SALVAR DADOS FIREBASE------------------------------------------------------------


    fun salvarDados(nome: String, idade: Int, idNomePasta: String) {


        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")


        val reference = bd!!.collection("Pessoas")
        val pessoa = Pessoa(nome, idade)


        reference.document(idNomePasta).set(pessoa).addOnSuccessListener {

            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao gravar dados")


        }.addOnFailureListener { error ->

            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao gravar dados: ${error.message.toString()}")


        }



    }


    //---------------------------------------------ALTERAR DADOS FIREBASE------------------------------------------------------------


    fun alterarDados(nome: String, idade: Int, idNomePasta: String){



        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")


        val reference = bd!!.collection("Pessoas")

        val pessoa = hashMapOf(

            "nome" to nome,
            "idade" to idade
        )


        reference.document(idNomePasta).update(pessoa).addOnSuccessListener{


            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao alterar dados")


        }.addOnFailureListener { error ->
            dialogProgress.dismiss()

            Util.exibirToast(baseContext, "Erro ao alterar dados: ${error.message.toString()}")

        }






    }

  //---------------------------------------------REMOVER DADOS FIREBASE------------------------------------------------------------



    fun removerDados(idNomePasta: String){

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")


        val reference = bd!!.collection("Pessoas")


        reference.document(idNomePasta).delete().addOnSuccessListener{


            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao Remover dados")


        }.addOnFailureListener { error ->
            dialogProgress.dismiss()

            Util.exibirToast(baseContext, "Erro ao Reomver dados: ${error.message.toString()}")

        }



    }


}