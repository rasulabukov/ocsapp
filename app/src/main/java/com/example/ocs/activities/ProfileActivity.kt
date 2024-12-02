package com.example.ocs.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.data.SupabaseClient.supabase
import com.example.ocs.data.User
import com.example.ocs.data.UserState
import com.example.ocs.viewmodel.SupabaseAuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var ava: CircleImageView
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var adminpanel: TextView
    private lateinit var refreshView: SwipeRefreshLayout

    private lateinit var viewModel: SupabaseAuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val editBtn: Button = findViewById(R.id.editProfile)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
        ava = findViewById(R.id.ava)
        name = findViewById(R.id.name)
        email = findViewById(R.id.emailTextview)
        refreshView = findViewById(R.id.refresh)
        val otherTextView: TextView = findViewById(R.id.other)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)

        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)

        viewModel.userState.observe(this) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    refreshView.isRefreshing = true
                }

                is UserState.Success -> {
                    refreshView.isRefreshing = false
                    Toast.makeText(this, userState.message, Toast.LENGTH_SHORT).show()
                }

                is UserState.Error -> {
                    refreshView.isRefreshing = false
                    Toast.makeText(this, userState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
        adminpanel = findViewById(R.id.adminpanel)

        val bottomSheetDialog = BottomSheetDialog(this)
        val openDialogTextView: TextView = bottomSheet.findViewById(R.id.delete)

        loadUserInfo()
        AdminUser()

        editBtn.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            finish()
        }
        otherTextView.setOnClickListener {
            bottomSheetDialog.setContentView(bottomSheet)
            bottomSheetDialog.show()

        }
        adminpanel.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }
        openDialogTextView.setOnClickListener {
            showCustomDialog()
        }
        refreshView.setOnRefreshListener {
            loadUserInfo()
        }

    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_confirm_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnOk: Button = dialog.findViewById(R.id.conf_btn)
        val btnCancel: Button = dialog.findViewById(R.id.canc_btn)
        val passEdt: EditText = dialog.findViewById(R.id.password)


        lifecycleScope.launch {
            val user = supabase.auth.currentUserOrNull()?.id
            if (user != null) {
                val userPass = supabase.from("users").select(columns = Columns.list("user_id", "firstname", "lastname", "email", "phone", "password")) {
                    filter {
                        User::user_id eq user
                    }
                }.decodeSingle<User>()

                val password = userPass.password.toString()

                btnOk.setOnClickListener {
                    if (passEdt.text.isNotEmpty() && passEdt.text.toString() == password){
                        viewModel.logout()
                        Toast.makeText(this@ProfileActivity, "Аккаунт удалён", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        startActivity(Intent(this@ProfileActivity, AuthActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Пароль неверный", Toast.LENGTH_SHORT).show()
                        Log.d("pass", password)
                    }

                }
            }

        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
            viewModel.logout()
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        btnno.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadUserInfo() {
        viewModel.loadUserInfo(this, { firstName ->
            // Обновляем TextView с именем пользователя
            name.text = firstName

        }, { Email ->
            email.text = Email
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
                        Glide.with(this@ProfileActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ava) // Изображение по умолчанию
                            .error(R.drawable.ava)       // Если загрузка не удалась
                            .into(ava)
                        refreshView.isRefreshing = false
                    } else {
                        // Устанавливаем изображение по умолчанию, если avatar пустой
                        ava.setImageResource(R.drawable.ava)
                        refreshView.isRefreshing = false
                    }
                } else {
                    // Устанавливаем аватар по умолчанию, если пользователь не найден
                    ava.setImageResource(R.drawable.ava)
                    refreshView.isRefreshing = false
                }
            } catch (e: Exception) {
                // Обрабатываем возможные ошибки
                Toast.makeText(this@ProfileActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                ava.setImageResource(R.drawable.ava) // Устанавливаем изображение по умолчанию
            }
        }
    }

    private fun AdminUser() {
        val user = supabase.auth.currentUserOrNull()?.email
        if (user == "mrgaaksm@gmail.com" || user == "ajnaabukova@gmail.com"){
            adminpanel.visibility = View.VISIBLE
        } else {
            adminpanel.visibility = View.GONE
        }
    }


}