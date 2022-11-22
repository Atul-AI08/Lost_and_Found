package com.example.lostandfound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class Profile : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val id = Firebase.auth.currentUser.toString()
        db = FirebaseFirestore.getInstance()
        db.collection("Users").document(id).get().addOnSuccessListener { document ->
            val name = "Name: " + document["name"].toString()
            val roll = "Roll No.: " + document["roll no."].toString()
            val email = "Email: " + document["email"].toString()
            val phone = "Phone: " + document["phone"].toString()
            findViewById<TextView>(R.id.textView2).text = name
            findViewById<TextView>(R.id.textView3).text = roll
            findViewById<TextView>(R.id.textView4).text = email
            findViewById<TextView>(R.id.textView5).text = phone
        }
    }
}