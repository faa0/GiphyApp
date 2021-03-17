package com.fara.giphyapp.repo

import androidx.lifecycle.ViewModel
import com.fara.giphyapp.data.GiphyApi
import javax.inject.Inject

class RemoteRepo @Inject constructor(
    private val api: GiphyApi
) : ViewModel() {

    suspend fun getTrending(limit: Int, offset: Int) =
        api.getTrending(limit = limit, offset = offset)

    suspend fun searchGifs(query: String, limit: Int, offset: Int) =
        api.searchGifs(q = query, limit = limit, offset = offset)
}