package com.adrosonic.craftexchangemarketing.repository

import com.adrosonic.craftexchangemarketing.repository.remote.*
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ImageDownloadRepository {
    private fun <T> builder(endpoint: Class<T>): T {
        var lists = listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)


        return Retrofit.Builder()
            .baseUrl(ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectionSpecs(lists)
                .connectTimeout(60,TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
        .build())

            .build()
            .create(endpoint)
    }

    fun getProductService(): ProductDao {
        return builder(ProductDao::class.java)
    }

    fun getBuyerOwnDesignService(): BuyerOwnDesignDao {
        return builder(BuyerOwnDesignDao::class.java)
    }


}