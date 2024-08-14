package com.example.moviecatalog.ui.popular_movie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviecatalog.R
import com.example.moviecatalog.data.api.MovieDBClient
import com.example.moviecatalog.data.api.MovieDBInterface
import com.example.moviecatalog.data.db.AppDatabase
import com.example.moviecatalog.data.repository.NetworkState
import com.example.moviecatalog.databinding.ActivityMainBinding
import com.example.moviecatalog.databinding.MovieListItemBinding
import com.example.moviecatalog.room.FavoriteMovie
import com.example.moviecatalog.ui.favorite_movies.FavoriteMovieViewModel
import com.example.moviecatalog.ui.favorite_movies.FavoriteViewModelFactory
import com.example.moviecatalog.ui.movieDetails.SingleMovie

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    lateinit var movieRepository: MoviePageListRepository
    private lateinit var binding: ActivityMainBinding
    private lateinit var secondBinding: MovieListItemBinding

    private val favoriteViewModel: FavoriteMovieViewModel by viewModels {
        FavoriteViewModelFactory(AppDatabase.getDatabase(applicationContext))
    }

    private lateinit var movieAdapter: PopularMovieAdapter
    private var favoriteMovies: List<FavoriteMovie> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        secondBinding = MovieListItemBinding.inflate(layoutInflater)

        val apiService : MovieDBInterface = MovieDBClient.getClient()
        movieRepository = MoviePageListRepository(apiService)
        viewModel = getViewModel()

        movieAdapter = PopularMovieAdapter(this) { movie ->
            val intent = Intent(this, SingleMovie::class.java)
            intent.putExtra("id", movie.id)
            startActivityForResult(intent, REQUEST_CODE_SINGLE_MOVIE)
        }

        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType: Int = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1
                else return 2
            }
        }

        binding.rvMovieList.layoutManager = gridLayoutManager
        binding.rvMovieList.setHasFixedSize(true)
        binding.rvMovieList.adapter = movieAdapter

        viewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer{
            Log.d("NetworkState", "Network state changed: $it")
            binding.progressBarPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtErrorPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })

        favoriteViewModel.getAllFavorites().observe(this) { favorites ->
            favoriteMovies = favorites
            movieAdapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SINGLE_MOVIE && resultCode == RESULT_OK) {
            val movieId = data?.getIntExtra("movieId", -1) ?: return
            val isFavorite = data.getBooleanExtra("isFavorite", false)
            updateFavoriteIcon(movieId, isFavorite)
        }
    }

    public fun isFavorite(movieId: Int): Boolean {
        return favoriteMovies.any { it.id == movieId }
    }

    private fun updateFavoriteIcon(movieId: Int, isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.favorite_red_24 else R.drawable.favorite_border_red_24
        movieAdapter.updateFavoriteIcon(movieId, iconRes)
    }

    private fun getViewModel(): MainActivityViewModel {

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(movieRepository) as T
            }
        }) [MainActivityViewModel::class.java]
    }

    companion object {
        const val REQUEST_CODE_SINGLE_MOVIE = 1
    }
}