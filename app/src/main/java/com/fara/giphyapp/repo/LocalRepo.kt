package com.fara.giphyapp.repo

import androidx.lifecycle.ViewModel
import com.fara.giphyapp.db.AppDatabase
import com.fara.giphyapp.db.model.Gifs
import javax.inject.Inject

class LocalRepo @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    suspend fun insert(gifs: Gifs) = db.getGiphyDao().insert(gifs)

    suspend fun getGifs() = db.getGiphyDao().getGifs()

    suspend fun deleteGifs() = db.getGiphyDao().deleteGifs()
}