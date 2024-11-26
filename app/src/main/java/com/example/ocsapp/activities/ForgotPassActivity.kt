package com.example.ocsapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ocsapp.R
import com.example.ocsapp.data.State
import com.example.ocsapp.data.UserState
import com.example.ocsapp.viewmodel.SupabaseAuthViewModel

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var warningTextView: TextView
    private lateinit var btnForgot: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: SupabaseAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        progressBar = findViewById(R.id.progressBar)
        emailEditText = findViewById(R.id.email)

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)

        viewModel.timer.observe(this) { State ->
            when (State) {
                is State.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is State.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, State.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, OtpActivity::class.java).apply {
                        putExtra("email", emailEditText.text.toString())
                    })
                    finish()
                }

                is State.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, State.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val login: TextView = findViewById(R.id.login)
        btnForgot = findViewById(R.id.btn)
        val back: ImageButton = findViewById(R.id.back)
        warningTextView = findViewById(R.id.error)

        btnForgot.isEnabled = false
        btnForgot.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_error)

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        login.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnForgot.setOnClickListener {
            viewModel.sendResetPasswordOtp(emailEditText.text.toString())
        }
        back.setOnClickListener {
            finish()
        }
    }
    private fun validateEmail() {
        val email = emailEditText.text.toString()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            warningTextView.text = "Введите корректный email."
            emailEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            emailEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
        }
    }

    private fun validateAllInputs() {
        val email = emailEditText.text.toString()

        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

        btnForgot.isEnabled = isValid
        btnForgot.background = (if (isValid) ContextCompat.getDrawable(this, R.drawable.bg_button_login) else ContextCompat.getDrawable(this, R.drawable.bg_button_login_error))
    }
}