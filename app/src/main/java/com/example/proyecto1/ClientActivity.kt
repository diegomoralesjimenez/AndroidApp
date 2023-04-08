package com.example.proyecto1

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.proyecto1.databinding.ActivityClientBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class ClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientBinding
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private lateinit var frameLayout: FrameLayout

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("Users").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val displayName = documentSnapshot.getString("Nombre")
                    toolbar.title = "Bienvenido $displayName"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.welcome)

        frameLayout = findViewById(R.id.appLayout)

        binding.navigationLayout.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.prestamo -> (replaceFragment(Prestamo()))
                R.id.ahorro -> replaceFragment(Ahorro())
                R.id.cuota -> replaceFragment(Cuota())
                R.id.personal -> replaceFragment(Personal())
            }
            true
        }

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.appLayout, fragment)
        fragmentTransaction.commit()
    }
}