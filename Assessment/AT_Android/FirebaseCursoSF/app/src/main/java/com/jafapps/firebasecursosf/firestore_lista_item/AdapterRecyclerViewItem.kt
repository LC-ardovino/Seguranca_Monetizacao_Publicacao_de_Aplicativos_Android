package com.jafapps.firebasecursosf.firestore_lista_item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jafapps.firebasecursosf.R
import com.jafapps.firebasecursosf.firestore_lista_categoria.AdapterRecyclerViewCategoria
import com.jafapps.firebasecursosf.firestore_lista_categoria.Categoria

class AdapterRecyclerViewItem (val context: Context, var itens: ArrayList<Item>, var clickItem: ClickItem) :
    RecyclerView.Adapter<AdapterRecyclerViewItem.ViewHolder>() {





    //diz qual layout é dos itens

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_lista_item_recyclerview,parent,false)

        val holder = AdapterRecyclerViewItem.ViewHolder(view)

        return holder


    }



    //qual tamanho  da lista

    override fun getItemCount(): Int {

        return itens.size
    }





    //inserir informações no layout

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = itens.get(position)

        holder.nome.setText(item.nome)
        holder.descricao.setText(item.descricao)

        Glide.with(context).load(item.url_imagem).into(holder.imagem)


        holder.cardView.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {

                clickItem.clickItem(item)
            }
        })


    }





    //interface
    interface ClickItem{


        fun clickItem(item: Item)

    }



    //ligar variaveis com itens(elementos) do layout

    class  ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        val cardView = itemView.findViewById<CardView>(R.id.cardView_ListaItem)
        val imagem = itemView.findViewById<ImageView>(R.id.imageView_ListaItem_Imagem)
        val nome = itemView.findViewById<TextView>(R.id.textView_ListaItem_Nome)
        val descricao = itemView.findViewById<TextView>(R.id.textView_ListaItem_Descricao)


    }

}