package com.fara.giphyapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fara.giphyapp.db.dao.GiphyDao
import com.fara.giphyapp.db.model.Gifs

@Database(
    entities = [Gifs::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getGiphyDao(): GiphyDao
}