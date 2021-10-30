package com.ricardo.fooder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.objetos.parametros
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class splash : AppCompatActivity() {

    val DURACION_SPLASH = 2000
    val fecha =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        leerFecha()

      /*  Handler().postDelayed({
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        },DURACION_SPLASH.toLong())*/


prueba.setOnLongClickListener {
    resetFecha()
true}

    }



    fun guardarFecha(){
        //almacena una fecha 15 dias despues de abrir la aplicacion x 1era vez
        val objDb =  DbHelper(this)
        val objParametro = parametros()

        objParametro.parametro="fechaActivo"
        objParametro.valor=fechaLater()
        objDb.updateParametro(objParametro)
    }

    fun leerFecha(){
        //compara la fecha actual con la fecha qe se guardo la primera vez abierta la app.
        val objDb =  DbHelper(this)
        val objParametro = parametros()



        objParametro.parametro="fechaActivo"
        var fechaActivo = objDb.selectParametro(objParametro)


        if(fechaActivo ==""){
            guardarFecha()
            iniciarActivity()

        }else{
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val s = sdf.parse(fechaActivo)

            if(Date().after(s)){

                Toast.makeText(this,"tu periodo de prueba ha terminado, Gracias por tu ayuda!",Toast.LENGTH_LONG).show()
            }else{iniciarActivity()}

        }

    }

    fun fechaNow():String{
        //fecha de hoy
        var fecha=""
        try{

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(Date())

            //

            //

            fecha = currentDate.toString()

        }catch (e:Exception){
            Toast.makeText(this,"Error fechaNow: "+e.message, Toast.LENGTH_SHORT).show()
        }


        return  fecha
    }

    fun fechaLater():String{
        //fecha 15 dias despues de hoy
        var fecha=""
        try{

            val date = Calendar.getInstance()
            date.add(Calendar.DAY_OF_YEAR, +15)


            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(date.time)

            fecha =currentDate.toString()

        }catch (e:Exception){
            Toast.makeText(this,"Error fechaNow: "+e.message, Toast.LENGTH_SHORT).show()
        }


        return  fecha
    }

    private fun resetFecha() {
        val objDb =  DbHelper(this)
        val objParametro = parametros()

        objParametro.parametro="fechaActivo"
        objParametro.valor=""
        objDb.updateParametro(objParametro)
    }

    fun iniciarActivity(){

        Handler().postDelayed({
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        },DURACION_SPLASH.toLong())
    }

}