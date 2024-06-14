package com.example.designbuildproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        //val descriptionInput = findViewById<EditText>(R.id.description)
        filePickerButton = findViewById<Button>(R.id.filePickerButton)
        uploadButton = findViewById<Button>(R.id.uploadButton)
        descriptionInput = findViewById(R.id.editTextComments)


        val backToDashboard = findViewById<TextView>(R.id.backToDashboard)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser


        filePickerButton.setOnClickListener {
            selectImage()
        }

        uploadButton.setOnClickListener {
            uploadImage()
        }


    }



    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageFileUri = data?.data
            imageView.setImageURI(imageFileUri)

        }
    }



    private fun uploadImage() {
        if (imageFileUri != null) {
            currentUser?.let { user ->
                val uid = user.uid
                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val currentDateTime = dateFormat.format(Date())
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
                                currentDateTime,
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
        val comment: String,
        val dateTime: String,
        val storagePath: String,
        val downloadUrl: String
    )
}









     /*
        val file = Uri.fromFile(File(imageUri.path.toString()))
        Toast.makeText(this, imageUri.path, Toast.LENGTH_SHORT).show()
        val imageRef = storageRef.child(imageFileUri.toString())
        var uploadTask = imageRef.putFile(file)
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        }

*/


