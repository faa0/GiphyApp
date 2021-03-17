package com.fara.giphyapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fara.giphyapp.adapter.DetailAdapter
import com.fara.giphyapp.databinding.ActivityDetailBinding
import com.fara.giphyapp.viewmodel.GiphyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var adapter: DetailAdapter
    private val viewModel: GiphyViewModel by viewModels()

    companion object {
        const val DEFAULT_POSITION_VALUE = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val position = intent.getIntExtra("position", DEFAULT_POSITION_VALUE)

        setupDetailViewPager()

        binding.apply {

            lifecycleScope.launch { adapter.differ.submitList(viewModel.getGifs()) }
            detailPager.postDelayed(
                { detailPager.setCurrentItem(position, false) }, 100
            )
        }
    }

    private fun setupDetailViewPager() {
        adapter = DetailAdapter()
        binding.apply {
            detailPager.adapter = adapter
        }
    }
}