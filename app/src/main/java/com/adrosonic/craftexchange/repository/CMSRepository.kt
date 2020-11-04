package com.adrosonic.craftexchange.repository

import com.adrosonic.craftexchange.repository.remote.*
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object CMSRepository {
    private fun <T> builder(endpoint: Class<T>): T {
        var lists = listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)


        return Retrofit.Builder()
            .baseUrl(ConstantsDirectory.CMS_URL_DEV)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY))
//                .connectionSpecs(Collections.singletonList(spec))
                .connectionSpecs(lists)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build())

            .build()
            .create(endpoint)
    }

    fun getCMSservice(): CMSDao {
        return builder(CMSDao::class.java)
    }

}