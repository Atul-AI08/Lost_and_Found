package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val lost = findViewById<Button>(R.id.button1)
        val found = findViewById<Button>(R.id.button2)
        val lost_database = findViewById<Button>(R.id.button3)

        lost.setOnClickListener{
            val intent = Intent(this, LostActivity::class.java)
            startActivity(intent)
        }
        found.setOnClickListener{
            val intent = Intent(this, FoundActivity::class.java)
            startActivity(intent)
        }
        lost_database.setOnClickListener {
            val intent = Intent(this, Lost_Data::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.button4).setOnClickListener{
            val intent = Intent(this, Found_Data::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.button5).setOnClickListener {
            val intent = Intent(this, MyData::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.button6).setOnClickListener {
            val intent = Intent(this, Test2::class.java)
            startActivity(intent)
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.profile -> {
            val id = Firebase.auth.currentUser.toString()
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}