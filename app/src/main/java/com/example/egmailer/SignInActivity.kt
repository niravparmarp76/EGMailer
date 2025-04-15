package com.example.egmailer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.egmailer.api.ApiInterface
import com.example.egmailer.api.ApiServiceCall
import com.example.egmailer.request.AuthLoginRequest
import com.example.egmailer.response.AuthLoginResponse
import com.example.egmailer.util.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class SignInActivity : AppCompatActivity() {
    private lateinit var edt_Email: TextInputEditText
    private lateinit var edt_Password: TextInputEditText
    private lateinit var btn_Login: MaterialButton
    private lateinit var btn_GoogleSignIn: SignInButton
    private lateinit var txt_Register: TextView

    val TAG = "SignInActivity"
    private val RC_SIGN_IN = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initializeView()
        loadComponents()
        setClickEvent()
    }

    override fun onResume() {
        super.onResume()
        isAlreadyLoggedIn()
    }

    private fun initializeView() {
        edt_Email = findViewById(R.id.edt_Email)
        edt_Password = findViewById(R.id.edt_Password)
        btn_Login = findViewById(R.id.btn_Login)
        btn_GoogleSignIn = findViewById(R.id.btn_GoogleSignIn)
        txt_Register = findViewById(R.id.txt_Register)
    }

    private fun loadComponents() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setClickEvent() {
        btn_Login.setOnClickListener {
            val email = edt_Email.text.toString().trim()
            val password = edt_Password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = AuthLoginRequest(email, password)
                login(loginRequest)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_GoogleSignIn.setOnClickListener {
            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        txt_Register.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val email = account.email.toString().trim()
                val password = account.id.toString().trim()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    val loginRequest = AuthLoginRequest(email, password)
                    login(loginRequest)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google Sign-In failed: ${e.statusCode} - ${e.message}")
            }
        }
    }

    private fun login(loginRequest: AuthLoginRequest) {
        Log.d(TAG, "login: ")
        val service: ApiInterface = ApiServiceCall.getClient().create(ApiInterface::class.java)
        val observable: Observable<AuthLoginResponse> = service.authLogin(loginRequest)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<AuthLoginResponse> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(authLoginResponse: AuthLoginResponse) {
                    Log.d(TAG, "onNext: " + authLoginResponse.getMessage())
                    if (authLoginResponse.getMessage() == "User not found") {
                        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                        intent.putExtra("email", loginRequest.email)
                        intent.putExtra("password", loginRequest.password)
                        startActivity(intent)
                    } else {
                        val sessionManager = SessionManager(this@SignInActivity)
                        sessionManager.saveUserSession(
                            authLoginResponse.getToken(),
                            authLoginResponse.getName(),
                            authLoginResponse.getEmail()
                        )

                        val intent = Intent(this@SignInActivity, UserProfile::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val message = try {
                            JSONObject(errorBody.toString()).getString("message")
                        } catch (jsonEx: Exception) {
                            "Unexpected error"
                        }

                        Log.d(TAG, "onError: $message")

                        if (message == "User not found") {
                            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                            intent.putExtra("email", loginRequest.email)
                            intent.putExtra("password", loginRequest.password)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignInActivity, e.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {}
            })
    }

    private fun isAlreadyLoggedIn() {
        val sessionManager = SessionManager(this)
        val name = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()
        val token = sessionManager.getUserToken()

        Log.d(TAG, "Name: $name, Email: $email, Token: $token")

        if (!name.isNullOrEmpty() && !email.isNullOrEmpty() && !token.isNullOrEmpty()) {
            startActivity(Intent(this, UserProfile::class.java))
            finish()
            return
        }
    }
}