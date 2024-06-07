package com.example.designbuildproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val historyButton = findViewById<Button>(R.id.historyButton)
        val messageButton = findViewById<Button>(R.id.messageButton)
        val logoutLink = findViewById<TextView>(R.id.logoutLink)

        uploadButton.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        messageButton.setOnClickListener {
            Toast.makeText(this, "Message button clicked", Toast.LENGTH_SHORT).show()
        }

        logoutLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
