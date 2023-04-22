package com.example.proyecto1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class PrestamoAdapter(private val prestamos: List<Map<String, Any>>) :
    RecyclerView.Adapter<PrestamoAdapter.PrestamoViewHolder>() {

    class PrestamoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loanTypeTextView: TextView = itemView.findViewById(R.id.loan_type)
        val monthsLeftTextView: TextView = itemView.findViewById(R.id.months_left)
        val amountLeftTextView: TextView = itemView.findViewById(R.id.amount_left)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pres_item, parent, false)
        return PrestamoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo = prestamos[position]
        holder.loanTypeTextView.text = prestamo["Tipo"] as String
        holder.monthsLeftTextView.text = "${prestamo["Annos"]}"
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
        formatter.currency = Currency.getInstance("CRC")
        holder.amountLeftTextView.text = formatter.format(prestamo["Monto"] as Double)
    }

    override fun getItemCount(): Int {
        return prestamos.size
    }
}
