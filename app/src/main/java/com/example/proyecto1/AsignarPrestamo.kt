package com.example.proyecto1

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects
import kotlin.math.pow

class AsignarPrestamo : Fragment() {

    // TextView
    private lateinit var cedulaTextView: TextView
    private lateinit var nombreTextView: TextView
    private lateinit var salarioTextView: TextView
    private lateinit var montoPrestamo: TextView
    private lateinit var tasaInteres: TextView
    private lateinit var newMontoMensual: TextView

    //Botones
    private lateinit var btnHipo : Button
    private lateinit var btnEduca : Button
    private lateinit var btnPersonal : Button
    private lateinit var btnViaje : Button
    private var duracionPrestamo: RadioGroup? = null
    lateinit var btnDuracionPrestamo: RadioButton

    //Base de Datos
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_asignar_prestamo, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas

        cedulaTextView = view.findViewById(R.id.cedulaPrest)
        nombreTextView = view.findViewById(R.id.txtNombreCliente)
        salarioTextView = view.findViewById(R.id.txtSalario)
        montoPrestamo = view.findViewById(R.id.txtPrestamo)
        tasaInteres = view.findViewById(R.id.txtTasaInteres)
        newMontoMensual = view.findViewById(R.id.txtMontoMensual)

        btnHipo = view.findViewById(R.id.btnHipotecario)
        btnEduca = view.findViewById(R.id.btnEducativo)
        btnPersonal = view.findViewById(R.id.btnPersonal)
        btnViaje = view.findViewById(R.id.btnViajes)
        duracionPrestamo = view.findViewById(R.id.radioGDuracion)

        val addButton = view.findViewById<Button>(R.id.btnAgregar)
        addButton.setOnClickListener {

            db.collection("Users")
                .whereEqualTo("Cedula", cedulaTextView.text.toString())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val userId = document.id
                        insertar(view)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener el usuario con cédula", exception)
                }

        //insertar(view)
        }

        cedulaTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When the user types in the cedula, query the database for the user information
                val usersRef = db.collection("Users")
                val query = usersRef.whereEqualTo("Cedula", s.toString())
                query.get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.size() > 0) {
                        // The user exists, so retrieve their information
                        val documentSnapshot = querySnapshot.documents[0]
                        val nombre = documentSnapshot.getString("Nombre")
                        val salario = documentSnapshot.getDouble("Salario")
                        nombreTextView.setText(nombre)
                        salarioTextView.setText(salario.toString())
                    } else {
                        // The user does not exist
                        nombreTextView.setText("")
                        salarioTextView.setText("No se encontró al usuario con la cédula especificada.")
                    }
                }.addOnFailureListener { exception ->
                    // An error occurred while retrieving the user information
                    Log.w(TAG, "Error al obtener la informacion.", exception)
                    nombreTextView.setText("")
                    salarioTextView.setText("Ocurrió un error al buscar al usuario.")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        fun setInterestRate(interestRate: String, button: Button) {
            tasaInteres.setText(interestRate)
            if (button.currentTextColor != Color.BLACK) {
                button.setTextColor(Color.BLACK)
            }
        }

        btnHipo.setOnClickListener { setInterestRate("7.5", it as Button) }
        btnEduca.setOnClickListener { setInterestRate("8", it as Button) }
        btnViaje.setOnClickListener { setInterestRate("12", it as Button) }
        btnPersonal.setOnClickListener { setInterestRate("10", it as Button) }


        return view
    }

    fun insertar(view: View) {
        // Completamos las variables
        val newCedula = cedulaTextView.text.toString()
        val newMontoPrest = montoPrestamo.text.toString()


        var newTipodeCredito = ""
        if (tasaInteres.text.toString() == "7.5") {
            newTipodeCredito = "Hipotecario"
        } else if (tasaInteres.text.toString() == "8") {
            newTipodeCredito = "Educacion"
        } else if (tasaInteres.text.toString() == "10") {
            newTipodeCredito = "Personal"
        } else if (tasaInteres.text.toString() == "12") {
            newTipodeCredito = "Viajes"
        }

        // Captura informacion desde los RadioGroup Duracion del Prestamo
        val opcionDuracionPrest: Int = duracionPrestamo!!.checkedRadioButtonId
        btnDuracionPrestamo = view.findViewById(opcionDuracionPrest)
        val newDuracPrest = btnDuracionPrestamo.text.toString()

        //ESTOS SON EDITADOS POR EL ADMINISTRADOR
        var newtasaInteres = tasaInteres.text.toString()


        // Calculo de cuota mensual
        lateinit var numCuotas: String
        if (newDuracPrest == "3") {
            numCuotas = "36"
        } else if (newDuracPrest == "5") {
            numCuotas = "60"
        } else if (newDuracPrest == "10") {
            numCuotas = "120"
        }

        val numCuotasNumerico = numCuotas.toInt()
        newMontoMensual.setText(calculaCuota(newtasaInteres, newMontoPrest.toInt(), numCuotasNumerico).toString())
        val newMontoMensualNum = newMontoMensual.text.toString()

        if (validarAtributos()) {
            // Coleccion
            val userRef = db.collection("Users").whereEqualTo("Cedula",newCedula)


            // Agregar un préstamo a la colección de préstamos
            val prestamo = hashMapOf(
                "MontoPrestamo" to newMontoPrest,
                "TipoCredito" to newTipodeCredito,
                "DuracionPrestamo" to newDuracPrest,
                "TasaInteres" to newtasaInteres,
                "MontoMensual" to newMontoMensualNum
            )

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result?.documents
                    if (documents != null && documents.isNotEmpty()) {
                        val userDocRef = documents[0].reference
                        val prestamosCollectionRef = userDocRef.collection("Prestamos")

                        // Agregar el nuevo préstamo con el método add()
                        prestamosCollectionRef.add(prestamo)
                            .addOnSuccessListener {
                                Log.d(TAG, "Prestamo agregado exitosamente")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al agregar prestamo", e)
                            }

                    } else {
                        Log.d(TAG, "No existe un usuario con la cedula $newCedula")
                    }
                } else {
                    Log.d(TAG, "Error al obtener documentos", task.exception)
                }
            }

            Toast.makeText(context, "Préstamo agregado satisfactoriamente!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Todos los espacios deben llenarse.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarAtributos(): Boolean {
        return !cedulaTextView.text.isEmpty() &&
                !nombreTextView.text.isEmpty() &&
                !salarioTextView.text.isEmpty() &&
                !montoPrestamo.text.isEmpty() &&
                !newMontoMensual.text.isEmpty() &&
                (duracionPrestamo != null && duracionPrestamo!!.checkedRadioButtonId != -1)
                //(tipoCredito != null && tipoCredito!!.checkedRadioButtonId != -1)
    }

    private fun calculaCuota(newtasaInteres : String, newMontoMensualNum : Int, numCuotasNumerico : Int): Int {
        val newSalario = salarioTextView.text.toString()
        if(newMontoMensualNum > newSalario.toDouble() * 0.45){
            Toast.makeText(context, "El prestamo no puede ser mayor al 45% del salario.", Toast.LENGTH_SHORT).show()
        }else{
            // Convertir la tasa de interés a decimal
            val tasaInteresDecimal = newtasaInteres.toDouble() / 100

            // Calcular la cuota mensual utilizando la fórmula
            val cuota = (newMontoMensualNum * tasaInteresDecimal) / (1 - (1 + tasaInteresDecimal).pow(-numCuotasNumerico))

            // Redondear el resultado y convertirlo a un entero
            return cuota.toInt()
        }
        return 0
    }

}