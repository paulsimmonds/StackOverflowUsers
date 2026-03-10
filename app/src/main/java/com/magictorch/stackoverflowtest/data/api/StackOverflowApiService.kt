package com.magictorch.stackoverflowtest.data.api

import com.magictorch.stackoverflowtest.data.dto.tag.TagsResponse
import com.magictorch.stackoverflowtest.data.dto.user.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StackOverflowApiService {
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "reputation",
        @Query("site") site: String = "stackoverflow",
        @Query("inname") inname: String? = null,
    ): UsersResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "reputation",
        @Query("site") site: String = "stackoverflow",
    ): UsersResponse

    @GET("users/{id}/top-tags")
    suspend fun getTopTags(
        @Path("id") id: Int,
        @Query("site") site: String = "stackoverflow",
    ): TagsResponse
}
