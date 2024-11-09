package com.example.ocsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.ocsapp.Page
import com.example.ocsapp.R


class OnBoardFragment(private val page: Page) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_board, container, false)

        val title = view.findViewById<TextView>(R.id.title)
        val desc = view.findViewById<TextView>(R.id.desc)
        val imageView = view.findViewById<ImageView>(R.id.img)

        title.text = page.title
        desc.text = page.desc
        imageView.setImageResource(page.image)

        return view
    }

}