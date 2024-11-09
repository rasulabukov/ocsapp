package com.example.ocsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ocsapp.Page
import com.example.ocsapp.fragments.OnBoardFragment

class OBAdapter(activity: FragmentActivity, private val pagerList: ArrayList<Page>) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return pagerList.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnBoardFragment(pagerList[position])
    }
}