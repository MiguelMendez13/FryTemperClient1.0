package com.example.frytemper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_tarea.view.*

class TareaAdapter(private val mContext: Context, private val Nombres: List<Tarea>) : ArrayAdapter<Tarea>(mContext,0, Nombres) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val layout= LayoutInflater.from(mContext).inflate(R.layout.item_tarea, parent, false)
        val tarea= Nombres[position]
        layout.Nombre.text=tarea.Nombre
        layout.Hora.text=tarea.Hora.toString()
        layout.Minu.text=tarea.Minuto.toString()
        layout.Segu.text=tarea.Segundo.toString()

        return (layout)
    }
}