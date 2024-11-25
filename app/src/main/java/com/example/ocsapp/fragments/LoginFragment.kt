package com.example.ocsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ocsapp.R
import com.example.ocsapp.activities.AuthActivity
import com.example.ocsapp.activities.ForgotPassActivity
import com.example.ocsapp.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var warningTextView: TextView
    private lateinit var login: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.password)
        warningTextView = view.findViewById(R.id.error)
        login = view.findViewById(R.id.login_btn)


        val signup: TextView = view.findViewById(R.id.signup)
        val forgot: TextView = view.findViewById(R.id.forgot)

        login.isEnabled = false
        login.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login_error)

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        forgot.setOnClickListener {
            val intent = Intent(activity, ForgotPassActivity::class.java)
            startActivity(intent)
        }

        signup.setOnClickListener {
            val mainActivity = activity as AuthActivity
            mainActivity.findViewById<ViewPager2>(R.id.viewPager).currentItem = 1
        }

        login.setOnClickListener {
            Login()
        }

        return view
    }

    private fun Login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()


    }

    private fun validateEmail() {
        val email = emailEditText.text.toString()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            warningTextView.text = "Введите корректный email."
            emailEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            emailEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google)
        }
    }

    private fun validatePassword() {
        val password = passwordEditText.text.toString()
        if (password.length < 6) {
            warningTextView.text = "Пароль должен содержать минимум 6 символов."
            passwordEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            passwordEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google)
        }
    }

    private fun validateAllInputs() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6

        login.isEnabled = isValid
        login.background = (if (isValid) ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login) else ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login_error))
    }


}