package com.example.proyecto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class PagoPrestamo : Fragment() {


    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var dineroTextView: EditText
    private lateinit var montoPrestamoView: EditText

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private val prestamosList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pago_prestamo, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas
        dineroTextView = view.findViewById(R.id.dinero)
        montoPrestamoView = view.findViewById(R.id.prestamo)

        autoCompleteTextView = view.findViewById(R.id.auto)


        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val prestamosRef = db.collection("Users").document(userId).collection("Prestamos")

        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                //Get a los valores de la coleccion
                val dinero = documentSnapshot.getDouble("Dinero")

                prestamosRef.get().addOnSuccessListener { result ->
                    for (document in result) {
                        val prestamo = document.getString("TipoCredito") // assuming that the name of the prestamo is stored in a field called "nombre"
                        if (prestamo != null) {
                            prestamosList.add(prestamo)
                        }
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, prestamosList)
                    autoCompleteTextView.setAdapter(adapter)
                }

                //val prestamo = documentSnapshot.getDouble("Dinero")
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val dineroFormatted = formatter.format(dinero)
               // val prestamoFormatted = formatter.format(prestamo)

                dineroTextView.setText(dineroFormatted)
            }
        }


        return view
    }

}