package com.example.designbuildproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        val emailInput = findViewById<EditText>(R.id.email)
        val passInput = findViewById<EditText>(R.id.pass)
        val confirmPassInput = findViewById<EditText>(R.id.confirmPass)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLogin = findViewById<TextView>(R.id.backToLogin) // Make sure this matches the id in XML

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val pass = passInput.text.toString()
            val confirmPass = confirmPassInput.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {

                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            Toast.makeText(
                                baseContext,
                                "Registration complete, please sign in.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val user = auth.currentUser
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Registration failed, user already exists or password is too short.",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }

        }



        }
        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) // Add this line to handle click event
        }
    }

    }


