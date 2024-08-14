package com.example.moviecatalog.ui.favorite_movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviecatalog.data.db.AppDatabase

class FavoriteViewModelFactory (private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoriteMovieViewModel(FavoriteMovieRepository(db)) as T
    }
}