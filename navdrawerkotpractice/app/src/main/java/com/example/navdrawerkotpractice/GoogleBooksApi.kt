package com.example.navdrawerkotpractice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    fun getBookInfo(@Query("q") query: String): Call<BookResponse>
}
