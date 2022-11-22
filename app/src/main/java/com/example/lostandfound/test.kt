package com.example.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_lost.BSelectImage
import kotlinx.android.synthetic.main.activity_test.*

class test : AppCompatActivity() {
    private var imageList : ArrayList<Uri?>? = null
    private var position = 0
    private var storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        imageList = ArrayList()
        image_switcher?.setFactory { ImageView(applicationContext) }
        BSelectImage.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    pickImageFromGallery()
                }
            }
            else {
                pickImageFromGallery()
            }
        }

        val imgIn = AnimationUtils.loadAnimation(
            this, android.R.anim.slide_in_left)
        image_switcher?.inAnimation = imgIn

        val imgOut = AnimationUtils.loadAnimation(
            this, android.R.anim.slide_out_right)
        image_switcher?.outAnimation = imgOut

        val prev = findViewById<ImageButton>(R.id.bt_previous)
        prev.setOnClickListener {
            if (position > 0){
                position--
                image_switcher.setImageURI(imageList!![position])
            }
            else{
                Toast.makeText(this, "No More images...", Toast.LENGTH_SHORT).show()
            }
        }

        val next = findViewById<ImageButton>(R.id.bt_next)
        next.setOnClickListener {
            if (position < imageList!!.size-1){
                position++
                image_switcher.setImageURI(imageList!![position])
            }
            else{
                Toast.makeText(this, "No More images...", Toast.LENGTH_SHORT).show()
            }
        }
        submit.setOnClickListener{
            val count = imageList!!.size
            if (count>0){
                for (i in 0 until count){
                    val imageRef = storageRef.child("Test/test_$i")
                    imageList!![i]?.let { it1 ->
                        imageRef.putFile(it1)
                            .addOnSuccessListener {
                                Log.d("TAG", "Image uploaded successfully")
                            }
                            .addOnFailureListener {
                                Log.d("TAG", "Image upload failed")
                            }
                    }
                }
            }
        }
    }
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK){

            if (data!!.clipData != null) {
                val count = data.clipData?.itemCount
                if (count != null) {
                    if (count>5){
                        Toast.makeText(this, "Select less than 6 images", Toast.LENGTH_SHORT).show()
                    }
                }
                for (i in 0 until count!!) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageList!!.add(imageUri)
                }
                image_switcher.setImageURI(imageList!![0])
                position = 0

            } else if (data.data != null) {
                val imageUri = data.data
                imageList!!.add(imageUri)
                image_switcher.setImageURI(imageList!![0])
                position = 0
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
