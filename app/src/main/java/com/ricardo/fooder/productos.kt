package com.ricardo.fooder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.objetos.items
import kotlinx.android.synthetic.main.activity_productos.*

class productos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        /////////////
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        /////////////

        listItems()

        btnaddItem.setOnClickListener {

            if(validarVacio(tnombre) && validarVacio(tprecio)) {
                val itm=items()
                itm.nombreItem = tnombre.text.toString().padEnd(15,' ').lowercase()
                itm.descripcionItem = tdescripcion.text.toString().lowercase()
                itm.precioItem = tprecio.text.toString()

                addItem(itm)

                tnombre.setText("")
                tdescripcion.setText("")
                tprecio.setText("")

            }

            listItems()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun listItems(){
        try{

            val objdbhelper = DbHelper(this)
            val itm = items()
            val list1:List<items> = objdbhelper.selectItems()
            val adapter = itemsAdapter(this,list1)

            lvItems.adapter = adapter


            lvItems.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->

                val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Desea inhabilitar item?")
                    .setNegativeButton("No") { view, _ ->

                        view.dismiss()
                    }
                    .setPositiveButton("Si") { view, _ ->

                        /*tnombre.setText(list1[position].nombreItem.toString())
                        tdescripcion.setText(list1[position].descripcionItem.toString())
                        tprecio.setText(list1[position].precioItem.toString())*/

                        itm.idItem = list1[position].idItem.toString()
                        //itm.nombreItem = tnombre.text.toString().padEnd(15,' ').lowercase()
                        //itm.descripcionItem = tdescripcion.text.toString().lowercase()
                        //itm.precioItem = tprecio.text.toString()
                        itm.enableItem = "0"

                        objdbhelper.updateItem(itm)
                        listItems()


                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()

                dialog.show()

               true
            }


            }catch (e:Exception){
            Toast.makeText(this,"Error listItems",Toast.LENGTH_LONG).show()
        }


    }


    private fun addItem(itm:items){
        try{
            val objdbhelper = DbHelper(this)
            objdbhelper.insertItem(itm)
            Toast.makeText(this,"nuevo item creado!",Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(this,"Error addItem",Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarVacio(txt: EditText):Boolean{

        if (txt.text.toString().trim { it <= ' ' } == "") {
            txt.error = "Campo obligatorio"
            return false
        } else {
            txt.error = null
        }
        return true
    }

}