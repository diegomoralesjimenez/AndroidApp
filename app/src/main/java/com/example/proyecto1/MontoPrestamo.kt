package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MontoPrestamo : Fragment() {
    private var prestamo: Float = 0f
    private var tasaInteres: Float = 0f
    private var monto: Float = 0f
    private var duracion: String = ""
    private var tipoCredito: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monto_prestamo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            prestamo = it.getFloat("Prestamo")
            tasaInteres = it.getFloat("TasaInteres")
            monto = it.getFloat("Monto")
            duracion = it.getString("DuracionPrestamo")?:""
            tipoCredito = it.getString("TipoCredito")?:""
        }

        // Set the calculated values to the appropriate views
        val prestamoTextView = view.findViewById<TextView>(R.id.prestamoTotal)
        val tasaInteresTextView = view.findViewById<TextView>(R.id.tasaInteres)
        val montoTextView = view.findViewById<TextView>(R.id.montoPrestamo)
        val duracionTextView = view.findViewById<TextView>(R.id.duracion)
        val tipoCreditoView = view.findViewById<TextView>(R.id.tipoCredito)

        prestamoTextView.text = prestamo.toString()
        tasaInteresTextView.text = tasaInteres.toString()
        montoTextView.text = monto.toString()
        duracionTextView.text = duracion
        tipoCreditoView.text = tipoCredito
    }
}
