package com.example.ocs.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.data.SupabaseClient.supabase
import com.example.ocs.data.User
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel
import de.hdodenhof.circleimageview.CircleImageView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
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
    private var selectedImageUri: Uri? = null
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
                selectedImageUri = uri
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

        lifecycleScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id

                // Проверяем, есть ли текущий пользователь
                if (userId != null) {
                    val response = supabase.from("users").select(columns = Columns.raw("avatar")) {
                        filter {
                            User::user_id eq userId
                        }
                    }.decodeSingle<Map<String, String>>()

                    // Проверяем, существует ли поле avatar и не пустое ли оно
                    val avatarUrl = response["avatar"]
                    if (!avatarUrl.isNullOrEmpty()) {
                        Glide.with(this@EditProfileActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ava) // Изображение по умолчанию
                            .error(R.drawable.ava)       // Если загрузка не удалась
                            .into(ava)
                    } else {
                        // Загружаем изображение по умолчанию, если avatar пустой
                        ava.setImageResource(R.drawable.ava)
                    }
                } else {
                    // Устанавливаем аватар по умолчанию, если пользователь не найден
                    ava.setImageResource(R.drawable.ava)
                }
            } catch (e: Exception) {
                // Обрабатываем возможные ошибки
                Toast.makeText(this@EditProfileActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                ava.setImageResource(R.drawable.ava) // Устанавливаем изображение по умолчанию
            }
        }
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
                    genderState
                )
                if (selectedImageUri != null) {
                    uploadImageToSupabase(selectedImageUri!!)
                } else {
                    startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java))
                    finish()
                    Toast.makeText(this@EditProfileActivity, "Данные успешно изменены", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun uploadImageToSupabase(uri: Uri) {
        val bucketName = "avatars"
        val fileName = "${System.currentTimeMillis()}.jpg"

        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.let {

            val byteArray = it.readBytes()

            lifecycleScope.launch {
                try {
                    // Получаем текущий аватар пользователя
                    val userId = supabase.auth.currentUserOrNull()?.id
                    val currentAvatarUrl = getCurrentAvatarUrl(userId)

                    // Удаляем старую аватарку, если она существует
                    if (!currentAvatarUrl.isNullOrEmpty() && currentAvatarUrl.contains(bucketName)) {
                        val oldFileName = currentAvatarUrl.substringAfterLast("/")
                        supabase.storage.from(bucketName).delete(oldFileName)
                    }

                    // Загружаем новую аватарку
                    supabase.storage.from(bucketName).upload(fileName, byteArray)

                    // Получаем публичный URL новой аватарки
                    val newImageUrl = supabase.storage.from(bucketName).publicUrl(fileName)

                    // Обновляем URL в базе данных
                    updateAvatarUrl(newImageUrl)

                    Toast.makeText(this@EditProfileActivity, "Аватарка успешно обновлена", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@EditProfileActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getCurrentAvatarUrl(userId: String?): String? {
        return try {
            if (userId == null) return null
            val response = supabase.from("users").select(columns = Columns.raw("avatar")) {
                filter {
                    User::user_id eq userId
                }
            }.decodeSingle<Map<String, String>>()

            response["avatar"]
        } catch (e: Exception) {
            Toast.makeText(this@EditProfileActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }



    private fun updateAvatarUrl(imageUrl: String) {
        lifecycleScope.launch {
            viewModel.updateUserAvatar(this@EditProfileActivity, imageUrl)
        }

    }
}