package com.example.etapa2.PacoteUtil

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    view: View,
    msg:String,
    lenght:Int,
    actionMessage:CharSequence?,
    action:(View)->Unit
){
    val snackbar = Snackbar.make(view,msg,lenght)
    if (actionMessage != null){
        snackbar.setAction(actionMessage){
            action(this)
        }.show()
    }else{
        snackbar.show()
    }
}