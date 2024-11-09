package com.example.ocsapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.ocsapp.Page
import com.example.ocsapp.R
import com.example.ocsapp.SharedPreferenceManager
import com.example.ocsapp.adapters.OBAdapter
import com.example.ocsapp.gone
import com.example.ocsapp.visible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardActivity : AppCompatActivity() {

    private val onBoardPageChangeCallback = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            when(position){
                0 -> {
                    nextBtn.text = "Next"
                    skipBtn.visible()
                    nextBtn.visible()
                }
                1 -> {
                    nextBtn.text = "Next"
                    skipBtn.visible()
                    nextBtn.visible()

                }
                else -> {
                    nextBtn.text = "Get Started"
                    skipBtn.gone()
                    nextBtn.visible()
                }
            }
        }
    }

    private val pagerList = arrayListOf(
        Page("Добро пожаловать в OCS!", R.drawable.ob1, "Откройте для себя мир моды с удобным приложением для покупок! \uD83C\uDF1F Найдите стильную одежду для любого повода всего в несколько кликов."),
        Page("Ваш индивидуальный стиль", R.drawable.ob2, "Пользуйтесь рекомендациями, сформированными на основе ваших предпочтений. Получайте подборки на основе вашего вкуса и трендов! \uD83C\uDFA8\uD83D\uDC57"),
        Page("Удобные покупки", R.drawable.ob3, "Делайте покупки легко и быстро! От выбора размеров до безопасной оплаты — мы заботимся о каждой детали. \uD83D\uDED2\uD83D\uDCB3"),
    )
    lateinit var onBoardViewPager2: ViewPager2
    lateinit var skipBtn: Button
    lateinit var nextBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)

        onBoardViewPager2 = findViewById(R.id.vponboard)
        skipBtn = findViewById(R.id.btn_skip)
        nextBtn = findViewById(R.id.btn_next)

        onBoardViewPager2.apply {
            adapter = OBAdapter(this@OnBoardActivity, pagerList)
            registerOnPageChangeCallback(onBoardPageChangeCallback)
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, onBoardViewPager2){tab, position -> }.attach()


        nextBtn.setOnClickListener {
            if(onBoardViewPager2.currentItem < onBoardViewPager2.adapter!!.itemCount-1){
                onBoardViewPager2.currentItem += 1
            }else{
                homeScreen()
            }
        }

        skipBtn.setOnClickListener {
            homeScreen()
        }

    }

    override fun onDestroy() {
        onBoardViewPager2.unregisterOnPageChangeCallback(onBoardPageChangeCallback)
        super.onDestroy()
    }

    private fun homeScreen() {
        val sharedPrefereceManager = SharedPreferenceManager(this)
        sharedPrefereceManager.isFirstTime = false
        val i = Intent(this, AuthActivity::class.java)
        startActivity(i)
        finish()
    }
}