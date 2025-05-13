package com.example.smartgrocer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addButton = findViewById<Button>(R.id.btnGoToAdd)
        val listButton = findViewById<Button>(R.id.btnGoToList)

        addButton.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        listButton.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }
    }
}
