package com.example.ocs.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ocs.R
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel

class NewPassActivity : AppCompatActivity() {
    private lateinit var btn_reset: Button
    private lateinit var warningTextView: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var confpassEditText: EditText

    private var email: String = ""

    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: SupabaseAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pass)
        email = intent.getStringExtra("email") ?: ""
        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)
        progressBar = findViewById(R.id.progressBar)
        viewModel.userState.observe(this) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is UserState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, userState.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is UserState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, userState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }




        val back: ImageButton = findViewById(R.id.back)
        val login: TextView = findViewById(R.id.login)

        btn_reset = findViewById(R.id.btn_reset)
        warningTextView = findViewById(R.id.error)
        passwordEditText = findViewById(R.id.password)
        confpassEditText = findViewById(R.id.conf_pass)

        btn_reset.isEnabled = false
        btn_reset.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_error)

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        confpassEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordConf()
                validateAllInputs()
                validateConfirm()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        back.setOnClickListener {
            finish()
        }

        login.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_reset.setOnClickListener {
            viewModel.updatePassword(email, confpassEditText.text.toString())
            viewModel.updatePasswordToDatabase(confpassEditText.text.toString())
        }

    }

    private fun validatePassword() {
        val password = passwordEditText.text.toString()
        if (password.length < 6) {
            warningTextView.text = "Пароль должен содержать минимум 6 символов."
            passwordEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            passwordEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
        }
    }

    private fun validatePasswordConf() {
        val password = confpassEditText.text.toString()
        if (password.length < 6) {
            warningTextView.text = "Пароль должен содержать минимум 6 символов."
            confpassEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
        }
        else {
            warningTextView.text = ""
            confpassEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
        }
    }

    private fun validateConfirm() {
        val password = passwordEditText.text.toString()
        val passwordconf = confpassEditText.text.toString()
        if (password != passwordconf) {
            warningTextView.text = "Пароли не совпадают."
            passwordEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
            confpassEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            confpassEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
            passwordEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
        }
    }

    private fun validateAllInputs() {
        val password = passwordEditText.text.toString()
        val passwordconf = confpassEditText.text.toString()

        val isValid = (password.length >= 6 && passwordconf.length >= 6) && (password == passwordconf)

        btn_reset.isEnabled = isValid
        btn_reset.background = (if (isValid) ContextCompat.getDrawable(this, R.drawable.bg_button_login) else ContextCompat.getDrawable(this, R.drawable.bg_button_login_error))
    }
}