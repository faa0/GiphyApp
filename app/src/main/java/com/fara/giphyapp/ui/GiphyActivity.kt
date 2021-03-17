package com.fara.giphyapp.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fara.giphyapp.R
import com.fara.giphyapp.adapter.GiphyAdapter
import com.fara.giphyapp.databinding.ActivityGifsBinding
import com.fara.giphyapp.db.model.Gifs
import com.fara.giphyapp.util.Constants
import com.fara.giphyapp.util.Resource
import com.fara.giphyapp.viewmodel.GiphyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GiphyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGifsBinding
    private val viewModel: GiphyViewModel by viewModels()
    private lateinit var adapter: GiphyAdapter
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGifsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            setupGiphyRecyclerView()

            if (isOnline(this@GiphyActivity)) {
                viewModel.deleteGifs()
                viewModel.getTrending()
            } else {
                lifecycleScope.launch { adapter.differ.submitList(viewModel.getGifs()) }
                Toast.makeText(this@GiphyActivity, "Turn on the Internet", Toast.LENGTH_SHORT)
                    .show()
            }

            viewModel.giphyLD.observe(this@GiphyActivity, { resource ->
                when (resource) {
                    is Resource.Success -> {
                        isLoading = false
                        resource.data?.let {
                            val totalCount =
                                it.pagination.total_count / Constants.LIMIT_OF_RECORDS + 2
                            isLastPage = viewModel.giphyOffset == totalCount
                            if (isLastPage) {
                                rvTrending.setPadding(0, 0, 0, 0)
                            }

                            val gifs = mutableListOf<Gifs>()
                            gifs.clear()
                            it.data.forEach { data ->
                                gifs.addAll(listOf(Gifs(data.title, data.images.fixed_height.url)))
                            }
                            adapter.differ.submitList(gifs)

                            if (it.data.isEmpty()) {
                                Toast.makeText(
                                    this@GiphyActivity,
                                    getString(R.string.incorrect_query),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        isLoading = false
                        resource.message?.let {
                            Log.d("TAG", "An error occured: $it")
                        }
                    }
                    is Resource.Loading -> {
                        isLoading = true
                    }
                }
            })

            adapter.setOnItemClickListener { position, gifs ->
                val intent = Intent(this@GiphyActivity, DetailActivity::class.java)
                intent.putExtra("position", position)
                startActivity(intent)
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    when {
                        etSearch.text.isNullOrEmpty() -> {
                            viewModel.giphyResponse = null
                            viewModel.giphyOffset = 0
                            viewModel.deleteGifs()
                            viewModel.getTrending()
                        }
                        else -> {
                            viewModel.giphyResponse = null
                            viewModel.giphyOffset = 0
                            viewModel.deleteGifs()
                            viewModel.searchGifs(etSearch.text.toString())
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvTrending.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.LIMIT_OF_RECORDS
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                binding.apply {
                    if (etSearch.text.isNullOrEmpty()) viewModel.getTrending()
                    else viewModel.searchGifs(etSearch.text.toString())
                    isScrolling = false
                }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(TRANSPORT_WIFI) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> {
                    Log.i("TAG", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun setupGiphyRecyclerView() {
        adapter = GiphyAdapter()
        binding.apply {
            rvTrending.adapter = adapter
            if (isOnline(this@GiphyActivity)) {
                rvTrending.addOnScrollListener(this@GiphyActivity.scrollListener)
            } else {
                rvTrending.removeOnScrollListener(this@GiphyActivity.scrollListener)
            }
        }
    }
}