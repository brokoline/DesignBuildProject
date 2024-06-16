package com.example.designbuildproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private var imageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val descriptionInput = findViewById<EditText>(R.id.description)
        val filePickerButton = findViewById<Button>(R.id.filePickerButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val backToDashboard = findViewById<TextView>(R.id.backToDashboard)

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageFileUri = result.data?.data
                Toast.makeText(this, "File selected: ${imageFileUri?.path}", Toast.LENGTH_SHORT).show()
            }
        }

        filePickerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            filePickerLauncher.launch(intent)
        }

        uploadButton.setOnClickListener {
            val description = descriptionInput.text.toString()
            if (imageFileUri != null && description.isNotEmpty()) {
                uploadImage(description, imageFileUri!!)
            } else {
                Toast.makeText(this, "Please select an image and enter a description", Toast.LENGTH_SHORT).show()
            }
        }

        backToDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun uploadImage(description: String, imageUri: Uri) {
        val file = File(imageUri.path) // You might need a better way to get the file from Uri
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("description", description)
            .addFormDataPart("image", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url("http://EpiLogger.com/api/file") // API endpoint
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                runOnUiThread {
                    Toast.makeText(this, "Billede er logget i EpiLogger", Toast.LENGTH_SHORT).show()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Fejl under upload", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
