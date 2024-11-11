package com.example.ocsapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocsapp.R

class NewPassActivity : AppCompatActivity() {
    private lateinit var btn_reset: Button
    private lateinit var warningTextView: TextView
    private lateinit var passwordEditText: EditText
    private lateinit var confpassEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pass)
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
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
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
        } else {
            warningTextView.text = ""
            confpassEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
        }
    }

    private fun validateAllInputs() {
        val password = passwordEditText.text.toString()
        val passwordconf = confpassEditText.text.toString()

        val isValid = password.length >= 6 && passwordconf.length >= 6

        btn_reset.isEnabled = isValid
        btn_reset.background = (if (isValid) ContextCompat.getDrawable(this, R.drawable.bg_button_login) else ContextCompat.getDrawable(this, R.drawable.bg_button_login_error))
    }
}