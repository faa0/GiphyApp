package com.fara.giphyapp.data.model

data class GiphyResponse(
    val data: MutableList<Data>,
    val meta: Meta,
    val pagination: Pagination
)