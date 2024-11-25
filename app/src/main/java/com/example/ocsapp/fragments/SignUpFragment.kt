package com.example.ocsapp.fragments

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
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.ocsapp.R
import com.example.ocsapp.activities.AuthActivity
import com.example.ocsapp.data.UserState
import com.example.ocsapp.viewmodel.SupabaseAuthViewModel
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signin: TextView
    private lateinit var signup: Button
    private lateinit var warningTextView: TextView
    private lateinit var viewModel: SupabaseAuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        signin = view.findViewById(R.id.signin)
        signup = view.findViewById(R.id.signup_btn)
        firstNameEditText = view.findViewById(R.id.fname)
        lastNameEditText = view.findViewById(R.id.lname)
        emailEditText = view.findViewById(R.id.email)
        phoneEditText = view.findViewById(R.id.phone)
        passwordEditText = view.findViewById(R.id.password)
        warningTextView = view.findViewById(R.id.error)

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)

        viewModel.userState.observe(viewLifecycleOwner) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    // Здесь можно показать прогресс
                }

                is UserState.Success -> {
                    Toast.makeText(requireContext(), userState.message, Toast.LENGTH_SHORT).show()
                }

                is UserState.Error -> {
                    Toast.makeText(requireContext(), userState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }


        signup.isEnabled = false
        signup.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login_error)


        firstNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFirstName()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lastNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateLastName()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
                validateAllInputs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePhone()
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


        signin.setOnClickListener {
            val mainActivity = activity as AuthActivity
            mainActivity.findViewById<ViewPager2>(R.id.viewPager).currentItem = 0
        }

        signup.setOnClickListener {
            CreateAccount()

        }

        return view
    }

    private fun CreateAccount() {
        val firstname = firstNameEditText.text.toString()
        val lastname = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val password = passwordEditText.text.toString()
        val profileimage = ""


        viewModel.signUp(requireContext(), firstname, lastname, email, phone, password, profileimage)

    }



    private fun validateFirstName() {
        val firstName = firstNameEditText.text.toString()
        if (firstName.isNotEmpty() && !firstName[0].isUpperCase()) {
            warningTextView.text = "Имя должно начинаться с большой буквы."
            firstNameEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            firstNameEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google)
        }
    }

    private fun validateLastName() {
        val lastName = lastNameEditText.text.toString()
        if (lastName.isNotEmpty() && !lastName[0].isUpperCase()) {
            warningTextView.text = "Фамилия должна начинаться с большой буквы."
            lastNameEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            lastNameEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google)
        }
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

    private fun validatePhone() {
        val phone = phoneEditText.text.toString()
        if (phone.isEmpty()) {
            warningTextView.text = "Введите номер телефона."
            phoneEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google_error)
        } else {
            warningTextView.text = ""
            phoneEditText.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_google)
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
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val password = passwordEditText.text.toString()

        val isValid = firstName.isNotEmpty() && lastName.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                phone.isNotEmpty() && password.length >= 6

        signup.isEnabled = isValid
        signup.background = (if (isValid) ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login) else ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_login_error))
    }


}