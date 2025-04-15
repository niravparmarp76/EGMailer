package com.example.egmailer

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.egmailer.util.SessionManager
import com.google.android.material.button.MaterialButton

class UserProfile : AppCompatActivity() {
    private lateinit var txt_PersonName: TextView
    private lateinit var txt_PersonID: TextView
    private lateinit var txt_Email: TextView
    private lateinit var btn_SignOut: MaterialButton

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        initView()
        loadComponents()
        setClickEvent()
    }

    private fun initView() {
        txt_PersonName = findViewById(R.id.txt_PersonName)
        txt_PersonID = findViewById(R.id.txt_PersonID)
        txt_Email = findViewById(R.id.txt_Email)
        btn_SignOut = findViewById(R.id.btn_SignOut)

        sessionManager = SessionManager(this)
    }

    private fun loadComponents() {
        val name = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()
        val token = sessionManager.getUserToken()

        if (!name.isNullOrEmpty() && !email.isNullOrEmpty()) {
            txt_PersonName.text = name
            txt_Email.text = email
            txt_PersonID.text = token
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        sessionManager.clearSession()

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setClickEvent() {
        btn_SignOut.setOnClickListener {
            signOut()
        }
    }
}