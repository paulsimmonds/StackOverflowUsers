package com.magictorch.stackoverflowtest.data.api

import com.magictorch.stackoverflowtest.data.dto.user.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StackOverflowApiService {
    @GET("2.2/users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "reputation",
        @Query("site") site: String = "stackoverflow"
    ): UsersResponse
}
