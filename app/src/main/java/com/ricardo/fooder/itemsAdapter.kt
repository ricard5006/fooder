package com.ricardo.fooder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ricardo.fooder.objetos.items
import kotlinx.android.synthetic.main.activity_items_adapter.view.*



class itemsAdapter(private val mContext: Context, private val lista:List<items>):
    ArrayAdapter<items>(mContext,0,lista)  {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.activity_items_adapter,parent,false)

        val item = lista[position]


        layout.tProducto.setText(item.nombreItem)
        layout.tDescripcion.setText(item.descripcionItem)
        layout.tprecio.setText(item.precioItem)



        return layout
    }

}