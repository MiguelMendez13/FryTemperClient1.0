package com.example.frytemper

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_agregar_tarea.*

import kotlinx.android.synthetic.main.activity_listas_fry_temper.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class ListasFryTemper : AppCompatActivity() {
    val ls = mutableListOf<Tarea>()
    val lstr = mutableListOf<String>()
    var selec=10000
    var address= ""
    var port=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listas_fry_temper)

        val objeto: Intent=intent
        val bundle = intent.extras
        val lsTareas = bundle?.getString("lsTareas").toString().split(":").toMutableList()
        val lsEjec = bundle?.getString("lsEjec").toString().split(":").toMutableList()

        address=bundle?.getString("ip").toString()
        port=bundle?.getString("port").toString().toInt()

        lsTareas.removeAt(lsTareas.size-1)
        lsEjec.removeAt(lsEjec.size-1)
        val lvTareas = findViewById<ListView>(R.id.lvTareas)

        var TareasItem: MutableList<Tarea> = mutableListOf()

        if(lsTareas[0]!="***"){
            for (it in lsTareas){
                val lel = it.split("-").toMutableList()
                ls.add(Tarea(lel[0],lel[1].toInt(),lel[2].toInt(),lel[3].toInt()))
            }


        }
        else{
            ls.add(Tarea("Nada",0,0,0))
        }
        val Adapter=TareaAdapter(this,ls)
        lvTareas.setAdapter(Adapter)


        val actualizador = lifecycleScope.launch {
            val address= "192.168.0.3"
            val port=10000
            withContext(Dispatchers.IO){client()}
        }
        println("#-----------------------------")
        println(actualizador)
        println("#-----------------------------")
        agregarBTN.setOnClickListener {
            //actualizador.cancel()
            val intent:Intent = Intent(this, AgregarTarea::class.java)
            intent.putExtra("ip", address)
            intent.putExtra("port", port.toString())
            startActivity(intent)

        }
        BtnBorrar.setOnClickListener{
            if(selec==10000){
                Toast.makeText(this,"Selecciona una tarea para borrar",Toast.LENGTH_SHORT).show()
            }
            else{
                println(selec)
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Borrar")
                builder.setMessage("Estas seguro de borrar la tarea "+lstr[selec])
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Si") { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {

                        val connection = Socket(address, port)
                        val writer: OutputStream = connection.getOutputStream()
                        writer.write(("Delete-- "+selec).toByteArray(Charset.defaultCharset()))
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

                builder.setNegativeButton("No") { dialog, which ->
                    Toast.makeText(applicationContext,
                        "No", Toast.LENGTH_SHORT).show()
                }

                /*builder.setNeutralButton("Maybe") { dialog, which ->
                    Toast.makeText(applicationContext,
                        "Maybe", Toast.LENGTH_SHORT).show()
                }*/
                builder.show()
            }
        }

        lvTareas.setOnItemClickListener {parent,view,position,id->
            println(lstr)
            if(lstr.size!=0) {
                TextoSelec.text = lstr[position]
                selec=position
            }
        }

    }

    fun toatt(texto:String){
        val toast= Toast.makeText(this,texto, Toast.LENGTH_SHORT).show()
    }

    private suspend fun client(){
        while (true) {
            try {
                lstr.clear()
                val connection = Socket(address, port)
                val writer: OutputStream = connection.getOutputStream()
                writer.write(("TList--").toByteArray(Charset.defaultCharset()))
                val respuesta = Scanner(connection.getInputStream())
                val reader = respuesta.nextLine().toString()
                respuesta.close()
                writer.close()
                connection.close()

                val lsTareas = reader.split(":").toMutableList()
                lsTareas.removeAt(lsTareas.size - 1)

                var TareasItem: MutableList<Tarea> = mutableListOf()
                if (lsTareas[0] != "***") {
                    for (it in lsTareas) {
                        var lst = it.split("-").toMutableList()
                        TareasItem.add(Tarea(lst[0], lst[1].toInt(), lst[2].toInt(), lst[3].toInt()))
                        lstr.add(lst[0])
                    }

                } else {
                    TareasItem.add(Tarea("Nada", 0, 0, 0))
                }

                runOnUiThread {
                    val Adapter = TareaAdapter(this, TareasItem)
                    lvTareas.setAdapter(Adapter)
                }


                //println("#-----------------------------")
                //println(lsTareas)
            }
            catch(e: Exception){
                println("hubo un error")
            }
        delay(1000)
        }
    }
}


