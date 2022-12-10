package com.jafapps.firebasecursosf.firestore_lista_item

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jafapps.firebasecursosf.R
import com.jafapps.firebasecursosf.firestore_lista_categoria.Categoria
import kotlinx.android.synthetic.main.activity_firestore_lista_item.*

class FirestoreListaItemActivity : AppCompatActivity(), View.OnClickListener,
    AdapterRecyclerViewItem.ClickItem {


    var categoria: Categoria? = null



    var adapterRecyclerViewItem: AdapterRecyclerViewItem? = null
    var itens: ArrayList<Item> = ArrayList()

    var database: FirebaseFirestore? = null
    var reference: CollectionReference? = null

    var storage: FirebaseStorage? = null


    var uri_Imagem: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_lista_item)


        categoria = intent.getParcelableExtra("categoriaNome")
        supportActionBar?.title = categoria?.nome

        database = FirebaseFirestore.getInstance()
        storage = Firebase.storage





        Util.exibirToast(this,categoria?.nome.toString() + " - " +categoria?.id.toString() )
        

        button_Firestore_Item_Salvar.setOnClickListener(this)
        imageView_Firestore_Item_LimparCampo.setOnClickListener(this)
        imageView_Firestore_Item_Galeria.setOnClickListener(this)

        iniciarRecyclerView()

        ouvinte()

    }
























    //--------------------------------------------------RECYCLERVIEW - LISTA---------------------------


    fun iniciarRecyclerView(){



        adapterRecyclerViewItem = AdapterRecyclerViewItem(baseContext,itens,this)


        recyclerView_Firestore_ListaItem.layoutManager = LinearLayoutManager(this)
        recyclerView_Firestore_ListaItem.adapter = adapterRecyclerViewItem
    }





    override fun clickItem(item: Item) {


        val intent = Intent(this,FirestoreItemDadosActivity::class.java)


        intent.putExtra("idCategoria",categoria?.id)
        intent.putExtra("item",item)

        startActivity(intent)

    }


























    override fun onClick(p0: View?) {


        when(p0?.id){


            imageView_Firestore_Item_LimparCampo.id ->{
                limparCampos()

            }
            imageView_Firestore_Item_Galeria.id ->{

                obterImagemGaleria()


            }
            button_Firestore_Item_Salvar.id ->{

                buttonSalvar()

            }
        }

    }












    //---------------------------------------METODOS DE CLICKS GALERIA E CAMERA-------------------------------------


    fun limparCampos(){

        editText_Firestore_Item_Nome.setText("")
        editText_Firestore_Item_Descricao.setText("")

        uri_Imagem = null
        imageView_Firestore_Item_Galeria.setImageResource(R.drawable.ic_galeria_24)

    }






    fun obterImagemGaleria(){

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        startActivityForResult(Intent.createChooser(intent,"Escolha uma Imagem"),11)

    }




    fun buttonSalvar(){


        val nome = editText_Firestore_Item_Nome.text.toString()
        val descricao = editText_Firestore_Item_Descricao.text.toString()


        if(!nome.trim().isEmpty()  &&  !descricao.trim().isEmpty()){


            if(Util.statusInternet(this)){

                if(uri_Imagem != null){

                 //   uploadImagem(nome,descricao)
                    verificarDocumentoExiste2(nome,descricao)

                }else{
                    Util.exibirToast(this,"Selecione uma imagem")

                }
            }else{

                Util.exibirToast(this,"Sem conexão com a internet")
            }
        }
        else{

            Util.exibirToast(this,"Preencha os campos obrigatórios")
        }

    }







    fun verificarDocumentoExiste2(nome: String, descricao: String){




        val uid =  Firebase.auth?.currentUser?.uid


        val idItem: String = database!!.collection("Categorias")
                .document(categoria?.id.toString()).collection("usuarios").document(uid!!)
                .collection("itens").document().id


        uploadImagem(nome, descricao,idItem)



    }






