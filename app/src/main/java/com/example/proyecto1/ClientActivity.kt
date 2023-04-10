package com.example.proyecto1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

    //Fragment Navigation
    private lateinit var binding: ActivityClientBinding
    private lateinit var toolbar: Toolbar

    //Variables
    private lateinit var frameLayout: FrameLayout
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
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase
        auth = FirebaseAuth.getInstance()

        //Toolbar inicializacion
        toolbar = findViewById(R.id.welcome)

        //Fragment inicializacion
        frameLayout = findViewById(R.id.appLayout)

        //Changes fragment depending on selected menu
        binding.navigationLayout.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.prestamo -> (replaceFragment(Prestamo()))
                R.id.ahorro -> replaceFragment(Ahorro())
                R.id.cuota -> replaceFragment(Cuota())
                R.id.personal -> replaceFragment(Personal())
            }
            true
        }

        //Logout button
        logout = findViewById(R.id.btn_logout)

        //Logout when user clicks
        binding.btnLogout.setOnClickListener {
            //Firebase signout
            auth.signOut()
            auth.removeAuthStateListener(authStateListener)
            //Changes to login activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Add default fragment
        replaceFragment(Prestamo())
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