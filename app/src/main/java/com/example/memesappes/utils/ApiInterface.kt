package com.example.memesappes.utils

import com.example.memesappes.models.ForgotUser
import com.example.memesappes.models.User
import okhttp3.RequestBody

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

val BackendUrl = "http://10.0.2.2:3000/"
interface ApiInterface {

    @POST("users/login")
    fun seConnecter(@Body info: RequestBody): Call<User>

    @POST("users/register")
    fun createUser(@Body info: RequestBody): Call<User>

    @PUT("users/resetpassword")
    fun sendMailForgetPassword(@Body info: RequestBody): Call<ForgotUser>

    @PUT("users/sendmodifiedpassword")
    fun sendModifiedPassword(@Body info: RequestBody): Call<ForgotUser>

    @GET("users/emailexist/{email}")
    fun EmailExist(@Path("email") email: String):Call<User>

    companion object {

        var BASE_URL = "http://10.0.2.2:3000/"

        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }



}