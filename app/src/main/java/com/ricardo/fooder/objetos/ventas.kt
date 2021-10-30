package com.ricardo.fooder.objetos

class ventas {

    var idvtas:Int?=null
    var cliente:String?=null
    var telefono:String?=null
    var direccion:String?=null
    var idItem:String?=null
    var totalVta:String?=null
    var fechaVta:String?=null

    var observacion:String?=null



    constructor(){}

    constructor(idvtas_:Int,cliente_:String,telefono_:String,direccion_:String,idItem_:String,totalVta_:String,fechaVta_:String,observacion_:String){

        this.idvtas=idvtas_
        this.cliente=cliente_
        this.telefono=telefono_
        this.direccion=direccion_
        this.idItem=idItem_
        this.totalVta=totalVta_
        this.fechaVta=fechaVta_
        this.observacion=observacion_


    }
}