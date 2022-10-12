package com.movie.app.jetpack.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActorModel(
    @SerializedName("UserPhotoURL") var userPhotoURL : String,
    @SerializedName("UserName") var userName : String,
    @SerializedName("UserRole") var userRole : String
): Parcelable
