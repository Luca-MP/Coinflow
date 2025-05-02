package it.pezzotta.coinflow.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.pezzotta.coinflow.Constants
import it.pezzotta.coinflow.data.remote.CoinService
import it.pezzotta.coinflow.data.repository.CoinRepository
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DIModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    @CoinOkHttpClient
    @Singleton
    @Provides
    fun provideHttpClient(@ApplicationContext context: Context) = OkHttpClient.Builder().apply {
        readTimeout(10, TimeUnit.SECONDS)
        addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        addInterceptor { chain ->
            val request: Request = chain.request().newBuilder().build()
            chain.proceed(request)
        }

        cache(Cache(File(context.cacheDir, "http_cache"), 300L * 1024L * 1024L))

    }.build()

    @CoinRetrofit
    @Singleton
    @Provides
    fun provideCoinRetrofit(@CoinOkHttpClient httpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(httpClient)
            .build()

    @Singleton
    @Provides
    fun provideCoinService(@CoinRetrofit retrofit: Retrofit): CoinService =
        retrofit.create(CoinService::class.java)

    @Singleton
    @Provides
    fun provideCoinRepository(coinService: CoinService) = CoinRepository(coinService)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinOkHttpClient
}
