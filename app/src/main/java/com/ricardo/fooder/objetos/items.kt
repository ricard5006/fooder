package com.ricardo.fooder.objetos

import android.os.Parcelable
import java.io.Serializable

class items:Serializable {

    var idItem:String?=null
    var nombreItem:String?=null
    var descripcionItem:String?=null
    var precioItem:String?=null
    var enableItem:String?=null


    constructor(){}

    constructor(idItem_:String,nombreItem_:String,descripcionItem_:String,precioItem_:String,enableItem_:String){

        this.idItem=idItem_
        this.nombreItem=nombreItem_
        this.descripcionItem=descripcionItem_
        this.precioItem=precioItem_
        this.enableItem=enableItem_



    }

}