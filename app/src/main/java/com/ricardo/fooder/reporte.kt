package com.ricardo.fooder

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import com.ricardo.fooder.dbHelper.DbHelper

import com.ricardo.fooder.dialogos.dialogVentas

import com.ricardo.fooder.objetos.ventas
import kotlinx.android.synthetic.main.activity_reporte.*
import java.text.SimpleDateFormat
import java.util.*

class reporte : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)

        /////////////
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        /////////////
        val objVta=ventas()
        objVta.fechaVta = fechaNow()
        tfecha.setText(fechaNow())
        listarVentas(objVta)
        ///
        /**calendario
         * muestra un modal/ DataPicker para obtener las facturas del dia
         */
        try{

            val cal = Calendar.getInstance()
            val vyear = cal.get(Calendar.YEAR)
            val vmonth = cal.get(Calendar.MONTH)
            val vday = cal.get(Calendar.DAY_OF_MONTH)
            tfecha.setOnClickListener {
                var f=""
                val dpd = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        var mes = month+1
                        f = "${dayOfMonth.toString().padStart(2,'0')}/${mes.toString().padStart(2,'0')}/$year"
                        objVta.fechaVta = f
                        tfecha.setText(f)
                        listarVentas(objVta)

                    },vyear,vmonth,vday
                )
                dpd.show()
            }

        }catch (e:Exception){
            Toast.makeText(this,"Error de calendario: ${e.message}",Toast.LENGTH_SHORT).show()
        }
        ///
    }



    fun listarVentas(objVta:ventas){

        try{
            var totalVta=0

            val objHelper = DbHelper(this)
            val list1:List<ventas> = objHelper.selectVentasbyFecha(objVta)
            val adapter = ventasAdapter(this,list1)

            lvVentas.adapter = adapter

            for(i in list1.indices){
                totalVta += list1[i].totalVta!!.toIntOrNull()!!
            }
            ttotalvta.setText("Total: $$totalVta")

            lvVentas.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

                val idVta:ventas = list1[position]
                val i = Intent(this, dialogVentas::class.java)
                i.putExtra("idVenta",idVta.idvtas.toString())
                startActivity(i)

            }

        }catch (e:Exception){
            Toast.makeText(this,"Error listarVenta: ${e.message}",Toast.LENGTH_SHORT).show()
        }



    }

    fun fechaNow():String{
        var fecha=""
        try{

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(Date())

            fecha =currentDate.toString()

        }catch (e:Exception){
            Toast.makeText(this,"Error fechaNow: "+e.message, Toast.LENGTH_SHORT).show()
        }


        return  fecha
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_in_reporte,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(item.itemId == android.R.id.home){
            finish()
        }

        if (id == R.id.reportByItem) {
        val i= Intent(this,reporteByItems::class.java)
            startActivity(i)
        }
            return true

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}