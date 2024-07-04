package com.example.permissionchecker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        val Uname = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.password)
        val loginbtn = findViewById<Button>(R.id.loginButton)
        firebaseAuth = FirebaseAuth.getInstance()
        loginbtn.setOnClickListener {
            val email = Uname.text.toString()
            val password = pass.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this@LoginPage,MainActivity::class.java)
                        startActivity(intent)
                    }else{
                            Toast.makeText(this@LoginPage,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this@LoginPage,"Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
        val signUp = findViewById<TextView>(R.id.signupText)
        signUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }


    }
}