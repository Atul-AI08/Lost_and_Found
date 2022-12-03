package com.example.lostandfound

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MyData : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var foundAdapter : MyAdapter
    private lateinit var data : ArrayList<MyDataClass>
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_data)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        data = arrayListOf()
        foundAdapter = MyAdapter(data)
        recyclerView.adapter = foundAdapter
        eventChangeListener()
    }
    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
        db.collection("Lost_Items")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val cur = Firebase.auth.currentUser?.uid
                    val user = document["user"].toString()
                    val id = document["image_id"].toString()
                    val desc = "Lost: " + document["item"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val type = "L"
                    val imageRef = storageRef.child("Lost_Items/$id/${id}_0")
                    if (user == cur) {
                        imageRef.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            data.add(MyDataClass(imageUrl, id, desc, location, date, type))
                            foundAdapter.notifyDataSetChanged()
                        }.addOnFailureListener {
                            Log.d("abd", "Image download failed")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
        db.collection("Found_Items")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val cur = Firebase.auth.currentUser?.uid
                    val user = document["user"].toString()
                    val id = document["image_id"].toString()
                    val desc = "Found: " + document["item"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val type = "F"
                    val imageRef = storageRef.child("Found_Items/$id/${id}_0")
                    if (user == cur) {
                        imageRef.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            data.add(MyDataClass(imageUrl, id, desc, location, date, type))
                            foundAdapter.notifyDataSetChanged()
                        }.addOnFailureListener {
                            Log.d("abd", "Image download failed")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}