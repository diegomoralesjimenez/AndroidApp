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

class NewClient : Fragment() {

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
            //Datos que se actualizan
            val newCedula = cedulaTextView.text.toString()
            val newNombre = nombreTextView.text.toString()
            val newContrasena = contrasenaTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()

            val opcionEstadoCiv : Int = estadoCivilGroup!!.checkedRadioButtonId
            estadoCiv = view.findViewById(opcionEstadoCiv)
            val newEstadoCiv = estadoCiv.text.toString()

            val newSalario = salarioTextView.text.toString()

            //Coleccion
            val userRef = db.collection("Users").document()

            //Creacion de los valores de la coleccion
            val user = hashMapOf(
                "Cedula" to newCedula,
                "Nombre" to newNombre,
                "Contraseña" to newContrasena,
                "Direccion" to newDireccion,
                "FechaNacimiento" to newFechaNacimiento,
                "EstadoCivil" to newEstadoCiv,
                "Salario" to newSalario
            )

            userRef.set(user)
                .addOnSuccessListener {
                    Toast.makeText(context, "Cliente agregado satisfactoriamente!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al agregar el cliente.", Toast.LENGTH_SHORT).show()
                }
        }



        return view
    }

    // Metodo insertar nuevo cliente



}