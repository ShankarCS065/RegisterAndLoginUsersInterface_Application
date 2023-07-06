package com.example.registerandloginusers

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var  auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth= FirebaseAuth.getInstance()


        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            registerUser()
        }
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            longinUser()
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile(){
        auth.currentUser?.let {user ->
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val photoURI= Uri.parse("android.resource://$packageName/${R.drawable.mst}")
            val profileUpdates=UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"Successfully updated user profile",Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun registerUser(){
val email=findViewById<EditText>(R.id.etGmail).text.toString()
        val password=findViewById<EditText>(R.id.etPassword).text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }


    private fun longinUser(){
        val email=findViewById<EditText>(R.id.etEmail1).text.toString()
        val password=findViewById<EditText>(R.id.etPassword1).text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }


    private fun checkLoggedInState(){
        val tvFirst=findViewById<TextView>(R.id.tvShowChange)
        val user=auth.currentUser
        if(user==null){
            tvFirst.text="you are not logged in"
        }
        else{
            tvFirst.text="You are logged in"
            findViewById<EditText>(R.id.etUsername).setText(user.displayName)
            findViewById<ImageView>(R.id.ivProfile).setImageURI(user.photoUrl)
        }
    }

}




