package com.example.ocs.fragments

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
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.ocs.R
import com.example.ocs.activities.AuthActivity
import com.example.ocs.activities.ForgotPassActivity
import com.example.ocs.activities.MainActivity
import com.example.ocs.data.UserLoad
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var warningTextView: TextView
    private lateinit var login: Button
    private lateinit var viewModel: SupabaseAuthViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var google: LinearLayout


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

        google = view.findViewById(R.id.google)

        progressBar = view.findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)

        viewModel.userState.observe(viewLifecycleOwner) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is UserState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), userState.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                is UserState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), userState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.userLoad.observe(viewLifecycleOwner) { userLoad ->
            when (userLoad) {
                is UserLoad.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is UserLoad.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), userLoad.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                is UserLoad.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), userLoad.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

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

        google.setOnClickListener {
            viewModel.signInGoogle(requireContext())
        }

        login.setOnClickListener {
            Login()
        }

        return view
    }

    private fun Login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        viewModel.login(requireContext(), email, password)
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