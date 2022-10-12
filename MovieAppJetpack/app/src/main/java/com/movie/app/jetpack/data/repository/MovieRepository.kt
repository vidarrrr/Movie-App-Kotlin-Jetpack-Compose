package com.movie.app.jetpack.data.repository

import com.movie.app.jetpack.data.source.remote.RetrofitHelper
import com.movie.app.jetpack.model.MovieModel
import retrofit2.Response

class MovieRepository {
    suspend fun getMovies(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getMovies()
    }
    suspend fun getBoxOffice(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getBoxOffice()
    }
    suspend fun getComingSoon(): Response<List<MovieModel>> {
        return RetrofitHelper.api.getComingSoon()
    }

}