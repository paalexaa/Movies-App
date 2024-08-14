package com.example.moviecatalog.ui.movieDetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.moviecatalog.R
import com.example.moviecatalog.data.api.MovieDBClient
import com.example.moviecatalog.data.api.MovieDBInterface
import com.example.moviecatalog.data.api.POSTER_BASE_URL
import com.example.moviecatalog.data.db.AppDatabase
import com.example.moviecatalog.data.repository.NetworkState
import com.example.moviecatalog.data.vo.MovieDetails
import com.example.moviecatalog.databinding.ActivitySingleMovieBinding
import com.example.moviecatalog.room.FavoriteMovie
import com.example.moviecatalog.ui.favorite_movies.FavoriteMovieViewModel
import com.example.moviecatalog.ui.favorite_movies.FavoriteViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class SingleMovie : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var binding: ActivitySingleMovieBinding

    private val favoriteViewModel: FavoriteMovieViewModel by viewModels {
        FavoriteViewModelFactory(AppDatabase.getDatabase(applicationContext))
    }

    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingleMovieBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val movieId: Int = intent.getIntExtra("id", 1)

        val apiService: MovieDBInterface = MovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })


        favoriteViewModel.isFavorite(movieId).observe(this) { count ->
            isFavorite = count > 0
            updateFavoriteIcon()
        }

        binding.btnFavorite.setOnClickListener {
            if (isFavorite) {
                favoriteViewModel.removeFavorite(movieId)
            } else {
                val movie = FavoriteMovie(
                    id = movieId,
                    posterPath = "",
                    releaseDate = "",
                    title = ""
                )
                favoriteViewModel.addFavorite(movie)
            }
            isFavorite = !isFavorite
            updateFavoriteIcon()

            val resultIntent = Intent()
            resultIntent.putExtra("movieId", movieId)
            resultIntent.putExtra("isFavorite", isFavorite)
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun bindUI(it: MovieDetails) {

        binding.movieTitle.text = it.title
        binding.movieTagline.text = it.tagline
        binding.movieReleaseDate.text = it.releaseDate
        binding.movieRating.text = it.rating.toString()
        binding.movieRuntime.text = it.runtime.toString() + " minutes"
        binding.movieOverview.text = it.overview

        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        binding.movieBudget.text = formatCurrency.format(it.budget)

        val PosterURL: String = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(PosterURL)
            .into(binding.moviePoster)

        val moviePosterURL: String = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(binding.ivMoviePoster)

    }

    private fun getViewModel(movieId: Int): MovieViewModel {

        return ViewModelProviders.of(this, object : ViewModelProvider. Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(movieRepository, movieId) as T
            }
        })[MovieViewModel::class.java]
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.favorite_red_24)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.favorite_border_red_24)
        }
    }
}