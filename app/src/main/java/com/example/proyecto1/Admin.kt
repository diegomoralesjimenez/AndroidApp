package com.example.proyecto1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.proyecto1.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Admin : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    private lateinit var toolbar: Toolbar
    private lateinit var logout: Button

    //Firebase
    private lateinit var auth: FirebaseAuth

    //Toolbar with User's name
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("Users").document(user.uid)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // handle errors
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val displayName = snapshot.getString("Nombre")
                    toolbar.title = "Bienvenido $displayName"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase
        auth = FirebaseAuth.getInstance()

        binding.navigationLayout.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cliente -> (replaceFragment(NewClient()))
                R.id.prestamo -> replaceFragment(AsignarPrestamo())
            }
            true
        }

        //Logout button
        logout = findViewById(R.id.sign_out_button)

        //Logout when user clicks
        binding.signOutButton.setOnClickListener {
            //Firebase signout
            auth.signOut()
            auth.removeAuthStateListener(authStateListener)
            //Changes to login activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Add default fragment
        replaceFragment(NewClient())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.adminLayout, fragment)
        fragmentTransaction.commit()
    }

}