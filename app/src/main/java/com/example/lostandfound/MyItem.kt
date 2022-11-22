package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyItem : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_item)
        val id = intent.getStringExtra("id").toString()
        val type = intent.getStringExtra("type").toString()
        db = FirebaseFirestore.getInstance()
        if (type == "L"){
            db.collection("Lost_Items").document(id)
                .get().addOnSuccessListener { document ->
                    val item = document["item"].toString()
                    val desc = document["description"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val time = document["time"].toString()
                    val image = findViewById<ImageView>(R.id.IVPreviewImage)
                    val imageRef = storageRef.child("Lost_Items/$id")
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        Glide.with(this).load(imageUrl).into(image)
                    }.addOnFailureListener {
                        Log.d("TAG", "Image download failed")
                    }
                    findViewById<TextView>(R.id.textView2).text = item
                    findViewById<TextView>(R.id.textView4).text = location
                    findViewById<TextView>(R.id.textView7).text = desc
                    findViewById<TextView>(R.id.textView9).text = date
                    findViewById<TextView>(R.id.textView11).text = time
                }

            findViewById<Button>(R.id.submit).setOnClickListener {
                db.collection("Lost_Items").document(id).delete().addOnSuccessListener {
                    Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Try Again Later", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            db.collection("Found_Items").document(id)
                .get().addOnSuccessListener { document ->
                    val item = document["item"].toString()
                    val desc = document["description"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val time = document["time"].toString()
                    val image = findViewById<ImageView>(R.id.IVPreviewImage)
                    val imageRef = storageRef.child("Found_Items/$id")
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        Glide.with(this).load(imageUrl).into(image)
                    }.addOnFailureListener {
                        Log.d("TAG", "Image download failed")
                    }
                    findViewById<TextView>(R.id.textView2).text = item
                    findViewById<TextView>(R.id.textView4).text = location
                    findViewById<TextView>(R.id.textView7).text = desc
                    findViewById<TextView>(R.id.textView9).text = date
                    findViewById<TextView>(R.id.textView11).text = time
                }

            findViewById<Button>(R.id.submit).setOnClickListener {
                db.collection("Found_Items").document(id).delete().addOnSuccessListener {
                    Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Try Again Later", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}