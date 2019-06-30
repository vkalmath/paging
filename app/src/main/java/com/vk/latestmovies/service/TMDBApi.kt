package com.vk.latestmovies.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

//API Kye
const val API_KEY = "762d3d555ea8de3401f5f7d7e1b61834"

const val BASE_URL = "https://api.themoviedb.org/3/movie/"

interface TMDBApi {

    //https://api.themoviedb.org/3/movie/top_rated?api_key=762d3d555ea8de3401f5f7d7e1b61834&language=en-US&page=1
    @GET("top_rated?api_key=762d3d555ea8de3401f5f7d7e1b61834&language=en-US")
    fun getTopRatedMovies(@Query("page") page: Int = 1) :
            Observable<Response>
}
