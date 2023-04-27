package com.example.proyecto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class PagoPrestamo : Fragment() {


    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var dineroTextView: EditText

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private val prestamosList = ArrayList<String>()

    private lateinit var btnAceptar: Button

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

        autoCompleteTextView = view.findViewById(R.id.auto)

        btnAceptar = view.findViewById(R.id.btnAgregar)

        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val prestamosRef = db.collection("Users").document(userId).collection("Prestamos")

        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                //Get a los valores de la coleccion
                val dinero = documentSnapshot.getDouble("Dinero")

                prestamosRef.get().addOnSuccessListener { result ->
                    for (document in result) {
                        val tipoCredito = document.getString("TipoCredito")
                        val montoMensual = document.getString("MontoMensual")
                        val pagado = document.getBoolean("Pagado")

                        if (tipoCredito != null && montoMensual != null && !pagado!!) {
                            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                            formatter.currency = Currency.getInstance("CRC")
                            val montoMensualFormatted = formatter.format(montoMensual.toDouble())

                            val prestamo = "$tipoCredito: $montoMensualFormatted"
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

        btnAceptar.setOnClickListener {
            val selectedPrestamo = autoCompleteTextView.text.toString()
            var selectedMontoMensual = selectedPrestamo.split(": ")[1].replace(Regex("[^\\d.]"), "").toInt()
            selectedMontoMensual /= 100

            prestamosRef
                .whereEqualTo("MontoMensual", selectedMontoMensual.toString())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val montoPrestamoStr = document.getString("MontoPrestamo")
                        val montoPrestamo = montoPrestamoStr?.toDoubleOrNull() ?: 0.0
                        if (montoPrestamo >= selectedMontoMensual.toDouble()) {
                            val updatedMontoPrestamo = montoPrestamo - selectedMontoMensual.toDouble()
                            document.reference.update("MontoPrestamo", updatedMontoPrestamo.toString())
                            document.reference.update("Pagado", true)

                            // Update the Dinero field of the corresponding user
                            val docRef = db.collection("Users").document(userId)
                            docRef.get().addOnSuccessListener { userDoc ->
                                val dinero = userDoc.getDouble("Dinero") ?: 0.0
                                val updatedDinero = dinero - selectedMontoMensual.toDouble()
                                docRef.update("Dinero", updatedDinero)
                            }

                            Toast.makeText(requireContext(), "Pago exitoso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "No tienes suficiente dinero para realizar el pago.", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        }


        return view
    }

}