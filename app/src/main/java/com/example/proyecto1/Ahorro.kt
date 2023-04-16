package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*


class Ahorro : Fragment() {

    private lateinit var navidenoView: TextView;
    private lateinit var escolarView: TextView;
    private lateinit var marchamoView: TextView;
    private lateinit var extraordinarioView: TextView;

    private lateinit var ahorroBtn: Button;


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

        ahorroBtn = view.findViewById(R.id.ahorroBtn)


        val savingsTypes = arrayOf("Navideno", "Escolar", "Marchamo", "Extraordinario")
        val savingsViews = arrayOf(navidenoView, escolarView, marchamoView, extraordinarioView)

        for (i in savingsTypes.indices) {
            val savingsType = savingsTypes[i]
            val savingsView = savingsViews[i]

            val savingsDocRef = db.collection("Users").document(userId)
                .collection("Ahorro").document(savingsType)

            savingsDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val savingsValue = documentSnapshot.getDouble("monto$savingsType")
                    val formattedSavingsValue = NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(savingsValue)
                    savingsView.text = formattedSavingsValue
                }
            }
        }

        ahorroBtn.setOnClickListener {
            val newFragment = AhorroProgramado()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_ahorro, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }



        return view
    }
}