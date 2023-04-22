package com.example.proyecto1

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Prestamo : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var prestamoAdapter: PrestamoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prestamo, container, false)

        db = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser!!.uid

        recyclerView = view.findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        prestamoAdapter = PrestamoAdapter(listOf())
        recyclerView.adapter = prestamoAdapter

        val prestamos = mutableListOf<Map<String, Any>>()

        db.collection("Users").document(userId).collection("Prestamos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val anos = document.getString("Annos") ?: ""
                    val monto = document.getDouble("Monto") ?: 0.0
                    val tipo = document.getString("Tipo") ?: ""
                    val prestamo = mapOf("Annos" to anos, "Monto" to monto, "Tipo" to tipo)
                    prestamos.add(prestamo)
                }

                prestamoAdapter = PrestamoAdapter(prestamos)
                recyclerView.adapter = prestamoAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error retrieving prestamos", exception)
            }



        return view
    }

}