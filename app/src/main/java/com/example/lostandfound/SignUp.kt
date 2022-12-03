package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUp : AppCompatActivity() {
    private lateinit var etEmail : String
    private lateinit var etPass : String
    private lateinit var etCnfPass : String
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = Firebase.auth
        findViewById<Button>(R.id.signup).setOnClickListener {
            val email = findViewById<EditText>(R.id.email).text.toString()
            val pass = findViewById<EditText>(R.id.pass).text.toString()
            val cnfpass = findViewById<EditText>(R.id.cnfpass).text.toString()
            etEmail = email
            etPass = pass
            etCnfPass = cnfpass
            signUpUser()
        }
    }
    private fun isValidEmail(toCheck: String): Boolean {
        val regex = "[a-zA-Z0-9._-]+@iitp.ac.in".toRegex()
        return toCheck.matches(regex)
    }
    private fun signUpUser() {
        val email = etEmail
        val pass = etPass
        val cnfpass = etCnfPass
        Log.d("xyz", "$email $pass $cnfpass")
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
        }
        else if(!isValidEmail(email)){
            Toast.makeText(this, "Enter your IITP mail", Toast.LENGTH_SHORT).show()
        }
        else if (pass != cnfpass)
            Toast.makeText(this, "Password and Confirm Password are not same", Toast.LENGTH_SHORT).show()
        else {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileDetails::class.java)
                    startActivity(intent)
                } else {
                    Log.d("xyz", "error")
                    Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}