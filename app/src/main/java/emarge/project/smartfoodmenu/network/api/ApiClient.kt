package emarge.project.caloriecaffe.network.api


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Provides
import dagger.Reusable
import emarge.project.smartfoodmenu.BuildConfig

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by kamal on 8/2/18.
 */

object ApiClient {

    val baseUrl = BuildConfig.API_BASE_URL

    @Reusable
    @Singleton
    fun client(): APIInterface {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(APIInterface::class.java)
    }


    private fun provideOkHttpClient(): OkHttpClient {
        val okhttpClientBuilder = OkHttpClient.Builder()
        okhttpClientBuilder.connectTimeout(20, TimeUnit.SECONDS)
        okhttpClientBuilder.readTimeout(20, TimeUnit.SECONDS)
        okhttpClientBuilder.writeTimeout(20, TimeUnit.SECONDS)

        return okhttpClientBuilder.build()
    }

 /*   private lateinit var retrofit: Retrofit

    val retrofitInstance: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            }
            return retrofit
        }


    // .baseUrl(URL_BASE)
    val clientAuthentication: Retrofit?
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            val builder = Retrofit.Builder()
                .baseUrl(URL_BASE)
            val interceptor = AuthenticationInterceptor(
                Credentials.basic("", "")
            )
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
                httpClient.addInterceptor(logging)
                builder.client(httpClient.build())
                retrofit = builder
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()

            }

            return retrofit
        }

    class AuthenticationInterceptor(private val authToken: String) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val original = chain.request()

            val builder = original.newBuilder()
                .header("Authorization", authToken)//Remember header() vs addHeader

            val request = builder.build()
            return chain.proceed(request)
        }
    }*/
}
