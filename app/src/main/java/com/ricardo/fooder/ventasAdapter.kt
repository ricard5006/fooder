package com.ricardo.fooder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ricardo.fooder.objetos.ventas
import kotlinx.android.synthetic.main.activity_ventas_adapter.view.*

class ventasAdapter (private val mContext: Context, private val lista:List<ventas>):

    ArrayAdapter<ventas>(mContext,0,lista)  {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.activity_ventas_adapter,parent,false)

        val vta = lista[position]


        layout.tIdVta.setText(vta.idvtas.toString().padStart(5,'0'))
        layout.tCliente.setText(vta.cliente)
        layout.tprecio.setText(vta.totalVta)




        return layout
    }

}