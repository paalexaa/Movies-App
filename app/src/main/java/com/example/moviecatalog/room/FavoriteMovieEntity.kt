package com.example.moviecatalog.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorites")
data class FavoriteMovie(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val title: String
)