package com.example.lostandfound

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import kotlinx.android.synthetic.main.activity_claim.*

class Claim : AppCompatActivity() {
    private var imageList : ArrayList<Uri>? = null
    private var position = 0
    private lateinit var db : FirebaseFirestore
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim)
        imageList = ArrayList()
        val id = intent.getStringExtra("id").toString()
        db = FirebaseFirestore.getInstance()
        var user = ""
        image_switcher?.setFactory { ImageView(applicationContext) }

        val imgIn = AnimationUtils.loadAnimation(
            this, android.R.anim.slide_in_left)
        image_switcher?.inAnimation = imgIn

        val imgOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        image_switcher?.outAnimation = imgOut

        val prev = findViewById<ImageButton>(R.id.bt_previous)
        prev.setOnClickListener {
            if (position > 0){
                position--
                Glide.with(this).load(imageList!![position]).into(image_switcher.currentView as ImageView)
            }
            else{
                Toast.makeText(this, "No More images...", Toast.LENGTH_SHORT).show()
            }
        }

        val next = findViewById<ImageButton>(R.id.bt_next)
        next.setOnClickListener {
            if (position < imageList!!.size-1){
                position++
                Glide.with(this).load(imageList!![position]).into(image_switcher.currentView as ImageView)
            }
            else{
                Toast.makeText(this, "No More images...", Toast.LENGTH_SHORT).show()
            }
        }

        db.collection("Found_Items").document(id)
            .get().addOnSuccessListener { document ->
                val item = document["item"].toString()
                val desc = document["description"].toString()
                val location = document["location"].toString()
                val date = document["date"].toString()
                val time = document["time"].toString()
                user = document["user"].toString()
                val imageRef = storageRef.child("Found_Items/$id")
                imageRef.listAll().addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
                    for (file in listResult.items) {
                        file.downloadUrl.addOnSuccessListener { uri ->
                            imageList!!.add(uri)
                            Glide.with(this).load(imageList!![position]).into(image_switcher.currentView as ImageView)
                            Log.d("img", "$uri")
                        }
                    }
                })
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
                            val email = Array(1){uEmail}
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/html"
                            intent.putExtra(Intent.EXTRA_EMAIL, email)
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Claim item found")
                            intent.putExtra(Intent.EXTRA_TEXT, "It is to state you that the item you have found is mine. Please contact me so that I can get it back.\nThanks a lot")
                            startActivity(Intent.createChooser(intent, "Send Email"))
                        }
                    }
                }
        }
    }
}