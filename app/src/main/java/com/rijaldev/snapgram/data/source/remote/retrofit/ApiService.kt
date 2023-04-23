package com.rijaldev.snapgram.data.source.remote.retrofit

import com.rijaldev.snapgram.data.source.remote.response.DetailStoryResponse
import com.rijaldev.snapgram.data.source.remote.response.LoginResponse
import com.rijaldev.snapgram.data.source.remote.response.RegisterResponse
import com.rijaldev.snapgram.data.source.remote.response.StoryResponse
import com.rijaldev.snapgram.data.source.remote.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0,
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null,
    ): StoryUploadResponse
}