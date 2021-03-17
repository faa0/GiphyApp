package com.fara.giphyapp.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gifs")
data class Gifs(
    val title: String,
    @PrimaryKey
    val url: String
)