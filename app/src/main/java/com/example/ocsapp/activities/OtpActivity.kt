package com.example.ocsapp.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint.Style
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
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

class OtpActivity : AppCompatActivity() {
    private lateinit var firstEdt: EditText
    private lateinit var secondEdt: EditText
    private lateinit var threeEdt: EditText
    private lateinit var fourEdt: EditText
    private lateinit var fiveEdt: EditText
    private lateinit var warningTextView: TextView
    private lateinit var btnOtp: Button

    private var countdownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 30000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        startCountdown(timeLeftInMillis)

        val back: ImageButton = findViewById(R.id.back)

        firstEdt = findViewById(R.id.firstEdt)
        secondEdt = findViewById(R.id.secondEdt)
        threeEdt = findViewById(R.id.threeEdt)
        fourEdt = findViewById(R.id.fourEdt)
        fiveEdt = findViewById(R.id.fiveEdt)
        warningTextView = findViewById(R.id.error)
        btnOtp = findViewById(R.id.btn)

        btnOtp.isEnabled = false
        btnOtp.background = ContextCompat.getDrawable(this, R.drawable.bg_button_login_error)
        firstEdt.requestFocus()


        btnOtp.setOnClickListener {
            val intent = Intent(this, NewPassActivity::class.java)
            startActivity(intent)
        }

        back.setOnClickListener {
            finish()
        }

        addTextWatchers()

    }

    private fun startCountdown(millis: Long) {
        countdownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timer: TextView = findViewById(R.id.timer)
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
                    resetCountdown()
                }
            }
        }.start()
    }
    private fun resetCountdown() {
        timeLeftInMillis = 30000
        val timer: TextView = findViewById(R.id.timer)
        timer.setTextColor(ContextCompat.getColor(this, R.color.otp))
        timer.isClickable = false
        startCountdown(timeLeftInMillis)
    }


    private fun validateOtp() {
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt)

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
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt)
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
        val editTexts = listOf(firstEdt, secondEdt, threeEdt, fourEdt, fiveEdt)

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