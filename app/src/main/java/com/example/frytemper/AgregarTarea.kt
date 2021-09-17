package com.example.frytemper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.get
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_agregar_tarea.*
import kotlinx.android.synthetic.main.activity_listas_fry_temper.*
import kotlinx.coroutines.*
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class AgregarTarea : AppCompatActivity() {

    var horas=0
    var minutos=0
    var segundos=0
    var Proceso=""
    var address= ""
    var port=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_tarea)
        val opcioneshrs=arrayListOf<String>()
        val opcionesmns=arrayListOf<String>()
        val opcionesseg=arrayListOf<String>()



        val objeto: Intent=intent
        val bundle = intent.extras
        address=bundle?.getString("ip").toString()
        port=bundle?.getString("port").toString().toInt()


        for(x in 0..24){
            opcioneshrs.add(x.toString())
        }
        for(x in 0..60){
            opcionesmns.add(x.toString())
        }
        for(x in 0..60){
            opcionesseg.add(x.toString())
        }

        val hrAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opcioneshrs)
        hrsS.setAdapter(hrAdapter)
        val mnAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opcionesmns)
        mnsS.setAdapter(mnAdapter)
        val seAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opcionesseg)
        segS.setAdapter(seAdapter)

        hrsS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                horas=opcioneshrs.get(position).toInt()
            }
        }
        mnsS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                minutos=opcionesmns.get(position).toInt()
            }
        }
        segS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                segundos=opcionesseg.get(position).toInt()
            }
        }

        val actualizador = lifecycleScope.launch {

            withContext(Dispatchers.IO){tareas()}
        }

        BTNagregar.setOnClickListener {

            println("\n\nProceso: "+Proceso)
            println("\n\nHoras: "+horas.toString())
            println("\n\nMinutos: "+minutos.toString())
            println("\n\nSegundos: "+segundos.toString())
            var leer=""

            if(Proceso=="" || TareaAgre.text=="#---#"){
                toatt("Selecciona una Aplicacion Valida")
            }
            else if(horas==0 && minutos==0 && segundos==0){
                toatt("Selecciona una hora Valida")
            }
            else {
                CoroutineScope(Dispatchers.IO).launch {
                    val connection = Socket(address, port)
                    val writer: OutputStream = connection.getOutputStream()
                    writer.write(
                        ("Add: " + Proceso.split(" ")[0] + " " + horas.toString() + " " + minutos.toString() + " " + segundos.toString()).toByteArray(
                            Charset.defaultCharset()
                        )
                    )
                    val respuesta = Scanner(connection.getInputStream())
                    val reader = respuesta.nextLine().toString()
                    respuesta.close()
                    writer.close()
                    connection.close()

                    runOnUiThread {
                        toatt(reader)
                    }

                }
            }



        }


    }

    fun toatt(texto:String){
        val toast= Toast.makeText(this,texto, Toast.LENGTH_SHORT).show()
    }



    private suspend fun tareas() {
        while (true) {
            try {
                var TareasItem: MutableList<String> = mutableListOf()

                TareasItem.add("AutoCerrar ID- 00")
                val connection = Socket(address, port)
                val writer: OutputStream = connection.getOutputStream()
                writer.write(("ListEjec--").toByteArray(Charset.defaultCharset()))
                val respuesta = Scanner(connection.getInputStream())
                val reader = respuesta.nextLine().toString()
                respuesta.close()
                writer.close()
                connection.close()

                val lsTareas = reader.split(":").toMutableList()
                lsTareas.removeAt(lsTareas.size - 1)


                for(x in lsTareas){
                    TareasItem.add(x)
                }


                runOnUiThread {

                        val Adapterr = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TareasItem)
                        ListaProcesosView.setAdapter(Adapterr)
                        ListaProcesosView.setOnItemClickListener {parent,view,position,id->
                        Proceso=TareasItem.get(position)
                        TareaAgre.text=TareasItem.get(position)
                    }
                }




                println("#-----------------------------")
                //println(lsTareas)
            } catch (e: Exception) {
                println("hubo un error")
            }
            delay(1000*60)
        }
    }


}