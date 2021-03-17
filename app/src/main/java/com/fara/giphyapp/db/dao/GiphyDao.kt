package com.fara.giphyapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fara.giphyapp.db.model.Gifs

@Dao
interface GiphyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gifs: Gifs)

    @Query("SELECT * FROM gifs")
    suspend fun getGifs(): List<Gifs>

    @Query("DELETE FROM gifs")
    suspend fun deleteGifs()
}