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

class Personal : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var fechaTextView: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout del fragment
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas
        nombreTextView = view.findViewById(R.id.nombre)
        direccionTextView = view.findViewById(R.id.direccion)
        fechaTextView = view.findViewById(R.id.fechaNacimiento)

        // Agarra la informaacion de la collecion de los usuarios de la base de datos
        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val nombre = documentSnapshot.getString("Nombre")
                val direccion = documentSnapshot.getString("Direccion")
                val fechaNacimiento = documentSnapshot.getString("FechaNacimiento")
                nombreTextView.text = nombre
                direccionTextView.text = direccion
                fechaTextView.text = fechaNacimiento
            }
        }

        //Actualiza los Usuarios en la base de datos
        val updateButton = view.findViewById<Button>(R.id.update)
        updateButton.setOnClickListener {
            val newNombre = nombreTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()

            val userRef = db.collection("Users").document(userId)
            userRef.update("Nombre", newNombre, "Direccion", newDireccion, "FechaNacimiento", newFechaNacimiento)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
        return view
    }
}
