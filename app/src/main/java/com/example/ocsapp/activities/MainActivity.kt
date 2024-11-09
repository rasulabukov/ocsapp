package com.example.ocsapp.activities

import android.app.Dialog
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ocsapp.R
import com.example.ocsapp.fragments.CartFragment
import com.example.ocsapp.fragments.ContactFragment
import com.example.ocsapp.fragments.FavouriteFragment
import com.example.ocsapp.fragments.HomeFragment
import com.example.ocsapp.fragments.SettingsFragment
import com.google.android.material.navigation.NavigationView
import java.sql.Savepoint

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        navView.setNavigationItemSelectedListener(this)

        val toogle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()


        if(savedInstanceState == null){
            replaceFragment(HomeFragment())
            navView.setCheckedItem(R.id.home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> replaceFragment(HomeFragment())
            R.id.cart -> replaceFragment(CartFragment())
            R.id.fav -> replaceFragment(FavouriteFragment())
            R.id.settings -> replaceFragment(SettingsFragment())
            R.id.mess -> replaceFragment(ContactFragment())
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
            finish()
            startActivity(intent)
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
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