package com.example.ocs.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ocs.NetworkUtils
import com.example.ocs.R
import com.example.ocs.activities.ItemActivity
import com.example.ocs.activities.SearchActivity
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
    private lateinit var network: LinearLayout
    private var productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.recycler_View)
        refreshView = view.findViewById(R.id.refresh)
        network = view.findViewById(R.id.network)
        viewModel = ViewModelProvider(this).get(SupabaseProductsViewModel::class.java)
        checkInternetConnection()


        refreshView.setOnRefreshListener {
            loadProductsFromViewModel()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_home_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)

        // Установка обработчика клика для элемента меню
        searchItem.setOnMenuItemClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
            true
        }
    }



    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = MainRecyclerAdapter(productList, { product ->
            val intent = Intent(requireContext(), ItemActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }, { product ->
            viewModel.addProductToFavorites(product.id)
        })
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

    private fun checkInternetConnection() {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            network.visibility = View.VISIBLE
        } else {
            network.visibility = View.GONE
            setupRecyclerView()
            loadProductsFromViewModel()
        }
    }
}
