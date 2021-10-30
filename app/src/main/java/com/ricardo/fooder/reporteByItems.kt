package com.ricardo.fooder

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.dialogos.dialogVentas
import com.ricardo.fooder.objetos.items
import com.ricardo.fooder.objetos.ventas

import kotlinx.android.synthetic.main.activity_reporte_by_items.*


import java.text.SimpleDateFormat
import java.util.*

class reporteByItems : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_by_items)
        ///
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        ///
        val objVta= ventas()
        objVta.fechaVta = fechaNow()
        tfechaByItem.setText(fechaNow())
        listarVentasByItems(objVta)
        ///
        /**calendario
         * muestra un modal/ DataPicker para obtener las facturas del dia selecionado
         */
        try{

            val cal = Calendar.getInstance()
            val vyear = cal.get(Calendar.YEAR)
            val vmonth = cal.get(Calendar.MONTH)
            val vday = cal.get(Calendar.DAY_OF_MONTH)
            tfechaByItem.setOnClickListener {
                var f=""
                val dpd = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        var mes = month+1
                        f = "${dayOfMonth.toString().padStart(2,'0')}/${mes.toString().padStart(2,'0')}/$year"
                        objVta.fechaVta = f
                        tfechaByItem.setText(f)
                        listarVentasByItems(objVta)

                    },vyear,vmonth,vday
                )
                dpd.show()
            }

        }catch (e:Exception){
            Toast.makeText(this,"Error de calendario: ${e.message}",Toast.LENGTH_SHORT).show()
        }
        ///


        /**
         * muestra los los items vendidos por en el dia selecionado
         */
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun listarVentasByItems(vta:ventas){
        var totalVta=0
        val objDb = DbHelper(this)
        val list1:List<items> = objDb.selectVentasbyItem(vta)

        val adapter = itemsAdapter(this,list1)

        lvVtaProd.adapter = adapter

        for(i in list1.indices){
            totalVta += list1[i].precioItem!!.toIntOrNull()!!
        }
        ttotalProd.setText("Total: $$totalVta")


    }

}