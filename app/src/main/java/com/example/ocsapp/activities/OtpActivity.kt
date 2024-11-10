package com.example.ocsapp.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint.Style
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocsapp.R

class OtpActivity : AppCompatActivity() {

    private var countdownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 30000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        startCountdown(timeLeftInMillis)
        
        val btn: Button = findViewById(R.id.btn)

        btn.setOnClickListener {
            val intent = Intent(this, NewPassActivity::class.java)
            startActivity(intent)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }
}