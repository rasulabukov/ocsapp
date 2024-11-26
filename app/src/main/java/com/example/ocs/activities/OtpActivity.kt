package com.example.ocs.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.example.ocs.data.State
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel

class OtpActivity : AppCompatActivity() {
    private lateinit var firstEdt: EditText
    private lateinit var secondEdt: EditText
    private lateinit var threeEdt: EditText
    private lateinit var fourEdt: EditText
    private lateinit var fiveEdt: EditText
    private lateinit var sixEdt: EditText
    private lateinit var warningTextView: TextView
    private lateinit var btnOtp: Button
    private lateinit var timer: TextView
    private lateinit var emailView: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: SupabaseAuthViewModel

    private var email: String = ""
    private lateinit var otpFields: List<EditText>

    private var countdownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        progressBar = findViewById(R.id.progressBar)
        email = intent.getStringExtra("email") ?: ""

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)

        viewModel.userState.observe(this) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is UserState.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, userState.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, NewPassActivity::class.java).apply {
                        putExtra("email", email)
                    })
                    finish()
                }

                is UserState.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, userState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.timer.observe(this) { State ->
            when (State) {
                is State.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is State.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, State.message, Toast.LENGTH_SHORT).show()
                }

                is State.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, State.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        timer = findViewById(R.id.timer)
        emailView = findViewById(R.id.email_view)

        emailView.text = email


        startCountdown(timeLeftInMillis)

        val back: ImageButton = findViewById(R.id.back)

        firstEdt = findViewById(R.id.firstEdt)
        secondEdt = findViewById(R.id.secondEdt)
        threeEdt = findViewById(R.id.threeEdt)
        fourEdt = findViewById(R.id.fourEdt)
        fiveEdt = findViewById(R.id.fiveEdt)
        sixEdt = findViewById(R.id.sixEdt)
        warningTextView = findViewById(R.id.error)
        btnOtp = findViewById(R.id.btn)

        btnOtp.isEnabled = false
        btnOtp.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_error)
        firstEdt.requestFocus()

        otpFields = listOf(
            firstEdt,
            secondEdt,
            threeEdt,
            fourEdt,
            fiveEdt,
            sixEdt
        )

        btnOtp.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }
            viewModel.resetPasswordWithOtp(email, otp)
        }

        back.setOnClickListener {
            finish()
        }


        addTextWatchers()

    }

    private fun startCountdown(millis: Long) {
        countdownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.isClickable = false
                timeLeftInMillis = millisUntilFinished
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val displaySeconds = seconds % 60
                timer.text = String.format("%02d:%02d", minutes, displaySeconds)
            }

            override fun onFinish() {
                val timer: TextView = findViewById(R.id.timer)
                timer.setText(R.string.timer)
                timer.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.blue))
                timer.isClickable = true
                timer.setOnClickListener {
                    viewModel.sendResetPasswordOtp(email)
                    resetCountdown()
                }
            }
        }.start()
    }
    private fun resetCountdown() {
        timeLeftInMillis = 60000
        val timer: TextView = findViewById(R.id.timer)
        timer.setTextColor(ContextCompat.getColor(this, R.color.otp))
        timer.isClickable = false
        startCountdown(timeLeftInMillis)
    }


    private fun validateOtp() {
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt, sixEdt)

        for (editText in editTexts) {
            val empty = editText.text.toString()
            if (empty.isEmpty()) {
                warningTextView.text = "Заполните все поля"
                editText.background = ContextCompat.getDrawable(this, R.drawable.bg_google_error)
            } else {
                warningTextView.text = ""
                editText.background = ContextCompat.getDrawable(this, R.drawable.bg_google)
            }
        }

    }

    private fun validateAllInputs() {
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt, sixEdt)
        var allFilled = true // Переменная для отслеживания заполненности всех полей

        // Проверяем каждое поле
        for (editText in editTexts) {
            val text = editText.text.toString()
            if (text.isEmpty()) {
                allFilled = false // Если хотя бы одно поле пустое, устанавливаем false
                break
            }
        }

        // Установка состояния кнопки и фона
        btnOtp.isEnabled = allFilled
        btnOtp.background = if (allFilled) {
            ContextCompat.getDrawable(this, R.drawable.bg_button_login)
        } else {
            ContextCompat.getDrawable(this, R.drawable.bg_button_login_error)
        }

        // Отображение предупреждения
        if (!allFilled) {
            warningTextView.text = "Заполните все поля"
        } else {
            warningTextView.text = ""
        }
    }

    private fun addTextWatchers() {
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt, sixEdt)

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validateAllInputs()
                    validateOtp()

                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }

                }

                override fun afterTextChanged(s: Editable?) {}
            })
            editTexts[i].setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && editTexts[i].text.isEmpty()) {
                    if (i > 0) {
                        editTexts[i - 1].requestFocus()
                        editTexts[i - 1].setText("") // Очищаем предыдущий EditText
                    }
                    true // Указываем, что событие обработано
                } else {
                    false
                }
            }
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }
}