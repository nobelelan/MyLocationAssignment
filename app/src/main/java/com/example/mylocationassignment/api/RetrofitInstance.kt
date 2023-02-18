package com.example.mylocationassignment.api

import com.example.mylocationassignment.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{
        private val okHttpClient = OkHttpClient.Builder().build()
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val placesApi by lazy { retrofit.create(PlacesApi::class.java) }
    }
}