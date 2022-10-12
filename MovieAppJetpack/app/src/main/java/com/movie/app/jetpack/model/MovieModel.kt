package com.movie.app.jetpack.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class MovieModel(
    @SerializedName("Title") var title: String,
    @SerializedName("Year") var year: String,
    @SerializedName("Runtime") var runtime: String,
    @SerializedName("Poster") var poster: String,
    @SerializedName("Genres") var genres: String,
    @SerializedName("Description") var description: String,
    @SerializedName("Actors") var actors: List<ActorModel>
) : Parcelable
