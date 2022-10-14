package com.movie.app.jetpack.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie.app.jetpack.data.repository.MovieRepository
import com.movie.app.jetpack.model.MovieModel
import com.movie.app.jetpack.ui.viewmodel.model.MovieTypes
import com.movie.app.jetpack.ui.viewmodel.model.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieViewModel(
    private val repository: MovieRepository,
    private val onError: (String?) -> Unit
) : ViewModel() {
    private var movieList: MutableLiveData<Resource<List<MovieModel>>> = MutableLiveData()
    val movieListVal: LiveData<Resource<List<MovieModel>>> get() = movieList

    private val handler = CoroutineExceptionHandler { _, error ->
        onError(error.message)
    }

    //if the item is in the adapter list there is a possibility to display the item
    //there is no search function implemented by title in the api
    //can fetch and search all data, but what if data is large
    //but this api is small could be implement with MediatorLiveData with getMovies()
    //+ getBoxOffice() + getComingSoon()
    //https://developer.android.com/reference/androidx/lifecycle/MediatorLiveData
    fun getMovies(type:Int) {
        viewModelScope.launch(handler) {
            val response:Response<List<MovieModel>> = when (type) {
                MovieTypes.BOX_OFFICE.type -> {
                    repository.getBoxOffice()
                }
                MovieTypes.COMING_SOON.type -> {
                    repository.getComingSoon()
                }
                else -> {
                    repository.getMovies()
                }
            }
            if(response.isSuccessful){
                response.body()?.let {
                    movieList.value = Resource.success(it)
                }
            }else{
                movieList.value = Resource.error("Failed",null)
            }

            /*.enqueue(object : Callback<List<MovieModel>> {
                override fun onResponse(
                    call: Call<List<MovieModel>>,
                    response: Response<List<MovieModel>>
                ) {
                    response.body()?.let {
                        movieList.value = Resource.success(it)
                    }

                }

                override fun onFailure(call: Call<List<MovieModel>>, t: Throwable) {

                    t.message?.let {
                        movieList.value = Resource.error(it, null)
                    }
                }
            })*/

        }
    }




}