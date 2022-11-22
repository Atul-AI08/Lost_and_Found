package com.example.lostandfound

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import kotlinx.android.synthetic.main.activity_test2.image_switcher


class Test2 : AppCompatActivity() {
    private var imageList : ArrayList<Uri>? = null
    private var position = 0
    private var storageRef = FirebaseStorage.getInstance().reference.child("Test")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        imageList = ArrayList()
        image_switcher?.setFactory { ImageView(applicationContext) }
        storageRef.listAll().addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
            for (file in listResult.items) {
                file.downloadUrl.addOnSuccessListener { uri ->
                    imageList!!.add(uri)
                    Glide.with(this).load(imageList!![position]).into(image_switcher.currentView as ImageView)
                    Log.d("img", "$uri")
                }
            }
        })
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

    }
}