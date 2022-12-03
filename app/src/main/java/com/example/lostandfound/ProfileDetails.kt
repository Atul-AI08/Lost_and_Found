package com.example.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile_details.*

class ProfileDetails : AppCompatActivity() {
    private var image: Uri? = null
    private var storageRef = FirebaseStorage.getInstance().reference
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)
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
        findViewById<Button>(R.id.submit).setOnClickListener{
            val name = findViewById<EditText>(R.id.name).text.toString()
            val roll = findViewById<EditText>(R.id.roll).text.toString()
            val phone = findViewById<EditText>(R.id.phone).text.toString()
            val email = findViewById<EditText>(R.id.email).text.toString()
            val cur = Firebase.auth.currentUser?.uid
            if(name.isEmpty()){
                Toast.makeText(this,"Enter your name",Toast.LENGTH_SHORT).show()
            }
            else if(roll.isEmpty()){
                Toast.makeText(this,"Enter your roll no.",Toast.LENGTH_SHORT).show()
            }
            else if(email.isEmpty()){
                Toast.makeText(this,"Enter your email",Toast.LENGTH_SHORT).show()
            }
            else if(!isNumeric(phone)){
                Toast.makeText(this, "Enter correct phone no.", Toast.LENGTH_SHORT).show()
            }
            else{
                val data = hashMapOf<String, Any>()
                data["Name"] = name
                data["Roll No"] = roll
                data["Phone"] = phone
                data["email"] = email
                data["uId"] = cur.toString()
                db.collection("Users").add(data).addOnSuccessListener { documentReference ->
                    val profileId = documentReference.id
                    val addition = hashMapOf<String, Any>()
                    addition["image_id"] = profileId
                    db.collection("Users").document(profileId).update(addition)
                    val imageRef = storageRef.child("Users/$profileId")
                    imageRef.putFile(image!!).addOnSuccessListener {
                        Log.d("TAG", "Image uploaded successfully")
                    }.addOnFailureListener {
                        Log.d("TAG", "Image upload failed")
                    }
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this,"Try again later",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun isNumeric(toCheck: String): Boolean {
        val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
        return toCheck.matches(regex)
    }
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
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