package com.example.proyecto1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Prestamo : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String


    @SuppressLint("MissingInflatedId")
    fun onCreate(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_client, container, false)


        // Firestore y Firebase inicializado
        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prestamo, container, false)
    }

}