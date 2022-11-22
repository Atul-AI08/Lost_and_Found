package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        etEmail = findViewById(R.id.email)
        etPass = findViewById(R.id.pass)
        findViewById<Button>(R.id.signup).setOnClickListener {
            val name = findViewById<EditText>(R.id.name).text.toString()
            val phone = findViewById<EditText>(R.id.phone).text.toString()
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val cnfpass = findViewById<EditText>(R.id.cnfpass).text.toString()
            val data = hashMapOf<String, Any>()
            data["Name"] = name
            data["Roll No"] = roll
            data["Phone"] = phone
            data["email"] = etEmail
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            }
            else if (pass != cnfpass)
                Toast.makeText(this, "Password and Confirm Password are not same", Toast.LENGTH_SHORT).show()
            else {
                signUpUser()
                val cur = Firebase.auth.currentUser?.uid
                data["uId"] = cur.toString()
                db.collection("Users").add(data)
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }
    }
    private fun signUpUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}