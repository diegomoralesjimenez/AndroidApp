package com.example.proyecto1

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow

class AsignarPrestamo : Fragment() {

    private lateinit var cedulaTextView: TextView
    private lateinit var nombreTextView: TextView
    private lateinit var salarioTextView: TextView
    private lateinit var montoPrestamo: TextView
    private var tipoCredito: RadioGroup? = null
    lateinit var btnTipoCredito: RadioButton
    private var duracionPrestamo: RadioGroup? = null
    lateinit var btnDuracionPrestamo: RadioButton
    private lateinit var tasaInteres: TextView
    private lateinit var montoMensual: TextView


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
        montoMensual = view.findViewById(R.id.txtMontoMensual)

        tipoCredito = view.findViewById(R.id.radioGCredito)
        duracionPrestamo = view.findViewById(R.id.radioGDuracion)


        val addButton = view.findViewById<Button>(R.id.btnAgregar)
        addButton.setOnClickListener {
            insertar(view)
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
                    Log.w(TAG, "Error getting user information.", exception)
                    nombreTextView.setText("")
                    salarioTextView.setText("Ocurrió un error al buscar al usuario.")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        return view
    }

    fun insertar(view: View) {
        // se podria agregar el atributo "document: DocumentSnapshot" en el metodo, se trae
        // el cliente como parametro del metodo "buscarClientePorCedula"

        if (validarAtributos()) {

            val newCedula = cedulaTextView.text.toString()

// ********************************************************************************************************
            db.collection("Users")
                .whereEqualTo("Cedula", newCedula)
                .get()
                .addOnSuccessListener { documents ->
                    // Iterar sobre los documentos obtenidos (debería haber solo uno)
                    for (document in documents) {

                        val newNombre = document.getString("Nombre")
                        val salario = document.getLong("Salario")?.toInt()

                        nombreTextView.text = newNombre
                        val newSalario = salario
                        var salarioNumerico = newSalario

                        // Debemos limitar el prestamo al 45% del salario
                        val newMontoPrest = montoPrestamo.text.toString()
                        val montoIngresado = newMontoPrest.toInt()
                        lateinit var newMonto: String


                        if (salarioNumerico != null) {
                            if (montoIngresado > salarioNumerico * 0.45) {
                                Toast.makeText(
                                    context,
                                    "El monto debe ser menor al 45% del salario.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                newMonto = montoIngresado.toString()
                            }
                        }

                        // Captura informacion desde los RadioGroup Tipo de Credito
                        val opcionTipoCredito: Int = tipoCredito!!.checkedRadioButtonId
                        btnTipoCredito = view.findViewById(opcionTipoCredito)
                        val newTipoCred = btnTipoCredito.text.toString()

                        // Captura informacion desde los RadioGroup Duracion del Prestamo
                        val opcionDuracionPrest: Int = tipoCredito!!.checkedRadioButtonId
                        btnDuracionPrestamo = view.findViewById(opcionDuracionPrest)
                        val newDuracPrest = btnDuracionPrestamo.text.toString()

                        //ESTOS SON EDITADOS POR EL ADMINISTRADOR
                        lateinit var newtasaInteres: String
                        if (newTipoCred == "Hipotecario") {
                            tasaInteres.setText("7.5%")
                            newtasaInteres = tasaInteres.text.toString()
                        } else if (newTipoCred == "Educacion") {
                            tasaInteres.setText("8%")
                            newtasaInteres = tasaInteres.text.toString()
                        } else if (newTipoCred == "Personal") {
                            tasaInteres.setText("10%")
                            newtasaInteres = tasaInteres.text.toString()
                        } else if (newTipoCred == "Viajes") {
                            tasaInteres.setText("12%")
                            newtasaInteres = tasaInteres.text.toString()
                        }

                        lateinit var cuotaMensual: String
                        val newMontoNumerico = newMonto.toInt()
                        lateinit var numCuotas: String
                        if (newDuracPrest == "3") {
                            numCuotas = "36"
                        } else if (newDuracPrest == "5") {
                            numCuotas = "60"
                        } else if (newDuracPrest == "10") {
                            numCuotas = "120"
                        }

                        val numCuotasNumerico = numCuotas.toInt()
                        if (newtasaInteres == "7.5%") {
                            cuotaMensual =
                                (((newMontoNumerico * 0.075) / (1 - (1 + 0.075).pow(numCuotasNumerico))).toString())
                        } else if (newtasaInteres == "8%") {
                            cuotaMensual =
                                (((newMontoNumerico * 0.8) / (1 - (1 + 0.8).pow(numCuotasNumerico))).toString())
                        } else if (newtasaInteres == "10%") {
                            cuotaMensual =
                                (((newMontoNumerico * 0.1) / (1 - (1 + 0.1).pow(numCuotasNumerico))).toString())
                        } else if (newtasaInteres == "12%") {
                            cuotaMensual =
                                (((newMontoNumerico * 0.12) / (1 - (1 + 0.12).pow(numCuotasNumerico))).toString())
                        }

                        montoMensual.text = cuotaMensual


                        // Obtener el ID del usuario
                        val usuarioId = document.id

                        val prestamo = hashMapOf(
                            "Cedula" to newCedula,
                            "Nombre" to newNombre,
                            "Salario" to newSalario,
                            "MontoPrestamo" to newMonto,
                            "TipoCredito" to newTipoCred,
                            "DuracionPrestamo" to newDuracPrest,
                            "TasaInteres" to newtasaInteres,
                            "MontoMensual" to cuotaMensual
                        )

                        db.collection("Users").document(usuarioId)
                            .collection("Prestamos").add(prestamo)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Prestamo agregado exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error al agregar el prestamo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
// ********************************************************************************************************

        } else {
            Toast.makeText(context, "Todos los espacios deben llenarse.", Toast.LENGTH_SHORT).show()
        }
    }

    fun buscarClientePorCedula(db: FirebaseFirestore, cedula: String): DocumentSnapshot? {
        val query = db.collection("Users").whereEqualTo("Cedula", cedula)
        val querySnapshot = query.get().result
        if (querySnapshot?.documents?.isNotEmpty() == true) {
            return querySnapshot.documents[0]
        }
        return null
    }

    private fun validarAtributos(): Boolean {
        return !cedulaTextView.text.isEmpty() &&
                !nombreTextView.text.isEmpty() &&
                !salarioTextView.text.isEmpty() &&
                !montoPrestamo.text.isEmpty() &&
                (duracionPrestamo != null && duracionPrestamo!!.checkedRadioButtonId != -1) &&
                (tipoCredito != null && tipoCredito!!.checkedRadioButtonId != -1)
    }





}