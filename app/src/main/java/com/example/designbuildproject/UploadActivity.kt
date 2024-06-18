package com.example.designbuildproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var imageFileUri: Uri? = null
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var imageView: ImageView
    private lateinit var filePickerButton: Button
    private lateinit var uploadButton: Button
    private lateinit var descriptionInput: EditText

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        firebaseRef = FirebaseDatabase.getInstance().getReference("comment")
        imageView = findViewById(R.id.imageView)
        filePickerButton = findViewById(R.id.filePickerButton)
        uploadButton = findViewById(R.id.uploadButton)
        descriptionInput = findViewById(R.id.editTextComments)
        val backToDashboard = findViewById<TextView>(R.id.backToDashboard)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageFileUri = result.data?.data
                imageView.setImageURI(imageFileUri)
            }
        }

        filePickerButton.setOnClickListener {
            selectImage()
        }

        uploadButton.setOnClickListener {
            uploadImage()
        }

        backToDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        filePickerLauncher.launch(intent)
    }

    private fun uploadImage() {
        if (imageFileUri != null) {
            currentUser?.let { user ->
                val uid = user.uid
                val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val dateTimeFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val currentDateTime = dateTimeFormat.format(Date())
                val fileName = "$currentDateTime.jpg"
                val storagePath = "user/$uid/images/$fileName"
                val storageReference: StorageReference = FirebaseStorage.getInstance().getReference(storagePath)

                storageReference.putFile(imageFileUri!!)
                    .addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            val comment = descriptionInput.text.toString()
                            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$uid/uploads")
                            val uploadId = databaseReference.push().key

                            val uploadData = UploadData(
                                comment,
                                currentDate,
                                storagePath,
                                uri.toString()
                            )

                            uploadId?.let {
                                databaseReference.child(it).setValue(uploadData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Image and comment uploaded successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Failed to upload comment: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        Log.e("UploadError", "Failed to upload comment", exception)
                                    }
                            } ?: run {
                                Toast.makeText(this, "Failed to generate database reference", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                            Log.e("UploadError", "Failed to get download URL", exception)
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        Log.e("UploadError", "Image upload failed", exception)
                    }
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    data class UploadData(
        val comment: String = "",
        val date: String = "",
        val storagePath: String = "",
        val downloadUrl: String = ""
    )
}
