package com.example.ocsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.ocsapp.R
import com.example.ocsapp.activities.AuthActivity
import com.example.ocsapp.activities.ForgotPassActivity
import com.example.ocsapp.activities.MainActivity

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val signup: TextView = view.findViewById(R.id.signup)
        val forgot: TextView = view.findViewById(R.id.forgot)

        forgot.setOnClickListener {
            val intent = Intent(activity, ForgotPassActivity::class.java)
            startActivity(intent)
        }

        signup.setOnClickListener {
            val mainActivity = activity as AuthActivity
            mainActivity.findViewById<ViewPager2>(R.id.viewPager).currentItem = 1
        }

        return view
    }


}