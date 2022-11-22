package com.example.lostandfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FoundItem : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found_item)
        val id = intent.getStringExtra("id").toString()
        var user = ""
        db = FirebaseFirestore.getInstance()
        db.collection("Lost_Items").document(id)
            .get().addOnSuccessListener { document ->
                val item = document["item"].toString()
                val desc = document["description"].toString()
                val location = document["location"].toString()
                val date = document["date"].toString()
                val time = document["time"].toString()
                user = document["user"].toString()
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
            db.collection("Users")
                .get().addOnSuccessListener{
                    for (document in it) {
                        val uid = document["uId"].toString()
                        val uEmail = document["email"].toString()
                        if (user == uid){
                            val email = Array<String>(1){uEmail}
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/html"
                            intent.putExtra(Intent.EXTRA_EMAIL, email)
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Found your lost item")
                            intent.putExtra(Intent.EXTRA_TEXT, "It is to state you that I have found your lost item. Please contact me to claim it.")
                            startActivity(Intent.createChooser(intent, "Send Email"))
                        }
                    }

                }
        }
    }
}