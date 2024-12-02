package com.example.ocs.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ocs.R
import com.example.ocs.adapters.MainRecyclerAdapter
import com.example.ocs.data.Product
import com.example.ocs.data.ProductState
import com.example.ocs.data.SupabaseClient.supabase
import com.example.ocs.viewmodel.SupabaseProductsViewModel
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var viewModel: SupabaseProductsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainRecyclerAdapter
    private lateinit var refreshView: SwipeRefreshLayout
    private var productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.recycler_View)
        refreshView = view.findViewById(R.id.refresh)
        viewModel = ViewModelProvider(this).get(SupabaseProductsViewModel::class.java)
        setupRecyclerView()
        loadProductsFromViewModel()

        refreshView.setOnRefreshListener {
            loadProductsFromViewModel()
        }

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = MainRecyclerAdapter(productList) { product ->
            Toast.makeText(requireContext(), "Вы выбрали: ${product.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Наблюдаем за изменениями в списке продуктов
        viewModel.productList.observe(viewLifecycleOwner) { products ->
            productList.clear()
            productList.addAll(products)
            adapter.notifyDataSetChanged()
            refreshView.isRefreshing = false// Обновляем данные адаптера
        }

        // Наблюдаем за состоянием загрузки
        viewModel.productState.observe(viewLifecycleOwner) { state ->
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
                    Log.d("Error", state.errorMessage)
                }
            }
        }
    }

    private fun loadProductsFromViewModel() {
        viewModel.loadProductInfo()
    }
}
