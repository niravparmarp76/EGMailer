package com.example.egmailer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton

class UserProfile : AppCompatActivity() {
    private lateinit var txt_PersonName: TextView
    private lateinit var txt_PersonID: TextView
    private lateinit var txt_Email: TextView
    private lateinit var btn_SignOut: MaterialButton

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        txt_PersonName = findViewById(R.id.txt_PersonName)
        txt_PersonID = findViewById(R.id.txt_PersonID)
        txt_Email = findViewById(R.id.txt_Email)
        btn_SignOut = findViewById(R.id.btn_SignOut)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            txt_PersonName.text = acct.displayName ?: "N/A"
            txt_Email.text = acct.email ?: "N/A"
            txt_PersonID.text = acct.id ?: "N/A"
        } else {
            Toast.makeText(this, "No account found", Toast.LENGTH_SHORT).show()
        }

        btn_SignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener(this) {
            Toast.makeText(
                applicationContext,
                "Signed Out",
                Toast.LENGTH_LONG
            ).show()
        }

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}