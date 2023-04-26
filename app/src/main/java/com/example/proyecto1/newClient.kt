package com.example.proyecto1

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class newClient : Fragment() {

    private lateinit var cedulaTextView: TextView
    private lateinit var nombreTextView: TextView
    private lateinit var contrasenaTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var fechaTextView: TextView
    private var estadoCivilGroup : RadioGroup? = null
    lateinit var estadoCiv: RadioButton
    private lateinit var salarioTextView: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_client, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        //userId = FirebaseAuth.getInstance().toString()

        // Variables inicializadas

        cedulaTextView = view.findViewById(R.id.cedula)
        nombreTextView = view.findViewById(R.id.nombre)
        contrasenaTextView = view.findViewById(R.id.contraseña)
        direccionTextView = view.findViewById(R.id.direccion)
        fechaTextView = view.findViewById(R.id.fechaNacimiento)
        estadoCivilGroup = view.findViewById(R.id.radioGroup)
        salarioTextView = view.findViewById(R.id.txtSalario)

        val addButton = view.findViewById<Button>(R.id.add)
        addButton.setOnClickListener {
            insertar(view)
        }

        return view
    }

    // Metodo insertar nuevo cliente

    fun insertar(view: View) {

        if (validarAtributos()) {
            //Datos que se actualizan
            val newCedula = cedulaTextView.text.toString()
            val newNombre = nombreTextView.text.toString()
            val newContrasena = contrasenaTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()

            // Captura informacion desde los RadioGroup
            val opcionEstadoCiv: Int = estadoCivilGroup!!.checkedRadioButtonId
            estadoCiv = view.findViewById(opcionEstadoCiv)
            val newEstadoCiv = estadoCiv.text.toString()

            val newSalario = salarioTextView.text.toString().toInt()

            /*
            Para renderizar todo menos el tipo
            val data = db.collection("Prestamos").whereNotEqualTo("Tipo","Inicio")
            println(data);
            */

            // Coleccion
            val userRef = db.collection("Users").document()

            // La siguiente linea muestra como guardar con la cedula como identificador
            //val prestamoRef = db.collection("Users").document(newCedula)
            //                .collection("Prestamos").document()
            //Creacion de los valores de la coleccion

// --------------------------------------------------------------------------
// --------------------------------------------------------------------------
// --------------------------------------------------------------------------
            // Crear instancia de FirebaseAuth
            val mAuth = FirebaseAuth.getInstance()

            // Crear cuenta de usuario en Firebase
            mAuth.createUserWithEmailAndPassword(newCedula + "@example.com", newContrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Obtener ID de usuario generado por Firebase
                        val userId = mAuth.currentUser?.uid

                        // Agregar información adicional del usuario en Firestore
                        val user = hashMapOf(
                            "Cedula" to newCedula,
                            "Nombre" to newNombre,
                            "Contraseña" to newContrasena,
                            "Direccion" to newDireccion,
                            "FechaNacimiento" to newFechaNacimiento,
                            "EstadoCivil" to newEstadoCiv,
                            "Salario" to newSalario,
                            "Role" to "Client"
                        )

                        userRef.set(user)
                            .addOnSuccessListener {
                                val clienteId = userRef.id

                                // Crear la colección de préstamos dentro del documento
                                val prestamosRef =
                                    db.collection("Users").document(clienteId)
                                        .collection("Prestamos")

                                // Agregar un préstamo a la colección de préstamos
                                val prestamo = hashMapOf(
                                    "Cedula" to "",
                                    "Nombre" to "",
                                    "Salario" to "",
                                    "MontoPrestamo" to "",
                                    "TipoCredito" to "Inicio",
                                    "DuracionPrestamo" to "",
                                    "TasaInteres" to "",
                                    "MontoMensual" to ""
                                )

                                prestamosRef.add(prestamo)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Cliente y préstamo agregados satisfactoriamente!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error al agregar el préstamo.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
//----------------------------------------------------------------------------------
                                // Crear la colección de préstamos dentro del documento
                                val ahorrosRef =
                                    db.collection("Users").document(clienteId).collection("Ahorro")

                                // Agregar un préstamo a la colección de préstamos
                                val ahorro = hashMapOf(
                                    "Tipo" to "",
                                    "Monto" to 0.0,
                                )

                                ahorrosRef.add(ahorro)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Cliente, préstamo y ahorro agregados satisfactoriamente!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error al agregar el ahorro.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                db.collection("Users").document(userId!!)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Cliente agregado satisfactoriamente!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Error al agregar el cliente.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error al agregar el cliente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
// --------------------------------------------------------------------------
// --------------------------------------------------------------------------
// --------------------------------------------------------------------------

//----------------------------------------------------------------------------------

                    Toast.makeText(
                        context,
                        "Cliente agregado satisfactoriamente!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al agregar el cliente.", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Toast.makeText(context, "Todos los espacios deben llenarse.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validarAtributos(): Boolean {
        return !cedulaTextView.text.isEmpty() &&
                !nombreTextView.text.isEmpty() &&
                !contrasenaTextView.text.isEmpty() &&
                !direccionTextView.text.isEmpty() &&
                !fechaTextView.text.isEmpty() &&
                (estadoCivilGroup != null && estadoCivilGroup!!.checkedRadioButtonId != -1) &&
                !salarioTextView.text.isEmpty()
    }


}