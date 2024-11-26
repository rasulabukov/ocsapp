package com.example.ocs

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceManager(context: Context) {

    private val preferece = context.getSharedPreferences(
        context.getString(R.string.app_name), AppCompatActivity.MODE_PRIVATE
    )

    private val editor = preferece.edit()


    private val keyIsFirstTime = "isFirstTime"

    var isFirstTime
        get() = preferece.getBoolean(keyIsFirstTime, true)
        set(value) {
            editor.putBoolean(keyIsFirstTime, value)
            editor.commit()
        }
}