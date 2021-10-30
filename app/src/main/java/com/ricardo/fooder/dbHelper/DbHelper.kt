package com.ricardo.fooder.dbHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.ricardo.fooder.objetos.items
import com.ricardo.fooder.objetos.parametros
import com.ricardo.fooder.objetos.ventas

class DbHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {

    companion object{
        private  val DATABASE_VER=1
        private  val DATABASE_NAME="fooder.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try{
            val CREATE_TABLE_CONFIG:String=
                ("CREATE TABLE configuracion (parametro TEXT PRIMARY KEY,valor TEXT DEFAULT '')")

            db!!.execSQL(CREATE_TABLE_CONFIG)

            var INSERT_PARAMETROS1 =
                ("INSERT INTO configuracion (parametro)VALUES('macPrinter')")
            db!!.execSQL(INSERT_PARAMETROS1)


            val CREATE_TABLE_ITEMS:String=
                ("CREATE TABLE items (idItem integer primary key autoincrement,nombreItem TEXT DEFAULT '',descripcionItem TEXT DEFAULT '',precioItem TEXT DEFAULT '',enableItem TEXT DEFAULT '1')")

            db!!.execSQL(CREATE_TABLE_ITEMS)


            val CREATE_TABLE_VENTS:String=
                ("CREATE TABLE ventas (idvtas INTEGER ,cliente TEXT DEFAULT '',telefono TEXT DEFAULT '',direccion TEXT DEFAULT '',idItem TEXT DEFAULT '',fechaVta TEXT DEFAULT '',observacion TEXT DEFAULT '')")

            db!!.execSQL(CREATE_TABLE_VENTS)


        }catch (ex: SQLiteException){
            val algo="error"
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS configuracion")
        db!!.execSQL("DROP TABLE IF EXISTS items")
        db!!.execSQL("DROP TABLE IF EXISTS ventas")
        onCreate(db!!)
    }

