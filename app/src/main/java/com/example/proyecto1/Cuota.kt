package com.example.proyecto1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class Cuota : Fragment() {

    private lateinit var salarioView: EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var textInteres: EditText;
    private lateinit var prestamoView: EditText;

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button

    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button

    private lateinit var calcularBtn: Button

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
        prestamoView = view.findViewById(R.id.prestamo)

        //Botones
        textInteres = view.findViewById(R.id.txtTasaInteres)
        button1 = view.findViewById(R.id.buttonH)
        button2 = view.findViewById(R.id.buttonE)
        button3 = view.findViewById(R.id.buttonV)
        button4 = view.findViewById(R.id.buttonO)

        button5 = view.findViewById(R.id.button5)
        button6 = view.findViewById(R.id.button6)
        button7 = view.findViewById(R.id.button7)
        button8 = view.findViewById(R.id.button8)

        var tipoCredito = ""
        fun setInterestRate(interestRate: String, button: Button) {
            textInteres.setText(interestRate)
            if (button.currentTextColor != Color.BLACK) {
                button.setTextColor(Color.BLACK)
            }
            tipoCredito = when(interestRate) {
                "7.5" -> "Hipotecario"
                "8" -> "Educacion"
                "10" -> "Personal"
                "12" -> "Viajes"
                else -> ""
            }
        }

        button1.setOnClickListener { setInterestRate("7.5", it as Button) }
        button2.setOnClickListener { setInterestRate("8", it as Button) }
        button3.setOnClickListener { setInterestRate("10", it as Button) }
        button4.setOnClickListener { setInterestRate("12", it as Button) }

        var duracion = ""
        fun duracionPrestamo(duracionPrestamo: String, button: Button) {
            duracion = duracionPrestamo;
        }

        button5.setOnClickListener { duracionPrestamo("1", it as Button) }
        button6.setOnClickListener { duracionPrestamo("3", it as Button) }
        button7.setOnClickListener { duracionPrestamo("5", it as Button) }
        button8.setOnClickListener { duracionPrestamo("10", it as Button) }


        calcularBtn = view.findViewById(R.id.calcular)

        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                //Get a los valores de la coleccion
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val salario = documentSnapshot.getDouble("Salario")
                val salarioFormatted = formatter.format(salario)
                salarioView.setText(salarioFormatted)
            }
        }

        prestamoView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val salario = formatter.parse(salarioView.text.toString())?.toDouble()
                val prestamo = s.toString().toFloatOrNull()

                // Format the input to the Costa Rican currency
                if (prestamo != null) {
                    val prestamoFormatted = formatter.format(prestamo)
                }
                if (salario != null && prestamo != null && prestamo > salario) {
                    prestamoView.error = "El prestamo debe ser menor o igual al salario"
                    calcularBtn.isEnabled = false
                } else {
                    prestamoView.error = null
                    calcularBtn.isEnabled = true
                }
            }
        })


        calcularBtn.setOnClickListener {
            // Get the entered salary and interest rate values
            val prestamoStr = prestamoView.text.toString()
            val tasaInteresStr = textInteres.text.toString()

            if (prestamoStr.isBlank() || tasaInteresStr.isBlank()) {
                Toast.makeText(activity, "Please enter valid values", Toast.LENGTH_SHORT).show()
            } else {
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val salario = formatter.parse(salarioView.text.toString())?.toDouble()
                val prestamo = prestamoStr.toFloatOrNull()
                val tasaInteres = tasaInteresStr.toFloatOrNull()

                if (salario == null || prestamo == null || tasaInteres == null) {
                    Toast.makeText(activity, "Please enter valid values2", Toast.LENGTH_SHORT).show()
                } else {
                    val maxPrestamo = salario * 0.45 // calculate the maximum allowable prestamo amount
                    if (prestamo > maxPrestamo) {
                        Toast.makeText(activity, "El prestamo debe ser menor o igual al 45% del salario", Toast.LENGTH_SHORT).show()
                    } else {
                        val monto = prestamo * tasaInteres / 100

                        val newFragment = MontoPrestamo()
                        newFragment.arguments = bundleOf(
                            "Prestamo" to prestamo,
                            "TasaInteres" to tasaInteres,
                            "Monto" to monto,
                            "DuracionPrestamo" to duracion,
                            "TipoCredito" to tipoCredito
                        )

                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_cuota, newFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }
            }
        }
        return view
    }


}