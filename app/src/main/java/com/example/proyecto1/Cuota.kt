package com.example.proyecto1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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


        fun setInterestRate(interestRate: String, button: Button) {
            textInteres.setText(interestRate)
            if (button.currentTextColor != Color.BLACK) {
                button.setTextColor(Color.BLACK)
            }
        }

        button1.setOnClickListener { setInterestRate("7.5", it as Button) }
        button2.setOnClickListener { setInterestRate("8", it as Button) }
        button3.setOnClickListener { setInterestRate("10", it as Button) }
        button4.setOnClickListener { setInterestRate("12", it as Button) }

        calcularBtn = view.findViewById(R.id.calcular)

        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                //Get a los valores de la coleccion
                val salario = documentSnapshot.getDouble("Salario")
                salarioView.setText(salario.toString())


            }
        }

        calcularBtn.setOnClickListener {
            // Get the entered salary and interest rate values
            val prestamoStr = prestamoView.text.toString()
            val tasaInteresStr = textInteres.text.toString()

            if (prestamoStr.isBlank() || tasaInteresStr.isBlank()) {
                Toast.makeText(activity, "Please enter valid values", Toast.LENGTH_SHORT).show()
            } else {
                val salario = salarioView.text.toString().toFloatOrNull()
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
                            "Monto" to monto
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