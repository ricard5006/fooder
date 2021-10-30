package com.ricardo.fooder

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.objetos.parametros
import kotlinx.android.synthetic.main.activity_configuracion.*
import java.lang.Exception
import com.example.tscdll.TSCActivity

class configuracion : AppCompatActivity() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothDevice : Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH=1


    var prnt = TSCActivity()


    companion object{

        val EXTRA_ADDRESS:String ="Device_address"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        /////////////
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        /////////////
        activaBt()
        pairedDeviceList()
        leerParametros()

        swPrint.setOnClickListener {
            if(swPrint.isChecked){
                swPrintPDF.isEnabled = true
            }else{
                swPrintPDF.isChecked = false
                swPrintPDF.isEnabled = false
            }
        }

        btnGuardar.setOnClickListener {
            guardarParametros()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun guardarParametros(){
        try{

            val objDb = DbHelper(this)
            val objParametros = parametros()


            objParametros.parametro ="ticket"
            objParametros.valor = TbCabecera.text.toString()
            objDb.updateParametro(objParametros)

            objParametros.parametro ="facturarImprimir"
            if(swPrint.isChecked){
                objParametros.valor = "true"
            }else{
                objParametros.valor = "false"
            }
            objDb.updateParametro(objParametros)

            objParametros.parametro ="PDF"
            if(swPrintPDF.isChecked){
                objParametros.valor = "true"
            }else{
                objParametros.valor = "false"
            }
            objDb.updateParametro(objParametros)

            objParametros.parametro ="macPrinter"
            objParametros.valor = TbMac.text.toString()
            objDb.updateParametro(objParametros)


            Toast.makeText(this,"Guardado!",Toast.LENGTH_SHORT).show()

        }catch (e:Exception){
            Toast.makeText(this,"Error guardarParametros: "+e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun leerParametros(){

        try{
            val objDb = DbHelper(this)
            val objParametro = parametros()
            var valor=""

            objParametro.parametro="ticket"
            valor = objDb.selectParametro(objParametro)
            TbCabecera.setText(valor)

            //lo factura (guarda) salamente o tambien lo imprime
            objParametro.parametro="facturarImprimir"
            valor = objDb.selectParametro(objParametro)
            if(valor=="true"){
                swPrint.isChecked=true
                swPrintPDF.isEnabled = true
            }

            //lo imprime en dispositivo o PDF
            objParametro.parametro ="PDF"
            valor = objDb.selectParametro(objParametro)
            if(valor=="true"){swPrintPDF.isChecked=true}

            objParametro.parametro="macPrinter"
            valor = objDb.selectParametro(objParametro)
            TbMac.setText(valor)




        }catch (e:Exception){
            Toast.makeText(this,"Error leerParametros: "+e.message,Toast.LENGTH_LONG).show()
        }
    }

    private fun activaBt(){

        //Activa BT
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"este dispositivo no es compatible", Toast.LENGTH_SHORT).show()
            return
        }
        if (!bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){

            if(requestCode == Activity.RESULT_OK){

                if(bluetoothAdapter!!.isEnabled){

                }else {
                    Toast.makeText(this,"Bluetooth deshabilitado",Toast.LENGTH_SHORT).show()
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this,"Bluetooth cancelado",Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun pairedDeviceList(){

        bluetoothDevice = bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        val list1:ArrayList<String> = ArrayList()

        if (!bluetoothDevice.isEmpty()) {
            for (device: BluetoothDevice in bluetoothDevice) {
                list1.add(device.name)
                list.add(device)
                Log.i("dispositivos", ""+device.name)
            }
        }else {
            Toast.makeText(this,"No se encontro dispositivos",Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list1)
        select_device_list.adapter = adapter


        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address.toString()
            val name :String=device.name.toString()

            Toast.makeText(this,"Dispositivo seleccionado: $name - $address",Toast.LENGTH_LONG).show()
            TbMac.setText(address)

        }


    }

}