package com.example.ocs.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ocs.R
import com.example.ocs.activities.ItemActivity
import com.example.ocs.adapters.FavouriteAdapter
import com.example.ocs.data.Product
import com.example.ocs.data.ProductState
import com.example.ocs.viewmodel.SupabaseProductsViewModel


class FavouriteFragment : Fragment() {
    private lateinit var viewModel: SupabaseProductsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavouriteAdapter
    private lateinit var refreshView: SwipeRefreshLayout
    private lateinit var favouriteEmptyView: LinearLayout
    private var favoriteProductList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerView = view.findViewById(R.id.recycler_View)
        refreshView = view.findViewById(R.id.refresh)
        favouriteEmptyView = view.findViewById(R.id.favourite_empty)

        viewModel = ViewModelProvider(this).get(SupabaseProductsViewModel::class.java)

        setupRecyclerView()
        loadFavoriteProducts()

        refreshView.setOnRefreshListener {
            loadFavoriteProducts()
        }

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = FavouriteAdapter(favoriteProductList, { product ->
            val intent = Intent(requireContext(), ItemActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }, { product ->
            viewModel.removeProductFromFavorites(product.id)
        })
        recyclerView.adapter = adapter

        viewModel.favoriteList.observe(viewLifecycleOwner) { products ->
            favoriteProductList.clear() // Очистим старый список
            favoriteProductList.addAll(products) // Добавим новые товары
            adapter.notifyDataSetChanged() // Обновим адаптер

            if (favoriteProductList.isEmpty()) {
                favouriteEmptyView.visibility = View.VISIBLE // Показываем empty view
                recyclerView.visibility = View.GONE // Скрываем RecyclerView
            } else {
                favouriteEmptyView.visibility = View.GONE // Скрываем empty view
                recyclerView.visibility = View.VISIBLE // Показываем RecyclerView
            }
            refreshView.isRefreshing = false // Спрячем индикатор загрузки
        }

        viewModel.favoriteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductState.Loading -> {
                    refreshView.isRefreshing = true
                }

                is ProductState.Success -> {
                    refreshView.isRefreshing = false
                }

                is ProductState.Error -> {
                    refreshView.isRefreshing = false
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.productState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProductState.Loading -> {
                }

                is ProductState.Success -> {
                    loadFavoriteProducts()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                is ProductState.Error -> {
                    Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFavoriteProducts() {
        viewModel.loadFavoriteProducts()

    }

}