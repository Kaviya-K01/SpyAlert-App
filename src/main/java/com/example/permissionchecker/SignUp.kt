package com.example.permissionchecker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()
        val signUpbtn = findViewById<Button>(R.id.signupButton)
        val Uname = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.password)
        val Cpass = findViewById<EditText>(R.id.Confirm_password)
        signUpbtn.setOnClickListener {
            val email = Uname.text.toString()
            val password = pass.text.toString()
            val Cpassword = Cpass.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()&&Cpassword.isNotEmpty()){
                if (password == Cpassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent = Intent(this@SignUp,LoginPage::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@SignUp,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this@SignUp,"Password is not matching",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@SignUp,"Empty fields are not allowed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}