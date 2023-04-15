package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*


class Ahorro : Fragment() {

    private lateinit var navidenoView: TextView;
    private lateinit var escolarView: TextView;
    private lateinit var marchamoView: TextView;
    private lateinit var extraordinarioView: TextView;


    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ahorro, container, false)

        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid


        navidenoView = view.findViewById(R.id.montoNavideno)
        escolarView = view.findViewById(R.id.montoEscolar)
        marchamoView = view.findViewById(R.id.montoMarchamo)
        extraordinarioView = view.findViewById(R.id.montoExtraordinario)


        val navidenoDocRef = db.collection("Users").document(userId)
            .collection("Ahorro").document("Navideno")


        navidenoDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val montoNavideno = documentSnapshot.getDouble("montoNavideno")
                //val montoEscolar= documentSnapshot.getDouble("montoEscolar")
               // val montoMarchamo = documentSnapshot.getDouble("montoMarchamo")
                //val montoExtraordinario =  documentSnapshot.getDouble("montoExtraordinario")

                val formattedMontoNavideno = NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(montoNavideno)
               // val formattedMontoEscolar = NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(montoEscolar)
              //  val formattedMontoMarchamo = NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(montoMarchamo)
               // val formattedMontoExtraordinario = NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(montoExtraordinario)
                navidenoView.text = formattedMontoNavideno
               // escolarView.text = formattedMontoEscolar
               // marchamoView.text =  formattedMontoMarchamo
                //extraordinarioView.text =  formattedMontoExtraordinario
            }
        }



        return view
    }
}