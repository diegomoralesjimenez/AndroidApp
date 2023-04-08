package com.example.proyecto1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var store: FirebaseFirestore
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        store = FirebaseFirestore.getInstance()
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            val email: String = usernameEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this@HomeActivity, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this@HomeActivity, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Authentication success.",
                            Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        if (user != null) {
                            checkUserAccessLevel(user.uid)
                        }
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.green)
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)

    }

    private fun checkUserAccessLevel(uid: String) {
        val docRef = store.collection("Users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val role = documentSnapshot.getString("Role")
                if (!TextUtils.isEmpty(role)) {
                    when (role) {
                        "Admin" -> {
                            val intent = Intent(this@HomeActivity, Admin::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "Client" -> {
                            val intent = Intent(this@HomeActivity, ClientActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(
                                this@HomeActivity,
                                "Unknown user role: $role",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "User role not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@HomeActivity,
                    "User document not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@HomeActivity,
                "Error checking user role: " + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}