package com.example.ocs.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.data.SupabaseClient.supabase
import com.example.ocs.data.User
import com.example.ocs.data.UserState
import com.example.ocs.fragments.CartFragment
import com.example.ocs.fragments.ContactFragment
import com.example.ocs.fragments.FavouriteFragment
import com.example.ocs.fragments.HomeFragment
import com.example.ocs.fragments.SettingsFragment
import com.example.ocs.viewmodel.SupabaseAuthViewModel
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var headerName: TextView
    private lateinit var headerEmail: TextView
    private lateinit var headerAva: CircleImageView
    private lateinit var viewModel: SupabaseAuthViewModel

    private lateinit var headerCont: RelativeLayout
    private lateinit var headerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)
        viewModel.isUserLoggedIn(this)

        viewModel.userState.observe(this) { userState ->
            when (userState) {
                is UserState.Loading -> {
                    // Здесь можно показать прогресс
                }

                is UserState.Success -> {
                    updateNavigationView(true)
                    loadUserInfo()
                }

                is UserState.Error -> {
                    updateNavigationView(false)
                }
            }
        }

        navView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val headerView = navView.getHeaderView(0)

        headerAva = headerView.findViewById(R.id.ava)
        headerCont = headerView.findViewById(R.id.cont_profile)
        headerButton = headerView.findViewById(R.id.login_btn)

        headerName = headerView.findViewById(R.id.name)
        headerEmail = headerView.findViewById(R.id.email)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navView.setNavigationItemSelectedListener(this)

        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        headerCont.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        headerButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        if(savedInstanceState == null){
            replaceFragment(HomeFragment())
            navView.setCheckedItem(R.id.home)
            supportActionBar?.title = "Главная"
        }


    }

    private fun updateNavigationView(isLoggedIn: Boolean) {
        val navView: NavigationView = findViewById(R.id.navigation_view)
        if (isLoggedIn) {
            headerCont.visibility = View.VISIBLE
            headerButton.visibility = View.GONE
            navView.menu.findItem(R.id.logout).isVisible = true
        } else {
            headerCont.visibility = View.GONE
            headerButton.visibility = View.VISIBLE
            navView.menu.findItem(R.id.logout).isVisible = false
        }
    }

    private fun loadUserInfo() {
        lifecycleScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id

                if (userId != null) {
                    // Запрашиваем данные пользователя из базы
                    val response = supabase.from("users").select(
                        columns = Columns.raw("firstname, lastname, email, avatar")
                    ) {
                        filter {
                            User::user_id eq userId
                        }
                    }.decodeSingle<Map<String, String>>()

                    // Получаем данные пользователя
                    val firstName = response["firstname"] ?: ""
                    val lastName = response["lastname"] ?: ""
                    val email = response["email"] ?: ""
                    val avatarUrl = response["avatar"]

                    // Устанавливаем имя и фамилию
                    headerName.text = "$firstName $lastName"

                    // Устанавливаем email
                    headerEmail.text = email

                    // Загружаем аватарку, если она есть
                    if (!avatarUrl.isNullOrEmpty()) {
                        Glide.with(this@MainActivity)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ava) // Изображение по умолчанию
                            .error(R.drawable.ava)       // Если загрузка не удалась
                            .into(headerAva)
                    } else {
                        headerAva.setImageResource(R.drawable.ava) // Устанавливаем изображение по умолчанию
                    }
                } else {
                    // Если пользователь не найден, показываем заглушки
                    headerName.text = "Гость"
                    headerEmail.text = "example@example.com"
                    headerAva.setImageResource(R.drawable.ava)
                }
            } catch (e: Exception) {
                // Обработка ошибок
                headerName.text = "Гость"
                headerEmail.text = "example@example.com"
                headerAva.setImageResource(R.drawable.ava)
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                replaceFragment(HomeFragment())
                supportActionBar?.title = "Главная"
            }
            R.id.cart -> {
                replaceFragment(CartFragment())
                supportActionBar?.title = "Корзина"
            }
            R.id.fav -> {
                replaceFragment(FavouriteFragment())
                supportActionBar?.title = "Избранное"
            }
            R.id.settings -> {
                replaceFragment(SettingsFragment())
                supportActionBar?.title = "Настройки"
            }
            R.id.mess -> {
                replaceFragment(ContactFragment())
                supportActionBar?.title = "Контакты"
            }
            R.id.logout -> showDialog()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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

    private fun replaceFragment(fragment: Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_cont, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else{
            onBackPressedDispatcher.onBackPressed()
        }
    }


}