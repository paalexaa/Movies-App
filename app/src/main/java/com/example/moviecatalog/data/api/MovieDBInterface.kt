package com.example.moviecatalog.data.api

import com.example.moviecatalog.data.vo.MovieDetails
import com.example.moviecatalog.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDBInterface {

//    https://api.themoviedb.org/3/movie/popular?api_key=6d5ace5bca1ebc26a3c69cb07995bcc3
//    https://api.themoviedb.org/3/movie/299534?api_key=6d5ace5bca1ebc26a3c69cb07995bcc3
//    https://api.themoviedb.org/3/

    @GET("movie/popular")
    fun getPopularMovie(@Query("page") page: Int) : Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int): Single<MovieDetails>
}