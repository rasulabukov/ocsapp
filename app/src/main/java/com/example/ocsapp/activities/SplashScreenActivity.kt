package com.example.ocsapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.ocsapp.R
import com.example.ocsapp.SharedPreferenceManager

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var cont: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        cont = findViewById(R.id.cont)

        cont.alpha = 0f
        cont.animate().setDuration(1500).alpha(1f).withEndAction {
            isFirstTime()
        }
    }

    private fun isFirstTime() {
        val sharedPreferenceManager = SharedPreferenceManager(this)
        if (sharedPreferenceManager.isFirstTime) {
            val i = Intent(this, OnBoardActivity::class.java)
            startActivity(i)
            finish()
        } else {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
