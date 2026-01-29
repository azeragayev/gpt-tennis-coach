package com.aaa.gptenniscoach.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.aaa.gptenniscoach.core.Jsons
import com.aaa.gptenniscoach.data.api.TennisCoachApi
import com.aaa.gptenniscoach.data.db.AppDatabase
import com.aaa.gptenniscoach.data.repo.SessionRepository
import com.aaa.gptenniscoach.data.repo.SessionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // TODO: Set your backend base URL. Must end with "/"
    private const val BASE_URL = "https://overexertedly-administrational-brittney.ngrok-free.dev/"

    /**
     * Provides a lenient JSON serializer instance for API communication.
     */
    @Provides @Singleton
    fun provideJson(): Json = Jsons.lenient

    /**
     * Provides a configured OkHttpClient with logging and timeout settings.
     */
    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .callTimeout(java.time.Duration.ofSeconds(180))
            .connectTimeout(java.time.Duration.ofSeconds(30))
            .readTimeout(java.time.Duration.ofSeconds(180))
            .writeTimeout(java.time.Duration.ofSeconds(180))
            .build()
    }

    /**
     * Provides a configured Retrofit instance with JSON serialization converter.
     */
    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * Provides the Retrofit-based TennisCoachApi service for backend communication.
     */
    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): TennisCoachApi =
        retrofit.create(TennisCoachApi::class.java)

    /**
     * Provides the Room database instance for local session storage.
     */
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        AppDatabase.build(ctx)

    /**
     * Provides the SessionRepository implementation for managing analysis sessions.
     */
    @Provides @Singleton
    fun provideSessionRepository(
        @ApplicationContext ctx: Context,
        db: AppDatabase,
        api: TennisCoachApi,
        json: Json
    ): SessionRepository = SessionRepositoryImpl(ctx, db.sessionDao(), api, json)
}
