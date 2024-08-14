package com.example.moviecatalog.ui.favorite_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviecatalog.data.vo.Movie
import com.example.moviecatalog.room.FavoriteMovie
import kotlinx.coroutines.launch

class FavoriteMovieViewModel(private val repository: FavoriteMovieRepository) : ViewModel() {

    private val _favoriteMovies = MutableLiveData<MutableList<Movie>>(mutableListOf())
    val favoriteMovies: LiveData<MutableList<Movie>> get() = _favoriteMovies

    fun toggleFavorite(movie: Movie) {
        val currentList = _favoriteMovies.value ?: mutableListOf()
        if (currentList.contains(movie)) {
            currentList.remove(movie)
        } else {
            currentList.add(movie)
        }
        _favoriteMovies.value = currentList
    }

    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.value?.contains(movie) ?: false
    }


    fun getAllFavorites(): LiveData<List<FavoriteMovie>> = repository.getAllFavorites()

    fun isFavorite(movieId: Int): LiveData<Int> = repository.isFavorite(movieId)

    fun addFavorite(movie: FavoriteMovie) = viewModelScope.launch {
        repository.addFavorite(movie)
    }

    fun removeFavorite(movieId: Int) = viewModelScope.launch {
        repository.removeFavorite(movieId)
    }
}
