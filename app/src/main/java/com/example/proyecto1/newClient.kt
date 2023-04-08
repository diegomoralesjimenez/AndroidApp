package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class newClient : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var fechaTextView: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_client, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas
        nombreTextView = view.findViewById(R.id.nombre)
        direccionTextView = view.findViewById(R.id.direccion)
        fechaTextView = view.findViewById(R.id.fechaNacimiento)

        val addButton = view.findViewById<Button>(R.id.add)
        addButton.setOnClickListener {
            //Datos que se actualizan
            val newNombre = nombreTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()

            //Coleccion
            val userRef = db.collection("Users").document()

            //Creacion de los valores de la coleccion
            val user = hashMapOf(
                "Nombre" to newNombre,
                "Direccion" to newDireccion,
                "FechaNacimiento" to newFechaNacimiento
            )

            userRef.set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "User added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
                }
        }



        return view
    }

    // Metodo insertar nuevo cliente



}