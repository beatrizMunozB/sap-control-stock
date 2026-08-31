package com.makita.controlstock.data.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
      /// fijarse en la ip que se cambia
      private const val BASE_URL = "http://172.16.128.184:3024/"
    //private const val BASE_URL = "http://10.0.2.2:3024/"

    //private const val BASE_URL = "http://172.16.1.206:3024/"    // Servidor
    //private const val BASE_URL = "http://172.16.1.234:3024/"    // dk-jherrera
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }


}