    fun updateParametro(objParam: parametros):Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("parametro", objParam.parametro)
        values.put("valor", objParam.valor)
        return db.replace("CONFIGURACION", null, values)

    }

    fun selectParametro(objParam: parametros):String {
        var dato=""
        val selectQuery = "SELECT * FROM configuracion WHERE parametro = ?"
        val db = this.writableDatabase

        val cursor = db.rawQuery(selectQuery, arrayOf(objParam.parametro.toString()))
        if (cursor.moveToFirst()) {

            do {
                // val parametro = Parametros()
                // parametro.parametro = cursor.getString(cursor.getColumnIndex("parametro"))
                dato = cursor.getString(cursor.getColumnIndex("valor"))


            } while (cursor.moveToNext())

        }

        db.close()
        return dato



    }

    fun insertItem(itm:items):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("nombreItem",itm.nombreItem)
        values.put("descripcionItem",itm.descripcionItem)
        values.put("precioItem",itm.precioItem)


        return db.insert("items",null,values)
    }

    fun updateItem(itm:items){

        val db = this.writableDatabase
        val values = ContentValues()

        values.put("enableItem",itm.enableItem)


        db.update("items",values,"idItem=?" ,arrayOf(itm.idItem.toString()))
    }

    fun selectItems():List<items>{
        val listItems = ArrayList<items>()
        try{

            val selectQuery = "SELECT * FROM items where enableItem = '1' "
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {
                    val item_ = items()
                    item_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    item_.nombreItem = cursor.getString(cursor.getColumnIndex("nombreItem"))
                    item_.descripcionItem = cursor.getString(cursor.getColumnIndex("descripcionItem"))
                    item_.precioItem = cursor.getString(cursor.getColumnIndex("precioItem"))


                    listItems.add(item_)

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){}
        return listItems

    }

    fun selectItemsBy(itm:items):List<items>{
        val listItems = ArrayList<items>()
        try{

            val selectQuery = "SELECT * FROM items where nombreItem like ?"
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, arrayOf(itm.nombreItem.toString()))

            if (cursor.moveToFirst()) {

                do {
                    val item_ = items()
                    item_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    item_.nombreItem = cursor.getString(cursor.getColumnIndex("nombreItem"))
                    item_.descripcionItem = cursor.getString(cursor.getColumnIndex("descripcionItem"))
                    item_.precioItem = cursor.getString(cursor.getColumnIndex("precioItem"))


                    listItems.add(item_)

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){}
        return listItems

    }

    fun updateVentas(vts:ventas):Long{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("idvtas",vts.idvtas)
        values.put("cliente",vts.cliente)
        values.put("telefono",vts.telefono)
        values.put("direccion",vts.direccion)
        values.put("idItem",vts.idItem)

        values.put("fechaVta",vts.fechaVta)
        values.put("observacion",vts.observacion)

        return db.insert("ventas",null,values)
    }

    fun selectVentasbyFecha(objventa:ventas):List<ventas>{
        val listVtas = ArrayList<ventas>()
        try{

            val selectQuery = "select *,sum(precioItem) AS totalVenta from ventas" +
                    " INNER JOIN items on items.idItem = ventas.idItem" +
                    " where ventas.fechaVta='"+ objventa.fechaVta.toString() +"'" +
                    " group by idvtas,cliente,telefono,direccion,fechaVta"
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {
                    val vtas_ = ventas()
                    vtas_.idvtas = cursor.getInt(cursor.getColumnIndex("idvtas"))
                    vtas_.cliente = cursor.getString(cursor.getColumnIndex("cliente"))
                    vtas_.telefono = cursor.getString(cursor.getColumnIndex("telefono"))
                    vtas_.direccion = cursor.getString(cursor.getColumnIndex("direccion"))
                    vtas_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    //vtas_.item?.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    vtas_.totalVta = cursor.getString(cursor.getColumnIndex("totalVenta"))
                    vtas_.fechaVta = cursor.getString(cursor.getColumnIndex("fechaVta"))

                    listVtas.add(vtas_)

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){
            val algo=e.message
        }
        return listVtas
    }

    fun selectItemsByIdVta(idVta:String):List<items>{
        val listItems = ArrayList<items>()
        try{

            val selectQuery = "select * from ventas " +
                    "left JOIN items on items.idItem = ventas.idItem " +
                    "where idvtas='"+ idVta +"'"
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {
                    val item_ = items()
                    item_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    item_.nombreItem = cursor.getString(cursor.getColumnIndex("nombreItem"))
                    item_.descripcionItem = cursor.getString(cursor.getColumnIndex("observacion"))
                    item_.precioItem = cursor.getString(cursor.getColumnIndex("precioItem"))


                    listItems.add(item_)

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){}
        return listItems
    }

    fun selectVentasbyItem(objventa:ventas):List<items>{
        val listItm = ArrayList<items>()
        try{

            val selectQuery = "select items.idItem,items.nombreItem,sum(items.precioItem) as precioItem,items.descripcionItem from ventas " +
                    "INNER join items on items.idItem = ventas.idItem " +
                    "where ventas.fechaVta='"+ objventa.fechaVta.toString() +"' "+
                    "group by items.idItem"

            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {
                    val itm_ = items()
                    itm_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    itm_.nombreItem = cursor.getString(cursor.getColumnIndex("nombreItem"))
                    itm_.precioItem = cursor.getString(cursor.getColumnIndex("precioItem"))
                    itm_.descripcionItem = cursor.getString(cursor.getColumnIndex("descripcionItem"))

                    listItm.add(itm_)

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){
            val algo=e.message
        }
        return listItm
    }



    fun lastVenta():ventas{
        val vtas_ = ventas()
        try{

            val selectQuery = "SELECT * FROM ventas ORDER by idvtas DESC limit 1"
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {

                    vtas_.idvtas = cursor.getInt(cursor.getColumnIndex("idvtas"))
                    vtas_.cliente = cursor.getString(cursor.getColumnIndex("cliente"))
                    vtas_.telefono = cursor.getString(cursor.getColumnIndex("telefono"))
                    vtas_.direccion = cursor.getString(cursor.getColumnIndex("direccion"))
                    vtas_.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    //vtas_.item?.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    vtas_.fechaVta = cursor.getString(cursor.getColumnIndex("fechaVta"))

                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){}
        return vtas_
    }

    fun selectVentaByIdVta(idVta: String): ventas {
        val ventas = ventas()

        try{

            val selectQuery = "select ventas.idvtas,ventas.cliente,ventas.telefono,ventas.direccion,ventas.idItem,sum(items.precioItem) as totalVta,ventas.fechaVta from ventas " +
                    "INNER JOIN items on items.idItem = ventas.idItem " +
                    "where idvtas='"+ idVta +"'"
            val db = this.writableDatabase

            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {

                    ventas.cliente = cursor.getString(cursor.getColumnIndex("cliente"))
                    ventas.telefono = cursor.getString(cursor.getColumnIndex("telefono"))
                    ventas.direccion = cursor.getString(cursor.getColumnIndex("direccion"))
                    ventas.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    //ventas.item?.idItem = cursor.getString(cursor.getColumnIndex("idItem"))
                    ventas.totalVta = cursor.getString(cursor.getColumnIndex("totalVta"))
                    ventas.fechaVta = cursor.getString(cursor.getColumnIndex("fechaVta"))


                } while (cursor.moveToNext())

            }
            db.close()

        }catch (e:Exception){}

        return ventas
    }


}