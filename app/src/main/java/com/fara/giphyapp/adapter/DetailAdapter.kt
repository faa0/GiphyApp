package com.fara.giphyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fara.giphyapp.databinding.ItemDetailBinding
import com.fara.giphyapp.db.model.Gifs

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Gifs>() {
        override fun areItemsTheSame(oldItem: Gifs, newItem: Gifs) = oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Gifs, newItem: Gifs) = oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailViewHolder(
        ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: DetailAdapter.DetailViewHolder, position: Int) {
        bind(holder, position)
    }

    override fun getItemCount() = differ.currentList.size

    private fun bind(holder: DetailAdapter.DetailViewHolder, position: Int) {
        val detail = differ.currentList[position]
        holder.binding.apply {
            Glide
                .with(ivDetail.context)
                .asGif()
                .load(detail.url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(ivDetail)
        }
    }
}