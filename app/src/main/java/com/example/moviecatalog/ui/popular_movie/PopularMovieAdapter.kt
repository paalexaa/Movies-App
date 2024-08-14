package com.example.moviecatalog.ui.popular_movie


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviecatalog.R
import com.example.moviecatalog.data.api.POSTER_BASE_URL
import com.example.moviecatalog.data.repository.NetworkState
import com.example.moviecatalog.data.vo.Movie
import com.example.moviecatalog.databinding.MovieListItemBinding
import com.example.moviecatalog.databinding.NetworkStateItemBinding
import com.example.moviecatalog.ui.movieDetails.SingleMovie

class PopularMovieAdapter(public var context: Context,
                          private val onFavoriteClick: (Movie) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == MOVIE_VIEW_TYPE) {
            val binding = MovieListItemBinding.inflate(layoutInflater, parent, false)
            MovieItemViewHolder(binding, onFavoriteClick)
        } else {
            val binding = NetworkStateItemBinding.inflate(layoutInflater, parent, false)
            NetworkStateItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    class MovieItemViewHolder (private val binding: MovieListItemBinding, private val onFavoriteClick: (Movie) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie?, context: Context) {
            binding.tvTitle.text = movie?.title
            binding.tvReleaseDate.text = movie?.title

            val moviePosterURL = POSTER_BASE_URL + movie?.posterPath
            Glide.with(binding.root.context)
                .load(moviePosterURL)
                .into(binding.posterImageView)

            binding.root.setOnClickListener {
                val intent = Intent(context, SingleMovie::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }

            binding.btnFavoriteItem.setOnClickListener {
                onFavoriteClick(movie!!)
            }

            val isFavorite = (context as MainActivity).isFavorite(movie!!.id)
            binding.btnFavoriteItem.setImageResource(if (isFavorite) R.drawable.favorite_red_24 else R.drawable.favorite_border_red_24)
        }
    }

    class NetworkStateItemViewHolder (val binding: NetworkStateItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                binding.progressBarItem.visibility = View.VISIBLE
            } else {
                binding.progressBarItem.visibility = View.GONE
            }
            if (networkState != null && networkState == NetworkState.ERROR) {
                binding.errorMsgItem.visibility = View.VISIBLE
                binding.errorMsgItem.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                binding.errorMsgItem.visibility = View.VISIBLE
                binding.errorMsgItem.text = networkState.msg
            } else {
                binding.errorMsgItem.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState: NetworkState? = this.networkState
        val hadExtraRow: Boolean = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow: Boolean = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    fun updateFavoriteIcon(movieId: Int, drawableResId: Int) {
        val position = currentList?.indexOfFirst { it.id == movieId }
        position?.let {
            notifyItemChanged(it)
        }
    }
}