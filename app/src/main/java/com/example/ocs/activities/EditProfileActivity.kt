package com.example.ocs.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ocs.R
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private lateinit var ava: CircleImageView
    private lateinit var firstnameEdt: EditText
    private lateinit var lastnameEdt: EditText
    private lateinit var emailEdt: EditText
    private lateinit var phoneEdt: EditText
    private lateinit var gender: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        ava = findViewById(R.id.ava)
        firstnameEdt = findViewById(R.id.firstname)
        lastnameEdt = findViewById(R.id.lastname)
        emailEdt = findViewById(R.id.email)
        phoneEdt = findViewById(R.id.phone)

        val btn: Button = findViewById(R.id.editProfile)

        loadUserInfo()

        ava.setOnClickListener {
            if (isStoragePermissionGranted()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
        }

        btn.setOnClickListener {
            finish()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Разрешение на доступ к хранилищу отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                ava.setImageURI(uri)
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
        private const val GALLERY_REQUEST_CODE = 100
    }

    private fun loadUserInfo() {
    }
}