/*


    fun verificarDocumentoExiste(nome: String, descricao: String){


        val idItem = System.currentTimeMillis().toInt()

        val reference = database!!.collection("Categorias")
            .document(categoria?.id.toString()).collection("itens").document(idItem.toString())


        reference.get().addOnSuccessListener { documento ->



            if(documento.exists()){
                Util.exibirToast(baseContext,"Erro ao salvar dados, a identificação desse documento já existe ")


            }else{

                uploadImagem(nome, descricao,idItem)
            }
        }


    }

*/










    fun uploadImagem(nome: String, descricao: String, idItem: String){



        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager,"1")



        val nomeImagem = idItem.toString() +".jpg"


        //  val idItem = System.currentTimeMillis().toInt()
      //  val nomeImagem = UUID.randomUUID().toString() +".jpg"


        val uid =  Firebase.auth?.currentUser?.uid


        val reference = storage!!.reference.child("Categorias")
                                    .child(categoria?.id.toString()).child("usuarios").child(uid!!)
            .child("itens").child(nomeImagem)



        var uploadTask = reference.putFile(uri_Imagem!!)


        uploadTask.continueWithTask{ task ->

            if(!task.isSuccessful){
                task.exception.let {
                    throw it!!
                }
            }
            reference.downloadUrl

        }.addOnSuccessListener { task->

            val url = task.toString()


            dialogProgress.dismiss()

            salvarDados(nome,descricao,url,idItem)

        }.addOnFailureListener{ error ->

            dialogProgress.dismiss()
            Util.exibirToast(baseContext,"Erro ao realizar o upload da imagem: ${error.message.toString()}")
        }



    }









    fun salvarDados(nome: String, descricao:String, url:String, idItem: String){


        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager, "0")

       // val nomeDocumento = System.currentTimeMillis().toInt()

        val nomeDocumento =  idItem

        val uid =  Firebase.auth?.currentUser?.uid

        val reference = database!!.collection("Categorias")
                                    .document(categoria?.id.toString()).collection("usuarios").document(uid!!)
                                    .collection("itens")



        val item = Item(nomeDocumento,nome,descricao,url)


        reference.document(nomeDocumento.toString()).set(item).addOnSuccessListener {

            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Sucesso ao gravar dados")

            limparCampos()


        }.addOnFailureListener { error ->

            dialogProgress.dismiss()
            Util.exibirToast(baseContext, "Erro ao gravar dados: ${error.message.toString()}")


        }






    }
























    //--------------------------------------------------OUVINTE - LER DADOS FIREBASE---------------------------



    fun ouvinte(){

        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager,"1")

        val uid =  Firebase.auth?.currentUser?.uid

        reference = database!!.collection("Categorias")
            .document(categoria?.id.toString()).collection("usuarios").document(uid!!)
            .collection("itens")


        reference?.addSnapshotListener{ documentos, error->


            dialogProgress.dismiss()

            if( error !=null){

                Util.exibirToast(baseContext,"Erro ao ler do Banco: ${error.message}")
            }

            else{

                if(documentos!!.size() == 0){
                    Util.exibirToast(baseContext,"Você não tem itens na categoria")
                }


                for(doc in documentos!!.documentChanges){


                    when(doc.type){

                        DocumentChange.Type.ADDED ->{


                            val item = doc.document.toObject(Item::class.java)

                            itens.add(item)

                            adapterRecyclerViewItem?.notifyDataSetChanged()

                        }
                        DocumentChange.Type.MODIFIED ->{


                            val item = doc.document.toObject(Item::class.java)


                           // val key = doc.document.id.toInt()
                            val key = doc.document.id

                            val index = itens.indexOfFirst { i ->

                                i.id == key
                            }

                            itens.set(index,item)

                            adapterRecyclerViewItem?.notifyDataSetChanged()


                        }

                        DocumentChange.Type.REMOVED ->{


                          //  val key = doc.document.id.toInt()

                            val key = doc.document.id

                            val index = itens.indexOfFirst {

                                it.id == key
                            }



                            itens.removeAt(index)

                            adapterRecyclerViewItem?.notifyItemRemoved(index)
                            adapterRecyclerViewItem?.notifyItemChanged(index,itens.size)
                            adapterRecyclerViewItem?.notifyDataSetChanged()

                        }
                    }
                }
            }
        }
    }













    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(resultCode == Activity.RESULT_OK){

            if( requestCode == 11 && data != null ){ // galeria

                uri_Imagem = data.data
                imageView_Firestore_Item_Galeria.setImageURI(uri_Imagem)
            }


        }

    }






}