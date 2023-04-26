package com.example.proyecto1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PagoPrestamo : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pago_prestamo, container, false)
    }

    companion object {
        fun newInstance(prestamo: Map<String, Any>): PagoPrestamo {
            val fragment = PagoPrestamo()
            val args = Bundle()
            args.putString("tipo", prestamo["Tipo"] as String?)
            args.putInt("annos", prestamo["Annos"].toString().toInt())
            args.putDouble("monto", prestamo["Monto"] as Double)
            fragment.arguments = args
            return fragment
        }
    }


}