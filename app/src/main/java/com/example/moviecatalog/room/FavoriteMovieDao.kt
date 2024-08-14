package com.example.movieapp.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moviecatalog.room.FavoriteMovie

@Dao
interface FavoriteMovieDao {
    @Insert
    suspend fun addFavorite(movie: FavoriteMovie)

    @Query("DELETE FROM favorites WHERE id = :movieId")
    suspend fun removeFavorite(movieId: Int)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): LiveData<List<FavoriteMovie>>

    @Query("SELECT COUNT(*) FROM favorites WHERE id = :movieId")
    fun isFavorite(movieId: Int): LiveData<Int>
}