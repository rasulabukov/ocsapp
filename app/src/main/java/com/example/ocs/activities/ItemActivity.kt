package com.example.ocs.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.viewmodel.SupabaseProductsViewModel

class ItemActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var quantityTextView: TextView

    private val viewModel: SupabaseProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        imageView = findViewById(R.id.image)
        nameTextView = findViewById(R.id.main_text)
        descriptionTextView = findViewById(R.id.desc_text)
        priceTextView = findViewById(R.id.price_text)
        quantityTextView = findViewById(R.id.quantity_text)

        val back: ImageButton = findViewById(R.id.back_btn)

        val productId = intent.getIntExtra("PRODUCT_ID", 0)

        viewModel.loadProductInfoItemActivity(productId)

        viewModel.product.observe(this, { product ->
            product?.let {
                nameTextView.text = it.name
                descriptionTextView.text = it.description
                priceTextView.text = "${it.price} ₽"
                quantityTextView.text = "Количество: ${it.quantity}"

                Glide.with(this@ItemActivity)
                    .load(it.image)
                    .placeholder(R.drawable.load)
                    .into(imageView)
            } ?: run {
                // Handle the case where product data is null
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            }
        })

        back.setOnClickListener {
            finish()
        }

    }

}