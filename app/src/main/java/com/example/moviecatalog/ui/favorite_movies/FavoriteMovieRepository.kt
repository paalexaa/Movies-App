package com.example.moviecatalog.ui.favorite_movies

import androidx.lifecycle.LiveData
import com.example.moviecatalog.data.db.AppDatabase
import com.example.moviecatalog.room.FavoriteMovie

class FavoriteMovieRepository (private val db: AppDatabase) {

    fun getAllFavorites(): LiveData<List<FavoriteMovie>> = db.favoriteMovieDao().getAllFavorites()

    fun isFavorite(movieId: Int): LiveData<Int> = db.favoriteMovieDao().isFavorite(movieId)

    suspend fun addFavorite(movie: FavoriteMovie) = db.favoriteMovieDao().addFavorite(movie)

    suspend fun removeFavorite(movieId: Int) = db.favoriteMovieDao().removeFavorite(movieId)
}
