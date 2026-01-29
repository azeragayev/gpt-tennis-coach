package com.aaa.gptenniscoach.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
/**
 * Retrofit API interface for communicating with the Tennis Coach backend.
 */
interface TennisCoachApi {

    /**
     * Uploads a video for analysis with player metadata and returns analysis results.
     */
    @Multipart
    @POST("v1/analyze/video")
    suspend fun analyzeVideo(
        @Part video: MultipartBody.Part,
        @Part("meta") meta: RequestBody,
        // @Part("includeOverlay") includeOverlay: RequestBody
        // ✅ correct: query param, snake_case
        @Query("include_overlay") includeOverlay: Int = 1
    ): ResponseBody

}



