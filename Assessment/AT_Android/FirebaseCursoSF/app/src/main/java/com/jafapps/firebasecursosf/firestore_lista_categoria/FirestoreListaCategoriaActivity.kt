package com.jafapps.firebasecursosf.firestore_lista_categoria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.jafapps.bdfirestore.util.DialogProgress
import com.br.jafapps.bdfirestore.util.Util
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jafapps.firebasecursosf.R
import com.jafapps.firebasecursosf.firestore_lista_item.FirestoreListaItemActivity
import kotlinx.android.synthetic.main.activity_firestore_lista_categoria.*

class FirestoreListaCategoriaActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    SearchView.OnCloseListener, AdapterRecyclerViewCategoria.ClickCategoria,
    AdapterRecyclerViewCategoria.UltimoItemExibidoRecyclerView {

    var searchView: SearchView? = null

    var adapterRecyclerViewCategoria: AdapterRecyclerViewCategoria? = null
    var categorias: ArrayList<Categoria> = ArrayList()

    var database: FirebaseFirestore? = null
    var reference: CollectionReference? = null
    var proximoQuery: Query? = null


    var isFiltrando = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_lista_categoria)


        supportActionBar?.title = "Categorias"

        button_Firestore_ListaCategoriaExibirMais.visibility = View.GONE

        /*
        button_Firestore_ListaCategoriaExibirMais.setOnClickListener{

           exibirMaisItensBD()
        }
         */




        database = FirebaseFirestore.getInstance()
        reference = database?.collection("Categorias")

        iniciarRecyclerView()


        exibirPrimeirosItensBD()



    }









    //--------------------------------------------------RECYCLERVIEW - LISTA---------------------------


    fun iniciarRecyclerView(){


        adapterRecyclerViewCategoria =AdapterRecyclerViewCategoria(baseContext,categorias,this,this)


        recyclerView_Firestore_ListaCategoria.layoutManager = LinearLayoutManager(this)
        recyclerView_Firestore_ListaCategoria.adapter = adapterRecyclerViewCategoria



    }



    //CLICK EM ITEM DA LISTA
    override fun clickCategoria(categoria: Categoria) {



        val intent = Intent(this,FirestoreListaItemActivity::class.java)
        intent.putExtra("categoriaNome",categoria)

        startActivity(intent)


    }


    //ultimo item exibido
    override fun ultimoItemExibidoRecyclerView(isExibido: Boolean) {


        if(isFiltrando){

            //     Util.exibirToast(this,"Você está filtrando. Não vai ser exibido mais itens")

        }else{

            exibirMaisItensBD()
        }

    }

















    //--------------------------------------------------MENU OPÇÕES COM PESQUISA---------------------------



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search,menu)

        val search = menu!!.findItem(R.id.action_search)

        searchView = search.actionView as SearchView

        searchView?.queryHint = "Pesquisar nome"

        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)
        searchView?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES



        return super.onCreateOptionsMenu(menu)
    }














    //--------------------------------------------------METODOS SEARCH PARA PESQUISA---------------------------


    override fun onQueryTextSubmit(query: String?): Boolean {


        Log.d("yyyyui-onQueryTextS","onQueryTextSubmit")

        return true
    }




    override fun onQueryTextChange(newText: String?): Boolean {

        Log.d("yyyyui-onQueryTextC",newText.toString())


        isFiltrando = true
        pesquisarNome(newText.toString())

        return true
    }



    override fun onClose(): Boolean {

        isFiltrando = false

        searchView?.onActionViewCollapsed()


        categorias.clear()
        adapterRecyclerViewCategoria?.notifyDataSetChanged()

        exibirPrimeirosItensBD()


        return true
    }






















    //----------------------------------------------PESQUISA (FILTRO) POR NOME NO FIREBASE---------------------------




    fun pesquisarNome(newText: String){


        //j

        val query = database!!.collection("Categorias")
            .orderBy("nome").startAt(newText).endAt(newText+"\uf8ff").limit(3)



        query.get().addOnSuccessListener { documentos ->



            categorias.clear()

            for(documento in documentos){


                val categoria = documento.toObject(Categoria::class.java)
                categorias.add(categoria)
            }


            adapterRecyclerViewCategoria?.notifyDataSetChanged()


        }





    }





























    //--------------------------------------------------LER DADOS FIREBASE - PAGINAÇÃO---------------------------


    fun exibirPrimeirosItensBD(){


        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager,"1")


        var query = database!!.collection("Categorias").orderBy("nome").limit(10)



        query.get().addOnSuccessListener { documentos ->


            dialogProgress.dismiss()


            val ultimoDOcumento = documentos.documents[documentos.size() - 1]
            proximoQuery = database!!.collection("Categorias").orderBy("nome").startAfter(ultimoDOcumento).limit(10)


            for(documento in documentos){

                var categoria = documento.toObject(Categoria::class.java)
                categorias.add(categoria)
            }


            adapterRecyclerViewCategoria?.notifyDataSetChanged()


        }.addOnFailureListener {error ->


            Util.exibirToast(baseContext,"Error : ${error.message}")
            dialogProgress.dismiss()

        }

    }









    fun exibirMaisItensBD(){


        val dialogProgress = DialogProgress()
        dialogProgress.show(supportFragmentManager,"1")


        proximoQuery!!.get().addOnSuccessListener { documentos ->



            dialogProgress.dismiss()


            if(documentos.size() > 0 ){

                val ultimoDOcumento = documentos.documents[documentos.size() - 1]
                proximoQuery = database!!.collection("Categorias").orderBy("nome").startAfter(ultimoDOcumento).limit(10)


                for(documento in documentos){

                    val categoria = documento.toObject(Categoria::class.java)
                    categorias.add(categoria)
                }


                adapterRecyclerViewCategoria?.notifyDataSetChanged()

            }else{


                ///Util.exibirToast(baseContext,"Acabou a categorias")
                //  button_Firestore_ListaCategoriaExibirMais.visibility = View.GONE

            }


        }.addOnFailureListener {error ->


            Util.exibirToast(baseContext,"Error : ${error.message}")
            dialogProgress.dismiss()


        }





    }



}