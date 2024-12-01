package com.example.ocs.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ocs.R
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var ava: CircleImageView
    private lateinit var firstnameEdt: EditText
    private lateinit var lastnameEdt: EditText
    private lateinit var emailEdt: EditText
    private lateinit var phoneEdt: EditText
    private lateinit var gender: RadioGroup
    private lateinit var men: RadioButton
    private lateinit var woman: RadioButton
    private lateinit var viewModel: SupabaseAuthViewModel

    private lateinit var genderState: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        ava = findViewById(R.id.ava)
        firstnameEdt = findViewById(R.id.firstname)
        lastnameEdt = findViewById(R.id.lastname)
        emailEdt = findViewById(R.id.email)
        phoneEdt = findViewById(R.id.phone)
        gender = findViewById(R.id.gender)
        men = findViewById(R.id.men)
        woman = findViewById(R.id.woman)

        val btn: Button = findViewById(R.id.editProfile)

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)
        loadUserInfo()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"


        ava.setOnClickListener {
            if (isStoragePermissionGranted()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
        }

        btn.setOnClickListener {
            EditUserInfo()

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
        viewModel.loadUserInfoEditActivity(this, { firstname ->
            firstnameEdt.setText(firstname)

        }, { lastname ->
            lastnameEdt.setText(lastname)
        }, { email ->
            emailEdt.setText(email)
        }, { phone ->
            phoneEdt.setText(phone)
        },{ genderstate ->
            when (genderstate) {
                "male" -> gender.check(R.id.men)
                "female" -> gender.check(R.id.woman)
                else -> gender.clearCheck()
            }
        })
    }

    private fun EditUserInfo() {

        if(firstnameEdt.text.isEmpty() || lastnameEdt.text.isEmpty() || phoneEdt.text.isEmpty() || emailEdt.text.isEmpty()){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailEdt.text).matches()){
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
        }
        else{
            if (men.isChecked){
                genderState = "male"
            } else if (woman.isChecked){
                genderState = "female"
            } else{
                genderState = ""
            }
            viewModel.editUserInfo(this, firstnameEdt.text.toString(), lastnameEdt.text.toString(), phoneEdt.text.toString(), emailEdt.text.toString())
            lifecycleScope.launch {
                viewModel.editUserToDatabase(
                    firstnameEdt.text.toString(),
                    lastnameEdt.text.toString(),
                    emailEdt.text.toString(),
                    phoneEdt.text.toString(),
                    "",
                    genderState
                )
            }
            finish()

        }

    }
}