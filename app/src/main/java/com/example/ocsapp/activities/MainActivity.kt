package com.example.ocsapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Layout
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
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ocsapp.R
import com.example.ocsapp.data.UserState
import com.example.ocsapp.fragments.CartFragment
import com.example.ocsapp.fragments.ContactFragment
import com.example.ocsapp.fragments.FavouriteFragment
import com.example.ocsapp.fragments.HomeFragment
import com.example.ocsapp.fragments.SettingsFragment
import com.example.ocsapp.viewmodel.SupabaseAuthViewModel
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import io.ktor.http.CacheControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var headerName: TextView
    private lateinit var viewModel: SupabaseAuthViewModel

    private lateinit var headerCont: RelativeLayout
    private lateinit var headerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(SupabaseAuthViewModel::class.java)
        viewModel.isUserLoggedIn(this)

        navView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val headerView = navView.getHeaderView(0)

        val headerAva: CircleImageView = headerView.findViewById(R.id.ava)
        headerCont = headerView.findViewById(R.id.cont_profile)
        headerButton = headerView.findViewById(R.id.login_btn)

        headerName = headerView.findViewById(R.id.name)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navView.setNavigationItemSelectedListener(this)

        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()


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
        } else {
            headerCont.visibility = View.GONE
            headerButton.visibility = View.VISIBLE
        }
    }

    private fun loadUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {

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