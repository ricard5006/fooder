package com.ricardo.fooder.dialogos

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ricardo.fooder.MainActivity
import com.ricardo.fooder.R
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.itemsAdapter
import com.ricardo.fooder.objetos.items
import kotlinx.android.synthetic.main.activity_dialog_producto.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*

class dialogProducto : AppCompatActivity() {

    var itm=items()
    var listItems= ArrayList<items> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_dialog_producto)

        listItems = intent.getSerializableExtra("items") as ArrayList<items>


        listItems()

        tSearchProd.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                buscarItems(s.toString())
            }
        })

        TbTitle.setOnClickListener {
            retornaItems()
        }

    }


    private fun listItems(){
        try{
            val objHelper = DbHelper(this)
            val list1:List<items> = objHelper.selectItems()
            val adapter = itemsAdapter(this,list1)

            lvaddProd.adapter = adapter



            lvaddProd.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                try{
                    itm = list1[position]

                    var opciones=""

                    val dialogV = layoutInflater.inflate(R.layout.custom_dialog,null)
                    val customDialog = AlertDialog.Builder(this)
                    customDialog.setView(dialogV)
                    val mAlertD = customDialog.show()


//                    val BtnCancel = dialogV.findViewById<Button>(R.id.BtnDialogCancel)
//                    val BtnAdd = dialogV.findViewById<Button>(R.id.BtnDialogAdd)

                    dialogV.BtnDialogCancel.setOnClickListener { mAlertD.dismiss() }
                    dialogV.BtnDialogAdd.setOnClickListener {

                        opciones = dialogV.TbOpcioneDialog.text.toString().padEnd(30,' ').lowercase()
                        itm.descripcionItem = "*"+opciones
                        listItems.add(itm)
                        Toast.makeText(this,"Producto añadido!!!", Toast.LENGTH_SHORT).show()
                        mAlertD.dismiss()
                    }



                }catch (e:Exception){
                    Toast.makeText(this,"Producto no añadido error: "+e.message, Toast.LENGTH_LONG).show()
                }



            }


        }catch (e:Exception){
            val algo=e.message
        }


    }

    private fun buscarItems(nombreItem:String){

        try{
            val itm= items()
            itm.nombreItem="%$nombreItem%"

            val objdbhelper = DbHelper(this)
            val list1:List<items> = objdbhelper.selectItemsBy(itm)
            val adapter = itemsAdapter(this,list1)

            lvaddProd.adapter = adapter

        }catch (e:Exception){
            val algo=e.message
        }

    }

    private fun retornaItems(){


        val i = Intent(this,MainActivity::class.java)
        i.putExtra("items",listItems)
        setResult(RESULT_OK,i)
        finish()

    }

    override fun onBackPressed() {


        retornaItems()
        super.onBackPressed()

    }

}