package com.example.ocs.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.data.Product

class MainRecyclerAdapter(
    private var productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<MainRecyclerAdapter.ProductViewHolder>() {

    private var filteredList: List<Product> = productList

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.computer_image)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        private val quantityTextView: TextView = itemView.findViewById(R.id.count)
        private val gradeTextView: TextView = itemView.findViewById(R.id.grade)
        private val favoriteImageButton: ImageButton = itemView.findViewById(R.id.favourite_btn)

        fun bind(product: Product) {
            // Устанавливаем данные
            priceTextView.text = "${product.price} ₽"
            nameTextView.text = "${product.name}"
            descriptionTextView.text = product.description
            quantityTextView.text = "Количество: ${product.quantity}"

            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(product.image) // URL изображения
                .placeholder(R.drawable.load) // Плейсхолдер
                .into(imageView)

            gradeTextView.text = "5.0" // Пример статического значения

            // Обработчик клика на элемент
            itemView.setOnClickListener {
                onItemClick(product)
            }

            favoriteImageButton.setOnClickListener {
                Toast.makeText(itemView.context, "В избранном: ${product.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            productList
        } else {
            productList.filter { product ->
                product.name!!.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
