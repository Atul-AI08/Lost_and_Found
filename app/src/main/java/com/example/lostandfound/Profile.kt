package com.example.lostandfound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val id = Firebase.auth.currentUser.toString()
        db = FirebaseFirestore.getInstance()
        db.collection("Users").get().addOnSuccessListener {
            val user = Firebase.auth.currentUser?.uid.toString()
            for (document in it) {
                val name = "Name: " + document["Name"].toString()
                val roll = "Roll No.: " + document["Roll No"].toString()
                val email = "Email: " + document["email"].toString()
                val phone = "Phone: " + document["Phone"].toString()
                val uid = document["uId"].toString()
                val imgId = document["image_id"].toString()
                if (user == uid) {
                    findViewById<TextView>(R.id.textView2).text = name
                    findViewById<TextView>(R.id.textView3).text = roll
                    findViewById<TextView>(R.id.textView4).text = email
                    findViewById<TextView>(R.id.textView5).text = phone
                    val imageRef = storageRef.child("Users/$imgId")
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        Glide.with(this).load(imageUrl).into(IVPreviewImage)
                    }.addOnFailureListener {
                        Log.d("abd", "Image download failed")
                    }
                }
            }
        }
    }
}