package com.movie.app.jetpack.data.service

import com.movie.app.jetpack.model.MovieModel
import retrofit2.Response
import retrofit2.http.GET

interface MovieService {
    @GET("movies")
    suspend fun getMovies(): Response<List<MovieModel>>

    @GET("box_office")
    suspend fun getBoxOffice(): Response<List<MovieModel>>

    @GET("coming_soon")
    suspend fun getComingSoon(): Response<List<MovieModel>>
}