package com.example.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build.*
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_found.*
import kotlinx.android.synthetic.main.activity_found.BSelectImage
import kotlinx.android.synthetic.main.activity_found.IVPreviewImage
import kotlinx.android.synthetic.main.activity_found.submit
import kotlinx.android.synthetic.main.activity_lost.*

class FoundActivity : AppCompatActivity() {
    private var image: Uri? = null
    private var storageRef = FirebaseStorage.getInstance().reference
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var productId = ""
    @RequiresApi(VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)

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
            else{
                pickImageFromGallery()
            }
        }
        var date = ""
        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH))
        { _, year, month, day ->
            date = "$day/$month/$year"
        }

        var time = ""
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setOnTimeChangedListener{_,hour,minute ->
            time = "$hour : $minute"
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
            db.collection("Found_Items").add(lost)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this,"Item added successfully",Toast.LENGTH_SHORT).show()
                    productId = documentReference.id
                    val imageRef = storageRef.child("Found_Items/$productId/${productId}")
                    imageRef.putFile(image!!)
                        .addOnSuccessListener {
                            Log.d("TAG", "Image uploaded successfully")
                        }
                        .addOnFailureListener {
                            val url = storageRef.child("Lost_Items/$productId/${productId}").downloadUrl
                            val data = HashMap<String, Any>()
                            data["image_id"] = url.toString()
                            db.collection("Lost_Items").document(productId).update(data)
                            Log.d("TAG", "Image upload failed")
                        }
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error adding item",Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            image = data?.data
            IVPreviewImage.setImageURI(data?.data)

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