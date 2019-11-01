package com.aaron.basemvvmlibrary2.net

import androidx.lifecycle.LiveData
import com.aaron.basemvvmlibrary2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface WanApi {
    companion object {
        fun get(): WanApi {
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(loggingInterceptor)
            }
            return Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .client(clientBuilder.build())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WanApi::class.java)
        }
    }

//    /**
//     * 公众号文章
//     */
//    @GET("wxarticle/list/{id}/{page}/json")
//    fun wxArticlePage(
//        @Path("id") id: Int,
//        @Path("page") page: Int
//    ): LiveData<ApiResponse<PageVO<ArticleVO>>>
//
//    /**
//     * 登录
//     */
//    @POST("user/login")
//    fun login(
//        @Query("username") username: String,
//        @Query("password") password: String
//    ): LiveData<ApiResponse<LoginUserVO>>


    /**
     * 收藏
     */
    @POST("lg/collect/{id}/json")
    fun collect(@Path("id") articleId: Int): LiveData<ApiResponse<String>>

    /**
     * 取消收藏
     */
    @POST("lg/uncollect_originId/{id}/json")
    fun uncollect(@Path("id") articleId: Int): LiveData<ApiResponse<String>>

}