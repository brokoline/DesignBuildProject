package com.example.designbuildproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userInput = findViewById<EditText>(R.id.user)
        val emailInput = findViewById<EditText>(R.id.email)
        val passInput = findViewById<EditText>(R.id.pass)
        val confirmPassInput = findViewById<EditText>(R.id.confirmPass)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLogin = findViewById<TextView>(R.id.backToLogin) // Make sure this matches the id in XML

        registerButton.setOnClickListener {
            val user = userInput.text.toString()
            val email = emailInput.text.toString()
            val pass = passInput.text.toString()
            val confirmPass = confirmPassInput.text.toString()

            if (user.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    addUser(user, email, pass)
                } else {
                    Toast.makeText(this, "Kodeordene matcher ikke. Prøv igen.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Udfyld venligst alle felter.", Toast.LENGTH_SHORT).show()
            }
        }

        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) // Add this line to handle click event
        }
    }

    private fun addUser(user: String, email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject().apply {
                put("name", user)
                put("email", email)
                put("pass", pass)
            }

            val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://yourapiendpoint.com/api/user") // API endpoint
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Bruger oprettet med succes", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(this@RegisterActivity, "Der opstod en fejl ved oprettelse af brugeren.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
