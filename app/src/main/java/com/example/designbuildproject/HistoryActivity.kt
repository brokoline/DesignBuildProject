package com.example.designbuildproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val startDateInput = findViewById<EditText>(R.id.startDate)
        val endDateInput = findViewById<EditText>(R.id.endDate)
        val fetchLogsButton = findViewById<Button>(R.id.fetchLogsButton)
        val backToDashboard = findViewById<TextView>(R.id.backToDashboard)

        fetchLogsButton.setOnClickListener {
            val startDate = startDateInput.text.toString()
            val endDate = endDateInput.text.toString()

            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                fetchLogs(startDate, endDate)
            } else {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            }
        }

        backToDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun fetchLogs(startDate: String, endDate: String) {
        // Replace with your logic to fetch logs from the server or database
        Toast.makeText(this, "Fetching logs from $startDate to $endDate", Toast.LENGTH_SHORT).show()
    }
}
