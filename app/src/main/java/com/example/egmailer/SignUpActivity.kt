package com.example.egmailer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.egmailer.api.ApiInterface
import com.example.egmailer.api.ApiServiceCall
import com.example.egmailer.request.AuthRegisterRequest
import com.example.egmailer.response.AuthRegisterResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
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

class SignUpActivity : AppCompatActivity() {
    private lateinit var edt_Name: TextInputEditText
    private lateinit var edt_Email: TextInputEditText
    private lateinit var edt_Password: TextInputEditText
    private lateinit var btn_SignUp: MaterialButton
    private lateinit var btn_GoogleSignUp: SignInButton

    private val TAG = "SignUpActivity"
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_UP = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initializeView()
        loadComponents()
        setClickEvent()
    }

    private fun initializeView() {
        edt_Name = findViewById(R.id.edt_Name)
        edt_Email = findViewById(R.id.edt_Email)
        edt_Password = findViewById(R.id.edt_Password)
        btn_SignUp = findViewById(R.id.btn_SignUp)
        btn_GoogleSignUp = findViewById(R.id.btn_GoogleSignUp)
    }

    private fun loadComponents() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        for (i in 0 until btn_GoogleSignUp.childCount) {
            val view = btn_GoogleSignUp.getChildAt(i)
            if (view is android.widget.TextView) {
                view.text = "Sign up with Google"
                break
            }
        }

        val intent = intent
        if (intent != null) {
            val email = intent.getStringExtra("email")
            val password = intent.getStringExtra("password")
            edt_Email.setText(email)
            edt_Password.setText(password)
        }
    }

    private fun signUp() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_UP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val name = account.displayName
                val email = account.email

                Toast.makeText(this, "Signed up as $name\n$email", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: ApiException) {
                Log.w("SignUpActivity", "signUpResult:failed code=${e.statusCode}")
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register(registerRequest: AuthRegisterRequest) {
        val service: ApiInterface = ApiServiceCall.getClient().create(ApiInterface::class.java)
        val observable: Observable<AuthRegisterResponse> = service.authRegister(registerRequest)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<AuthRegisterResponse> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(authRegisterResponse: AuthRegisterResponse) {
                    Toast.makeText(
                        this@SignUpActivity,
                        authRegisterResponse.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onError(e: Throwable) {
                    if (e is HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        val message = try {
                            JSONObject(errorBody.toString()).getString("message")
                        } catch (jsonEx: Exception) {
                            "Unexpected error"
                        }
                        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onError: $message")
                    } else {
                        Toast.makeText(this@SignUpActivity, e.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {}
            })
    }

    private fun setClickEvent() {
        btn_SignUp.setOnClickListener {
            val name = edt_Name.text.toString().trim()
            val email = edt_Email.text.toString().trim()
            val password = edt_Password.text.toString().trim()

            if (name.isEmpty()) {
                edt_Name.error = "Name is required"
            } else if (email.isEmpty()) {
                edt_Email.error = "Email is required"
            } else if (password.isEmpty()) {
                edt_Password.error = "Password is required"
            } else {
                val registerRequest = AuthRegisterRequest(name, email, password)
                register(registerRequest)
            }
        }

        btn_GoogleSignUp.setOnClickListener {
            signUp()
        }
    }
}
