package com.fara.giphyapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fara.giphyapp.data.model.GiphyResponse
import com.fara.giphyapp.db.model.Gifs
import com.fara.giphyapp.repo.LocalRepo
import com.fara.giphyapp.repo.RemoteRepo
import com.fara.giphyapp.util.Constants.Companion.LIMIT_OF_RECORDS
import com.fara.giphyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GiphyViewModel @Inject constructor(
    private val remoteRepo: RemoteRepo,
    private val localRepo: LocalRepo
) : ViewModel() {

    val giphyLD = MutableLiveData<Resource<GiphyResponse>?>()
    var giphyOffset = 0
    var giphyResponse: GiphyResponse? = null

    suspend fun getGifs(): List<Gifs> = localRepo.getGifs()

    fun deleteGifs() = viewModelScope.launch { localRepo.deleteGifs() }

    fun searchGifs(query: String) = viewModelScope.launch {
        giphyLD.postValue(Resource.Loading())
        val response = remoteRepo.searchGifs(query, LIMIT_OF_RECORDS, giphyOffset)
        giphyLD.postValue(handleMoviesResponse(response))

        response.body()?.data?.forEach {
            localRepo.insert(Gifs(it.title, it.images.fixed_height.url))
        }
    }

    fun getTrending() = viewModelScope.launch {
        giphyLD.postValue(Resource.Loading())
        val response = remoteRepo.getTrending(LIMIT_OF_RECORDS, giphyOffset)
        giphyLD.postValue(handleMoviesResponse(response))

        response.body()?.data?.forEach {
            localRepo.insert(Gifs(it.title, it.images.fixed_height.url))
        }
    }

    private fun handleMoviesResponse(response: Response<GiphyResponse>): Resource<GiphyResponse> {
        if (response.isSuccessful) {
            response.body()?.let { it ->
                giphyOffset += LIMIT_OF_RECORDS
                if (giphyResponse == null) {
                    giphyResponse = it
                } else {
                    val oldGifs = giphyResponse?.data
                    val newGifs = it.data
                    oldGifs?.addAll(newGifs)
                }
                return Resource.Success(giphyResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }
}