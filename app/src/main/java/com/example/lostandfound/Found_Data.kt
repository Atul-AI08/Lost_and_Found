package com.example.lostandfound

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class Found_Data : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var foundAdapter : FoundAdapter
    private lateinit var data : ArrayList<ItemsViewModel>
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found_data)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        data = arrayListOf()
        foundAdapter = FoundAdapter(data)
        recyclerView.adapter = foundAdapter
        eventChangeListener()
    }
    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
        db.collection("Found_Items")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val id = document["image_id"].toString()
                    val desc = document["item"].toString()
                    val location = document["location"].toString()
                    val date = document["date"].toString()
                    val imageRef = storageRef.child("Found_Items/$id/${id}_0")
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                        data.add(ItemsViewModel(imageUrl, id, desc, location,date))
                        foundAdapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                        Log.d("abd", "Image download failed")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}