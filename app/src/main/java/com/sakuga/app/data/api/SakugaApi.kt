package com.sakuga.app.data.api

import retrofit2.Response
import retrofit2.http.*

interface SakugaApi {

    // GET /post.json?limit=20&page=1&tags=tagname&login=x&password_hash=y
    @GET("post.json")
    suspend fun getPosts(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("tags") tags: String = "",
        @Query("login") login: String? = null,
        @Query("password_hash") passwordHash: String? = null
    ): Response<List<PostDto>>

    // GET /post/show.json?id=12345
    @GET("post/show.json")
    suspend fun getPost(
        @Query("id") id: Int,
        @Query("login") login: String? = null,
        @Query("password_hash") passwordHash: String? = null
    ): Response<PostDto>

    // GET /tag.json?name=tag*&limit=10&order=count
    @GET("tag.json")
    suspend fun getTags(
        @Query("name") namePattern: String,
        @Query("limit") limit: Int = 10,
        @Query("order") order: String = "count",
        @Query("login") login: String? = null,
        @Query("password_hash") passwordHash: String? = null
    ): Response<List<TagDto>>

    // GET /user.json?login=username&password_hash=hash
    // Moebooru uses this to verify credentials + get user info
    @GET("user.json")
    suspend fun getUsers(
        @Query("name") name: String,
        @Query("login") login: String,
        @Query("password_hash") passwordHash: String
    ): Response<List<LoginResponse>>

    // POST /favorite/create.json
    @FormUrlEncoded
    @POST("favorite/create.json")
    suspend fun addFavorite(
        @Field("id") postId: Int,
        @Field("login") login: String,
        @Field("password_hash") passwordHash: String
    ): Response<LoginResponse>

    // POST /favorite/destroy.json
    @FormUrlEncoded
    @POST("favorite/destroy.json")
    suspend fun removeFavorite(
        @Field("id") postId: Int,
        @Field("login") login: String,
        @Field("password_hash") passwordHash: String
    ): Response<LoginResponse>
}
