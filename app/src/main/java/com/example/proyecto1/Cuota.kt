package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Cuota : Fragment() {

    private lateinit var salarioView: EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cuota, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas
        salarioView = view.findViewById(R.id.salario)

        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                //Get a los valores de la coleccion
                val salario = documentSnapshot.getDouble("Salario")
                salarioView.setText(salario.toString())

            }
        }

        return view
    }


}