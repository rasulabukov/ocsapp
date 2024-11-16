package com.example.ocsapp.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ocsapp.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var ava: CircleImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val editBtn: Button = findViewById(R.id.editProfile)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
        ava = findViewById(R.id.ava)

        loadUserInfo()

        editBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                Toast.makeText(this, "Уведомления", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.logout_confirm_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnyes: Button = dialog.findViewById(R.id.conf_btn)
        val btnno: Button = dialog.findViewById(R.id.canc_btn)

        btnyes.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            dialog.dismiss()
            startActivity(intent)
            finish()
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        }
        btnno.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadUserInfo() {

    }


}