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


class Personal : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var fechaTextView: TextView
    private lateinit var apellidoView: TextView
    private lateinit var salarioView: EditText

    private lateinit var radioGroup: RadioGroup


    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout del fragment
        val view = inflater.inflate(R.layout.fragment_personal, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Variables inicializadas
        nombreTextView = view.findViewById(R.id.nombre)
        direccionTextView = view.findViewById(R.id.direccion)
        fechaTextView = view.findViewById(R.id.fechaNacimiento)
        apellidoView = view.findViewById(R.id.apellidos)
        salarioView = view.findViewById(R.id.salario)

        radioGroup = view.findViewById(R.id.radio_group_estadocivil)

        var estadoCivil: String = ""
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            estadoCivil = view.findViewById<RadioButton>(checkedId).text.toString()
        }


        // [GET] Agarra la informacion de la collecion de los usuarios de la base de datos
        val docRef = db.collection("Users").document(userId)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                //Get a los valores de la coleccion
                val nombre = documentSnapshot.getString("Nombre")
                val direccion = documentSnapshot.getString("Direccion")
                val fechaNacimiento = documentSnapshot.getString("FechaNacimiento")
                val salario = documentSnapshot.getDouble("Salario")
                val apellidos = documentSnapshot.getString("Apellidos")
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
                val salarioFormatted = formatter.format(salario)
                val estadoCivil = documentSnapshot.getString("EstadoCivil") ?: ""

                salarioView.setText(salarioFormatted)
                nombreTextView.text = nombre
                direccionTextView.text = direccion
                fechaTextView.text = fechaNacimiento
                apellidoView.text = apellidos


                // Actualizar la selección del radio group
                if (estadoCivil == "Soltero") {
                    radioGroup.check(R.id.radio_button_soltero)
                } else if (estadoCivil == "Casado") {
                    radioGroup.check(R.id.radio_button_casado)
                }
            }
        }

        //Actualiza los Usuarios en la base de datos
        val updateButton = view.findViewById<Button>(R.id.update)
        updateButton.setOnClickListener {


            //Datos que se actualizan
            val newNombre = nombreTextView.text.toString()
            val newDireccion = direccionTextView.text.toString()
            val newFechaNacimiento = fechaTextView.text.toString()
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
            val newSalario = formatter.parse(salarioView.text.toString())?.toDouble()
            val newApellido = apellidoView.text.toString()

            //Coleccion
            val userRef = db.collection("Users").document(userId)

            //Update a los valores de la coleccion;
            userRef.update("Nombre", newNombre, "Apellidos", newApellido,  "Direccion", newDireccion, "FechaNacimiento", newFechaNacimiento, "Salario", newSalario, "EstadoCivil", estadoCivil)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }




        return view
    }
}
