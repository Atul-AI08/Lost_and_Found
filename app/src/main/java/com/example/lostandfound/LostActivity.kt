package com.example.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build.*
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_lost.*
import java.util.*
import kotlin.collections.HashMap


class LostActivity : AppCompatActivity() {
    private var imageList : ArrayList<Uri?>? = null
    private var position = 0
    private var storageRef = FirebaseStorage.getInstance().reference
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var productId = ""
    @RequiresApi(VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)
        imageList = ArrayList()
        image_switcher?.setFactory { ImageView(applicationContext) }
        BSelectImage.setOnClickListener{
            if (VERSION.SDK_INT >= VERSION_CODES.M){
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
        var date = getCurrentDate()
        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH))
        { _, year, month, day ->
            date = "$day/$month/$year"
        }

        var time = getCurrentTime()
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setOnTimeChangedListener{_,hour,minute ->
            time = "$hour : $minute"
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
                Toast.makeText(this, "No More images", Toast.LENGTH_SHORT).show()
            }
        }

        val next = findViewById<ImageButton>(R.id.bt_next)
        next.setOnClickListener {
            if (position < imageList!!.size-1){
                position++
                image_switcher.setImageURI(imageList!![position])
            }
            else{
                Toast.makeText(this, "No More images", Toast.LENGTH_SHORT).show()
            }
        }
        submit.setOnClickListener{
            val item = findViewById<EditText>(R.id.item).text.toString()
            val desc = findViewById<EditText>(R.id.desc).text.toString()
            val lost = HashMap<String, Any>()
            val auth = FirebaseAuth.getInstance().currentUser!!.uid
            val location = findViewById<EditText>(R.id.loc).text.toString()
            lost["item"] = item
            lost["location"] = location
            lost["description"] = desc
            lost["date"] = date
            lost["time"] = time
            lost["user"] = auth
            db.collection("Lost_Items").add(lost)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this,"Item added successfully",Toast.LENGTH_SHORT).show()
                    productId = documentReference.id
                    val data = HashMap<String, Any>()
                    data["image_id"] = productId
                    db.collection("Lost_Items").document(productId).update(data)
                    val count = imageList!!.size
                    if (count>0){
                        for (i in 0 until count){
                            val imageRef = storageRef.child("Lost_Items/${productId}/${productId}_$i")
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
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Try again later",Toast.LENGTH_SHORT).show()
                }
        }
    }

    @RequiresApi(VERSION_CODES.N)
    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return "$day/$month/$year"
    }
    @RequiresApi(VERSION_CODES.N)
    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return "$hour:$minute"
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
                for (i in 0 until count!!) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imageList!!.add(imageUri)
                    Log.d("img", "$imageUri")
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