package com.example.lostandfound

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class Lost_Data : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var foundAdapter : CustomAdapter
    private lateinit var data : ArrayList<ItemsViewModel>
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_data)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        data = arrayListOf()
        foundAdapter = CustomAdapter(data)
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
                    val id = document["image_id"].toString()
                    val desc = document["item"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val imageRef = storageRef.child("Lost_Items/$id")
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        data.add(ItemsViewModel(imageUrl, id, desc, location,date))
                        foundAdapter.notifyDataSetChanged()
                        Log.d("abd", "$imageUrl")
                    }.addOnFailureListener {
                        Log.d("abd", "Image download failed")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}