package com.jcyq.productos.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jcyq.productos.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración única de Retrofit para toda la app.
 * La URL base viene de gradle.properties (apiBaseUrl) a través de BuildConfig.
 */
object RetrofitClient {

    // serializeNulls: al modificar, una descripción vacía se envía como null y se borra en la API.
    val gson: Gson = GsonBuilder().serializeNulls().create()

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val cliente = OkHttpClient.Builder()
        // Pide siempre JSON a Laravel
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
            )
        }
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: ProductoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ProductoApiService::class.java)
    }
}
