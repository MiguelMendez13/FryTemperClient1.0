package com.example.frytemper

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.OutputStream

import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import java.nio.charset.Charset
import java.util.*


class MainActivity : AppCompatActivity() {
    private var active: Boolean = false
    private var data: String = ""
    private var datos: String = ""
    var address= ""
    var port=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val address= "192.168.0.3"
        //val port=10000

        btnEntrar.setOnClickListener {
            address=ip.text.toString()
            port = puerto.text.toString().toInt()
        val result =try {

            CoroutineScope(Dispatchers.IO).launch {
                client()
            }

        }
        catch (e:Exception){
            print("#***************************************\n\n")
        }

        }
    }


    private suspend fun client(){
        println("lol")

        val connection = Socket(address, port)
        val writer: OutputStream = connection.getOutputStream()
        writer.write(("TList--").toByteArray(Charset.defaultCharset()))
        val respuesta=Scanner(connection.getInputStream())
        val reader = respuesta.nextLine().toString()
        respuesta.close()
        writer.close()
        connection.close()

        val connection2 = Socket(address, port)
        val writer2: OutputStream = connection2.getOutputStream()
        writer2.write(("ListEjec--").toByteArray(Charset.defaultCharset()))
        val respuesta2=Scanner(connection2.getInputStream())
        val reader2 = respuesta2.nextLine().toString()
        respuesta2.close()
        writer2.close()
        connection2.close()
        data+=reader
        println(reader)
        datos=reader


        val intent:Intent = Intent(this, ListasFryTemper::class.java)
        intent.putExtra("lsTareas", reader)
        intent.putExtra("lsEjec", reader2)
        intent.putExtra("ip", address)
        intent.putExtra("port", port.toString())
        startActivity(intent)

    }

}
