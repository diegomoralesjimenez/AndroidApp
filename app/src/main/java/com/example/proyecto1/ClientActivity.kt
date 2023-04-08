package com.example.proyecto1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto1.databinding.ActivityClientBinding

class ClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientBinding
    private lateinit var clientNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Prestamo())

        clientNameTextView = findViewById(R.id.welcome)

        binding.navigationLayout.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.prestamo -> replaceFragment(Prestamo())
                R.id.ahorro -> replaceFragment(Ahorro())
                R.id.cuota -> replaceFragment(Cuota())
                R.id.personal -> replaceFragment(Personal())

                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}