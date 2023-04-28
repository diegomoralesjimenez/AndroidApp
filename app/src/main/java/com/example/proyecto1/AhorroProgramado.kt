package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class AhorroProgramado : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var salarioView: EditText
    private lateinit var montoAhorroView: EditText
    private lateinit var meses: EditText

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var btnAccept: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ahorro_programado, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        salarioView = view.findViewById(R.id.salario)
        montoAhorroView = view.findViewById(R.id.montoAhorro)
        meses = view.findViewById(R.id.meses)

        button1 = view.findViewById(R.id.buttonNav)
        button2 = view.findViewById(R.id.buttonEsc)
        button3 = view.findViewById(R.id.buttonMarc)
        button4 = view.findViewById(R.id.buttonExtr)

        btnAccept = view.findViewById(R.id.aceptar)

        var tipo = ""
        fun tipoPrestamo(tipoPrestamo: String, button: Button) {
            tipo = tipoPrestamo;
        }
        button1.setOnClickListener { tipoPrestamo("Navideño", it as Button) }
        button2.setOnClickListener { tipoPrestamo("Escolar", it as Button) }
        button3.setOnClickListener { tipoPrestamo("Marchamo", it as Button) }
        button4.setOnClickListener { tipoPrestamo("Extraordinario", it as Button) }

        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                //Get a los valores de la coleccion
                val salario = documentSnapshot.getDouble("Salario")
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val salarioFormatted = formatter.format(salario)
                salarioView.setText(salarioFormatted)
            }
        }

        btnAccept.setOnClickListener {
            if(validarAtributos()){
                val ahorroRef = db.collection("Users").document(userId).collection("Ahorro")
                val tipoAhorro = tipo
                val query = ahorroRef.whereEqualTo("TipoAhorro", tipoAhorro)
                query.get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.size == 0) {
                        // No document with this TipoAhorro value exists, so it's safe to add a new one
                        val montoAhorroFieldName = "Monto" + tipoAhorro
                        val newAhorroDoc = ahorroRef.document(tipoAhorro)
                        val data = hashMapOf(
                            montoAhorroFieldName to montoAhorroView.text.toString().toDouble(),
                            "Meses" to meses.text.toString().toInt(),
                            "TipoAhorro" to tipoAhorro
                        )
                        newAhorroDoc.set(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Se ha agregado su ahorro", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                // Handle failed insertion
                            }
                    } else {
                        // A document with this TipoAhorro value already exists, so don't add a new one
                        Toast.makeText(context, "Ya existe un ahorro de tipo $tipoAhorro", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(context, "Por favor complete todos los espacios.40249", Toast.LENGTH_SHORT).show()
            }
        }



        return view
    }

    private fun validarAtributos(): Boolean {
        return !salarioView.text.isEmpty() &&
                !montoAhorroView.text.isEmpty() &&
                !meses.text.isEmpty()
    }

}