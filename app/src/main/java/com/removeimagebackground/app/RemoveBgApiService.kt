package com.removeimagebackground.app

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RemoveBgApiService {
    @Multipart
    @POST("removebg")
    fun removeBg(
        @Header("X-Api-Key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.remove.bg/v1.0/"

    val instance: RemoveBgApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(RemoveBgApiService::class.java)
    }
}
