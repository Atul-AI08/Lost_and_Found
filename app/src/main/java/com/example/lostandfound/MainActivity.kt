package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectLogin: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etEmail = findViewById(R.id.email)
        etPass = findViewById(R.id.pass)
        btnSignUp = findViewById(R.id.signup)
        tvRedirectLogin = findViewById(R.id.signin)
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
        }

        tvRedirectLogin.setOnClickListener {
            login()
        }
        findViewById<TextView>(R.id.forgot).setOnClickListener {
            auth.sendPasswordResetEmail(etEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Account not activated", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }
    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "Log In failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun signUpUser() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }
}
