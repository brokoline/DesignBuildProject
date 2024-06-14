package com.example.designbuildproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var uploadAdapter: UploadAdapter
    private lateinit var startDateInput: EditText
    private lateinit var endDateInput: EditText
    private lateinit var fetchLogsButton: Button
    private lateinit var backToDashboard: TextView

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        startDateInput = findViewById(R.id.startDate)
        endDateInput = findViewById(R.id.endDate)
        fetchLogsButton = findViewById(R.id.fetchLogsButton)
        backToDashboard = findViewById(R.id.backToDashboard)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        uploadAdapter = UploadAdapter(listOf()) // Initialize with an empty list
        recyclerView.adapter = uploadAdapter

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

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
        currentUser?.let { user ->
            val uid = user.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/uploads")

            databaseReference.orderByChild("dateTime").startAt(startDate).endAt(endDate).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val uploadList = ArrayList<UploadActivity.UploadData>()
                    for (dataSnapshot in snapshot.children) {
                        val uploadData = dataSnapshot.getValue(UploadActivity.UploadData::class.java)
                        uploadData?.let { uploadList.add(it) }
                    }
                    if (uploadList.isNotEmpty()) {
                        uploadAdapter = UploadAdapter(uploadList)
                        recyclerView.adapter = uploadAdapter
                    } else {
                        Toast.makeText(this@HistoryActivity, "No logs found for the specified date range", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HistoryActivity, "Failed to fetch logs: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
