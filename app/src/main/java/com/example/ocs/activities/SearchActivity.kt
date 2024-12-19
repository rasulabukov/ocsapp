package com.example.ocs.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ocs.R
import com.example.ocs.adapters.MainRecyclerAdapter
import com.example.ocs.data.Product
import com.example.ocs.viewmodel.SupabaseProductsViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainRecyclerAdapter
    private lateinit var searchView: SearchView
    private lateinit var viewModel: SupabaseProductsViewModel
    private lateinit var back: ImageButton
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recycler_view)
        searchView = findViewById(R.id.search_view)
        back = findViewById(R.id.back)
        viewModel = ViewModelProvider(this).get(SupabaseProductsViewModel::class.java)

        searchView.requestFocus()
        setupRecyclerView()
        observeViewModel()
        setupSearch()
        back.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MainRecyclerAdapter(productList, { product ->
            Toast.makeText(this, "Вы выбрали: ${product.name}", Toast.LENGTH_SHORT).show()
        }, { product ->
            viewModel.addProductToFavorites(product.id) // Handle favorite click
        })
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.productList.observe(this) { products ->
            productList.clear()
            productList.addAll(products)
            adapter.notifyDataSetChanged()
        }

        viewModel.loadProductInfo()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    adapter.filter(it)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }
}
