package com.movie.app.jetpack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movie.app.jetpack.data.repository.MovieRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: MovieRepository, private val onError : (String?) -> Unit): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MovieViewModel::class.java)){
            return MovieViewModel(repository, onError) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}