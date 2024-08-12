package com.example.moviecatalog.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviecatalog.data.api.MovieDBInterface
import com.example.moviecatalog.data.api.POST_PER_PAGE
import com.example.moviecatalog.data.repository.MovieDataSource
import com.example.moviecatalog.data.repository.MovieDataSourceFactory
import com.example.moviecatalog.data.repository.NetworkState
import com.example.moviecatalog.data.vo.Movie
import io.reactivex.disposables.CompositeDisposable

class MoviePageListRepository (private val apiService: MovieDBInterface) {

    lateinit var moviePageList: LiveData<PagedList<Movie>>
    lateinit var moviesDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePageList (compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        moviesDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        moviePageList = LivePagedListBuilder(moviesDataSourceFactory, config).build()
        return moviePageList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        val networkStateLiveData = MutableLiveData<NetworkState>()

        moviesDataSourceFactory.moviesLiveDataSource.observeForever { movieDataSource ->
            movieDataSource?.let {
                networkStateLiveData.postValue(it.networkState.value)
            }
        }
        return networkStateLiveData
    }
}