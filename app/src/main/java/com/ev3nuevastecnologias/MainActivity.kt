package com.ev3nuevastecnologias

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var firebase: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    //--------------------------------------------------//
    private lateinit var txt_nombre: EditText
    private lateinit var txt_correo: EditText
    private lateinit var txt_clave: EditText
    private lateinit var txt_clave2: EditText
    private lateinit var tv_Error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txt_nombre = findViewById(R.id.txt_nombre_main)
        txt_correo = findViewById(R.id.txt_correo_main)
        txt_clave = findViewById(R.id.txt_clave_main)
        txt_clave2 = findViewById(R.id.txt_confClave_main)
        tv_Error = findViewById(R.id.tv_Error)
        //-------------------------------------------//
        firebase = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }
    fun irLogin(view: View){
        val login = Intent(this, loginGymApp::class.java)
        startActivity(login)
    }
    fun irRecuperar(view: View){
        val recuperar = Intent(this, recuperarClave::class.java)
        startActivity(recuperar)
    }

    fun mostrarAlerta(titulo: String, mensaje: String){
        val alerta = AlertDialog.Builder(this)
        alerta.setTitle(titulo)
        alerta.setMessage(mensaje)
        alerta.setPositiveButton("Ok", null)

        alerta.show()
    }

    fun crearCuenta(view: View){
        val nombre = txt_nombre.text.toString().trim()
        val correo = txt_correo.text.toString().trim()
        val clave = txt_clave.text.toString().trim()
        val clave2 = txt_clave2.text.toString().trim()

        if(nombre.isEmpty() || correo.isEmpty() || clave.isEmpty() || clave2.isEmpty()){
            mostrarAlerta("Error","Campos vacíos.")
            return
        }
        if(clave != clave2){
            tv_Error.visibility = View.VISIBLE
            return
        }else{
            firebase.createUserWithEmailAndPassword(
                correo,clave
            ).addOnSuccessListener { tarea ->
                val UID: String = tarea.user?.uid.toString() //El "?" es la manera segura de acceder
                if(UID == null){
                    mostrarAlerta("Error", "Ha ocurrido un error al obtener el UID")
                }
                val estructuraPersonas = hashMapOf(
                    "nombre" to nombre,
                    "correo" to correo,
                    "clave" to clave
                )
                db.collection("personas")
                    .document(UID)
                    .set(estructuraPersonas)
                    .addOnSuccessListener {
                        mostrarAlerta("Completo", "¡Correcto!")
                    }
                    .addOnFailureListener { error ->
                        mostrarAlerta("Error", "Ha ocurrido un error${error?.message}")
                    }
            }
                .addOnFailureListener { error ->
                    mostrarAlerta("Error", "Ha ocurrido un error${error?.message}")
                }
        }
    }
}