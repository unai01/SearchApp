
package com.example.searchapp.di

import com.example.searchapp.data.config.ApiConstants
import com.example.searchapp.data.remote.OpenLibraryApi
import com.example.searchapp.data.repository.BookRepositoryImpl
import com.example.searchapp.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import java.util.concurrent.TimeUnit
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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

        private const val CONNECT_TIMEOUT_SECONDS = 10L
        private const val READ_TIMEOUT_SECONDS = 15L

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(
                    CONNECT_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
                )
                .readTimeout(
                    READ_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
                )
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(okHttpClient)
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