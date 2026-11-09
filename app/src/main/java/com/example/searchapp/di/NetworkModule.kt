package com.example.searchapp.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import com.example.searchapp.data.remote.OpenLibraryApi
import com.example.searchapp.data.repository.BookRepositoryImpl
import com.example.searchapp.domain.repository.BookRepository
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        implementation: BookRepositoryImpl
    ): BookRepository

    companion object {

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://openlibrary.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideOpenLibraryApi(
            retrofit: Retrofit
        ): OpenLibraryApi {
            return retrofit.create(OpenLibraryApi::class.java)
        }
    }
}