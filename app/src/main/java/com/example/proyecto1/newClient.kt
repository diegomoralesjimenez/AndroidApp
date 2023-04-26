package com.example.proyecto1

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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
    private lateinit var apellidosTextView: TextView
    private lateinit var contrasenaTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var fechaTextView: TextView
    private var estadoCivilGroup : RadioGroup? = null
    lateinit var estadoCiv: RadioButton
    private lateinit var salarioTextView: TextView

    private lateinit var db: FirebaseFirestore
    //private lateinit var userId: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_client, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        //userId = FirebaseAuth.getInstance().currentUser!!.uid
        //userId = FirebaseAuth.getInstance()

        // Variables inicializadas

        cedulaTextView = view.findViewById(R.id.cedula)
        nombreTextView = view.findViewById(R.id.nombre)
        apellidosTextView = view.findViewById(R.id.apellidos)
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

    fun insertar(view : View){

        if (validarAtributos()) {
            //Datos que se actualizan
            val newCedula = cedulaTextView.text.toString()
            val newNombre = nombreTextView.text.toString()
            val newApellidos = apellidosTextView.text.toString()
            val newContrasena = contrasenaTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()

            // Captura informacion desde los RadioGroup
            val opcionEstadoCiv : Int = estadoCivilGroup!!.checkedRadioButtonId
            estadoCiv = view.findViewById(opcionEstadoCiv)
            val newEstadoCiv = estadoCiv.text.toString()

            val newSalario = salarioTextView.text.toString().toInt()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(newCedula + "@gmail.com", newContrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Coleccion
                        //val userRef = db.collection("Users").document()

                        //Creacion de los valores de la coleccion
                        val user = hashMapOf(
                            "Cedula" to newCedula,
                            "Nombre" to newNombre,
                            "Apellidos" to newApellidos,
                            "Contraseña" to newContrasena,
                            "Direccion" to newDireccion,
                            "FechaNacimiento" to newFechaNacimiento,
                            "EstadoCivil" to newEstadoCiv,
                            "Salario" to newSalario,
                            "Role" to "Client"
                        )
                        db.collection("Users").document(task.result!!.user!!.uid).set(user)
                        val userRef = db.collection("Users").whereEqualTo("Cedula",newCedula)

                        userRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val documents = task.result?.documents
                                if (documents != null && documents.isNotEmpty()) {
                                    val userDocRef = documents[0].reference
                                    val prestamosCollectionRef = userDocRef.collection("Prestamos")
                                    val ahorrosCollectionRef = userDocRef.collection("Ahorro")

                                    // Agregar un préstamo a la colección de préstamos
                                    val prestamo = hashMapOf(
                                        "MontoPrestamo" to "",
                                        "TipoCredito" to "Inicio",
                                        "DuracionPrestamo" to "",
                                        "TasaInteres" to "",
                                        "MontoMensual" to ""
                                    )

                                    val ahorro = hashMapOf(
                                        "TipoAhorro" to "",
                                        "MontoAhorro" to 0.0,
                                        "Meses" to 0
                                    )

                                    // Agregar el nuevo préstamo con el método add()
                                    prestamosCollectionRef.add(prestamo)
                                        .addOnSuccessListener {
                                            Log.d(ContentValues.TAG, "Prestamo agregado exitosamente")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(ContentValues.TAG, "Error al agregar prestamo", e)
                                        }

                                    // Agregar el nuevo préstamo con el método add()
                                    ahorrosCollectionRef.add(ahorro)
                                        .addOnSuccessListener {
                                            Log.d(ContentValues.TAG, "Ahorro agregado exitosamente")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(ContentValues.TAG, "Error al agregar Ahorro", e)
                                        }
                                } else {
                                    Log.d(ContentValues.TAG, "No existe un usuario con la cedula $newCedula")
                                }
                            } else {
                                Log.d(ContentValues.TAG, "Error al obtener documentos", task.exception)
                            }
                        }
                        Toast.makeText(context, "Usuario registrado y autenticado exitosamente.", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
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