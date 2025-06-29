package natour.dev.zonetechnologiestask.network

import natour.dev.zonetechnologiestask.core.util.Constants
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TraccarApiClient {
    val api by lazy {
        val credentials = Credentials.basic("admin", "admin")

        val client = OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", credentials)
                    .addHeader("Content-Type", "application/json")
                    .build()

                chain.proceed(request)
            }.build()

        Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TraccarApi::class.java)
    }
}