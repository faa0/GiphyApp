package com.fara.giphyapp.di

import android.content.Context
import androidx.room.Room
import com.fara.giphyapp.data.GiphyApi
import com.fara.giphyapp.db.AppDatabase
import com.fara.giphyapp.repo.LocalRepo
import com.fara.giphyapp.repo.RemoteRepo
import com.fara.giphyapp.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "giphy_db.db",
    ).build()

    @Singleton
    @Provides
    fun provideLocalRepo(db: AppDatabase): LocalRepo = LocalRepo(db)

    @Singleton
    @Provides
    fun provideGiphyApi(): GiphyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GiphyApi::class.java)

    @Singleton
    @Provides
    fun provideGiphyRepository(api: GiphyApi): RemoteRepo = RemoteRepo(api)
}