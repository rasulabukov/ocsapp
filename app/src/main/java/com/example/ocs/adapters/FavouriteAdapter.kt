package com.example.ocs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ocs.R
import com.example.ocs.data.Product

class FavouriteAdapter(private var productList: List<Product>,
                       private val onItemClick: (Product) -> Unit,
                       private val onFavoriteDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.computer_image)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        private val quantityTextView: TextView = itemView.findViewById(R.id.count)
        private val gradeTextView: TextView = itemView.findViewById(R.id.grade)
        private val favoriteImageButton: ImageButton = itemView.findViewById(R.id.favourite_btn)

        fun bind(product: Product) {
            priceTextView.text = "${product.price} ₽"
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            quantityTextView.text = "Количество: ${product.quantity}"

            Glide.with(itemView.context)
                .load(product.image)
                .placeholder(R.drawable.load)
                .into(imageView)

            gradeTextView.text = "5.0"

            itemView.setOnClickListener {
                onItemClick(product)
            }


            favoriteImageButton.setOnClickListener {
                onFavoriteDeleteClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_favourite, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}