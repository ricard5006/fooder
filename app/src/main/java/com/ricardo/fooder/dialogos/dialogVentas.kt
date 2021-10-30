package com.ricardo.fooder.dialogos

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.tscdll.TSCActivity
import com.ricardo.fooder.R
import com.ricardo.fooder.dbHelper.DbHelper
import com.ricardo.fooder.itemsAdapter
import com.ricardo.fooder.objetos.items
import com.ricardo.fooder.objetos.parametros
import com.ricardo.fooder.objetos.ventas
import kotlinx.android.synthetic.main.activity_dialog_ventas.*
import java.io.File
import java.io.FileOutputStream


class dialogVentas : AppCompatActivity() {

    var prnt = TSCActivity()

    var vta_ = ventas()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_dialog_ventas)

        crearCarpeta()
        /////////////
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        /////////////


        vta_.idvtas = intent.getStringExtra("idVenta")!!.toIntOrNull()
        TbTitle.setText("Factura: ${vta_.idvtas.toString().padStart(5,'0')}")
        listarItems(vta_)


        TbTitle.setOnClickListener { finish() }

    }


    fun listarItems(Vta:ventas){
        try{

            val objHelper = DbHelper(this)
            val list1:List<items> = objHelper.selectItemsByIdVta(Vta.idvtas.toString())
            val adapter = itemsAdapter(this,list1)
            var precio=0

            lvProd.adapter = adapter

            for(i in list1.indices) {
                precio += list1[i].precioItem!!.toIntOrNull()!!
            }

            TbtotalVta.setText("Total: $$precio")

        }catch (e:Exception){
            Toast.makeText(this,"Error listarItems: ${e.message}",Toast.LENGTH_SHORT).show()}

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_historial_ventas,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id==R.id.print){

            if(impresoraOrPdf()=="true"){
                imprimirPDF(vta_)
            }else{
                imprimirBT(vta_)
            }


            return true
        }



        return super.onOptionsItemSelected(item)
    }

    private fun imprimirPDF(Vta:ventas) {
        try{

            val objHelper = DbHelper(this)
            val list1:List<items> = objHelper.selectItemsByIdVta(Vta.idvtas.toString())
            val objVentas = objHelper.selectVentaByIdVta(Vta.idvtas.toString())

            var cabecera=cabeceraTicket().split('/')

            var pageHigh = 180
            list1.forEach {
                pageHigh += 15
            }



            val pd = PdfDocument()
            val pi = PdfDocument.PageInfo.Builder(210,pageHigh,1).create()
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
            canvas.drawText("Dir: ${objVentas.direccion}", 10F, 100F,paint)

            canvas.drawText("************************************", 10F, 115F,paint)


            canvas.drawText("Item", 10F, 130F,paint)
            canvas.drawText("Cant.", 110F, 130F,paint)
            canvas.drawText("Vlr.", 150F, 130F,paint)

            var renglon = 150F
            var cant = 0

            for(i in list1.indices){

                canvas.drawText("${list1[i].nombreItem?.substring(0,15)}", 10F, renglon,paint)
                canvas.drawText("1", 110F, renglon,paint)
                canvas.drawText("${list1[i].precioItem}", 150F, renglon,paint)

                renglon+=15F
                cant+=1

            }


            canvas.drawText("${cant}", 110F, renglon,paint)
            canvas.drawText("$${objVentas.totalVta}", 150F, renglon,paint)

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
                        "T 0 2 304 $renglon ${list1[i].precioItem}\n" +
                        "T 0 2 161 $renglon 1\n"

                    renglon+=25
                    cant+=1

                }
                ticket +="T 0 2 162 $renglon $cant\n" +
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

    private fun cabeceraTicket():String{

        val objDb = DbHelper(this)
        val objParametros = parametros()

        var ticket:String?="ticket"

        objParametros.parametro = ticket
        ticket = objDb.selectParametro(objParametros)


        return ticket

    }

    private fun impresoraOrPdf():String{
        //imprime en PDF si es "true" o en dispositivo Bluetooth si es "false"

        val objDb = DbHelper(this)
        val objParametros = parametros()

        var Gp:String?="PDF"

        objParametros.parametro = Gp
        Gp = objDb.selectParametro(objParametros)


        return Gp
    }

    private fun crearCarpeta(){



        val f = File(Environment.getExternalStorageDirectory().absolutePath+"/fooder")

        if(f.exists()){

        }else{
            f.mkdirs()
        }
    }

}