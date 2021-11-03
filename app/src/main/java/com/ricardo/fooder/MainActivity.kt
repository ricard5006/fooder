package com.ricardo.fooder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tscdll.TSCActivity
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.dialogos.dialogProducto
import com.ricardo.fooder.objetos.items
import com.ricardo.fooder.objetos.parametros
import com.ricardo.fooder.objetos.ventas
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var listItems= ArrayList<items> ()

    private val CodigoBotonLinea = 5

    var prnt = TSCActivity()

    val vta_ = ventas()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            checkExternalStoragePermission()
        }

        crearCarpeta()

        tfecha.setText(fechaNow())

        tAddProd.setOnClickListener {
            mostrarModal()
        }

        btnaceptar.setOnClickListener{
            if(validarVacio(tcliente) && validarVacio(ttelefono) && validarVacio(tdireccion)){


                vta_.idvtas = lastVenta()
                vta_.cliente = tcliente.text.toString()
                vta_.telefono = ttelefono.text.toString()
                vta_.direccion = tdireccion.text.toString().padEnd(100,' ')
                vta_.fechaVta = fechaNow()

                for(i in listItems.indices){
                    //*vta_.idItem = listItems[i].idItem.toString()
                    vta_.idItem = listItems[i].idItem.toString()
                    vta_.observacion = listItems[i].descripcionItem.toString()
                    newVenta(vta_)
                }
                if(facturarImprimir()=="true"){

                    if(impresoraOrPdf()=="true"){
                        imprimirPDF(vta_)
                    }else{
                        imprimirBT(vta_)
                    }

                }

                Toast.makeText(this,"Realizado!",Toast.LENGTH_LONG).show()

                tcliente.setText("")
                ttelefono.setText("")
                tdireccion.setText("")
                listItems.clear()
                listProductos()
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id==R.id.print){

            //imprimir(vta_)
            //imprimirPDF(vta_)


            return true
        }

        if(id==R.id.report){
            val intent = Intent(this,reporte::class.java)
            startActivity(intent)
            return true
        }

        if(id==R.id.newProducts){
            val intent = Intent(this,productos::class.java)
            startActivity(intent)
            return true
        }

        if(id==R.id.config){
            val intent = Intent(this,configuracion::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun fechaNow():String{
        var fecha=""
        try{

            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(Date())

            fecha =currentDate.toString()

        }catch (e:Exception){
            Toast.makeText(this,"Error fechaNow: "+e.message,Toast.LENGTH_SHORT).show()
        }


        return  fecha
    }

    fun newVenta(vta:ventas){
        try{

            val objDb = DbHelper(this)
            objDb.updateVentas(vta)


        }catch (e:Exception){
            Toast.makeText(this,"Error newVentas",Toast.LENGTH_LONG).show()
        }

    }

    fun lastVenta():Int{

            var idLastVenta=1
            val objdbHelper= DbHelper(this)
            var objVts = objdbHelper.lastVenta()
        try{
            if(objVts.idvtas!!.toInt() != null){
                idLastVenta = objVts.idvtas!!.toInt()
                idLastVenta+=1

            }

        }catch (e:Exception){
            Toast.makeText(this,"Error lastVenta: "+e.message,Toast.LENGTH_LONG).show()}

        return idLastVenta
    }

    fun listProductos(){

        try {

            var precio=0
            val adapter = itemsAdapter(this,listItems)
            lvproductos.adapter = adapter



            for(i in listItems.indices) {
               precio += listItems[i].precioItem!!.toIntOrNull()!!
            }

            ttotal.setText("Total: $$precio")

            lvproductos.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->

                listItems.removeAt(position)

                listProductos()
                true
            }

        }catch (e:Exception){
            Toast.makeText(this,"Error listProductos: "+e.message,Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            if(requestCode == CodigoBotonLinea) {

                listItems = data!!.getSerializableExtra("items") as ArrayList<items>



                listProductos()

            }
            }


    }



    fun mostrarModal(){

        val i = Intent(this,dialogProducto::class.java)
        i.putExtra("items",listItems)
        startActivityForResult(i,CodigoBotonLinea)

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



    fun getMacPrinter():String{

        val objDb = DbHelper(this)
        val objParametros = parametros()

        var impresora="macPrinter"

        try{


            objParametros.parametro = impresora


            impresora = objDb.selectParametro(objParametros)



        }catch (e:Exception){
            Toast.makeText(this,"",Toast.LENGTH_LONG).show()
        }
        return impresora

    }


    fun facturarImprimir():String{
    //habilita la opcion para guardar e imprimir o solo guardar

        val objDb = DbHelper(this)
        val objParametros = parametros()

        var Gp:String?="facturarImprimir"

        objParametros.parametro = Gp
        Gp = objDb.selectParametro(objParametros)


        return Gp
    }

    fun impresoraOrPdf():String{
    //imprime en PDF si es "true" o en dispositivo Bluetooth si es "false"

    val objDb = DbHelper(this)
    val objParametros = parametros()

    var Gp:String?="PDF"

    objParametros.parametro = Gp
    Gp = objDb.selectParametro(objParametros)


    return Gp
}

    fun cabeceraTicket():String{

        val objDb = DbHelper(this)
        val objParametros = parametros()

        var ticket:String?="ticket"

        objParametros.parametro = ticket
        ticket = objDb.selectParametro(objParametros)


        return ticket

    }


    private fun imprimirPDF(Vta:ventas) {
        try{

            val objHelper = DbHelper(this)
            val list1:List<items> = objHelper.selectItemsByIdVta(Vta.idvtas.toString())
            val objVentas = objHelper.selectVentaByIdVta(Vta.idvtas.toString())

            var cabecera=cabeceraTicket().split('/')

            var pageHigh = 180
            list1.forEach {
                pageHigh += 22
            }



            val pd = PdfDocument()
            val pi = PdfDocument.PageInfo.Builder(300,pageHigh,1).create()
            val page = pd.startPage(pi)

            val canvas = page.canvas
            val paint = Paint()
            paint.color= Color.BLACK
            paint.textSize= 12F



            canvas.drawText("Ticket No. ${Vta.idvtas.toString().padStart(5,'0')}", 10F, 10F,paint)
            canvas.drawText("${cabecera[0]}", 10F, 25F,paint)
            canvas.drawText("${cabecera[1]}", 10F, 40F,paint)
            canvas.drawText("${cabecera[2]}", 10F, 55F,paint)

            canvas.drawText("Cliente: ${objVentas.cliente}", 10F, 70F,paint)
            canvas.drawText("Tel: ${objVentas.telefono}", 10F, 85F,paint)
            canvas.drawText("Dir: ${objVentas.direccion?.substring(0,50)}", 10F, 100F,paint)
            canvas.drawText("- ${objVentas.direccion?.substring(50,100)}", 10F, 115F,paint)
            canvas.drawText("******************************************************", 10F, 128F,paint)


            canvas.drawText("Item", 10F, 135F,paint)
            canvas.drawText("Cant.", 190F, 135F,paint)
            canvas.drawText("Vlr.", 230F, 135F,paint)

            var renglon = 155F
            var cant = 0

            for(i in list1.indices){

                canvas.drawText("${list1[i].nombreItem?.substring(0,15)}", 10F, renglon,paint)
                canvas.drawText("${list1[i].descripcionItem?.substring(0,30)}", 15F, renglon+12F,paint)

                canvas.drawText("1", 190F, renglon,paint)
                canvas.drawText("${list1[i].precioItem}", 230F, renglon,paint)

                renglon+=22F
                cant+=1

            }


            canvas.drawText("${cant}", 190F, renglon,paint)
            canvas.drawText("$${objVentas.totalVta}", 230F, renglon,paint)

            pd.finishPage(page)
            val ruta = "/sdcard/fooder"
            val file = File(ruta,"ticket_${Vta.idvtas.toString().padStart(5,'0')}.pdf")
            pd.writeTo(FileOutputStream(file))
            pd.close()
            Toast.makeText(this,"Listo!",Toast.LENGTH_SHORT).show()

        }catch (e:Exception){
            Toast.makeText(this,"Error:"+e.message,Toast.LENGTH_LONG).show()
        }




    }

    private fun imprimirBT(Vta:ventas){

        val objHelper = DbHelper(this)
        val list1:List<items> = objHelper.selectItemsByIdVta(Vta.idvtas.toString())
        val objVentas = objHelper.selectVentaByIdVta(Vta.idvtas.toString())

        var cabecera=cabeceraTicket().split('/')

        var pageHigh = 350
        list1.forEach {
            pageHigh += 25
        }


        if(getMacPrinter()!="") {
            try {
                prnt.openport(getMacPrinter())



                var ticket = "! 0 200 200 $pageHigh 1\n" +
                        "PW 400\n" +
                        "TONE 5\n" +
                        "SPEED 5\n" +
                        "SETFF 0 5\n" +
                        "ON-FEED FEED\n" +
                        "NO-PACE\n" +
                        "JOURNAL\n" +
                        "T 0 2 76 21 Ticket: ${Vta.idvtas.toString().padStart(5,'0')}\n" +
                        "T 0 2 76 85 ${cabecera[2]}\n" +
                        "T 0 2 76 64 ${cabecera[1]}\n" +
                        "T 0 2 76 42 ${cabecera[0]}\n" +
                        "T 0 2 16 162 Dir: ${objVentas.direccion}\n" +
                        "T 0 2 16 141 Tel: ${objVentas.telefono}\n" +
                        "T 0 2 16 118 Cliente: ${objVentas.cliente}\n" +
                        "T 0 2 24 189 *************************************\n" +
                        "T 0 2 304 216 Vlr.\n" +
                        "T 0 2 162 216 Cant.\n" +
                        "T 0 2 17 216 Item\n"

                var renglon = 244
                var cant = 0

                for(i in list1.indices){

                    ticket +="T 0 2 16 $renglon ${list1[i].nombreItem?.substring(0,15)}\n" +
                            "T 0 2 18 ${renglon+10} ${list1[i].descripcionItem?.substring(0,30)}\n"+
                            "T 0 2 180 $renglon 1\n"+
                            "T 0 2 304 $renglon ${list1[i].precioItem}\n"


                    renglon+=30
                    cant+=1

                }
                ticket +="T 0 2 180 $renglon $cant\n" +
                        "T 0 2 304 $renglon $${objVentas.totalVta}\n" +
                        "PRINT\n"

                prnt.sendcommand(ticket)
                Toast.makeText(this,"Imprimiendo",Toast.LENGTH_SHORT).show()
                //prnt.clearbuffer()
                //prnt.printlabel(1, 1)
                prnt.closeport(1000)

            }catch (e:Exception){Toast.makeText(this,"lo siento no se pudo imprimir.",Toast.LENGTH_SHORT).show()}
        }else{
            Toast.makeText(this,"lo siento, configure una impresora compatible CPCL",Toast.LENGTH_LONG).show()
        }

    }

    private fun crearCarpeta(){

        val f = File(Environment.getExternalStorageDirectory().absolutePath+"/fooder")

        if(f.exists()){

        }else{
            f.mkdirs()
        }
    }

    private fun checkExternalStoragePermission() {
        val permissionCheck: Int = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!")
        }
    }

}