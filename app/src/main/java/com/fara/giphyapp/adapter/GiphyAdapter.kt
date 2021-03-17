package com.fara.giphyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fara.giphyapp.databinding.ItemGifsBinding
import com.fara.giphyapp.db.model.Gifs

class GiphyAdapter : RecyclerView.Adapter<GiphyAdapter.GiphyViewHolder>() {

    inner class GiphyViewHolder(val binding: ItemGifsBinding) :
        RecyclerView.ViewHolder(binding.root)

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Gifs>() {
        override fun areItemsTheSame(oldItem: Gifs, newItem: Gifs) = oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: Gifs, newItem: Gifs) = oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GiphyViewHolder(
        ItemGifsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GiphyViewHolder, position: Int) {
        bind(holder, position)
    }

    override fun getItemCount() = differ.currentList.size

    private fun bind(holder: GiphyViewHolder, position: Int) {
        val gifs = differ.currentList[position]
        holder.binding.apply {
            Glide
                .with(ivMain.context)
                .asGif()
                .load(gifs.url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(ivMain)
            tvTitle.text = gifs.title

            root.setOnClickListener { onItemClickListener?.let { it(position, gifs) } }
        }
    }

    private var onItemClickListener: ((Int, Gifs) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int, Gifs) -> Unit) {
        onItemClickListener = listener
    }
}