package com.example.proyecto1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.proyecto1.databinding.ActivityAdminBinding

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationLayout.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cliente -> (replaceFragment(newClient()))
                R.id.prestamo -> replaceFragment(AsignarPrestamo())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.adminLayout, fragment)
        fragmentTransaction.commit()
    }

}