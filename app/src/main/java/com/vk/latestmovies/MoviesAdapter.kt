package com.vk.latestmovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import com.vk.latestmovies.service.Movie


val DIIF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}

class MoviesAdapter(val relay: PublishRelay<ApiState>) : PagedListAdapter<Movie, MoviesAdapter.ViewHolder>(DIIF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie: Movie? = getItem(position)
        holder.bind(movie)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView? = view.findViewById(R.id.movie_title)
        private val description: TextView? = view.findViewById(R.id.movie_description)
        private val imageView: ImageView? = view.findViewById(R.id.movie_thumbnail)
        private val movieContainer: ConstraintLayout? = view.findViewById(R.id.movie_container)

        fun bind(movie: Movie?) {
            movie?.let {
                titleView?.text = movie?.title
                description?.text = movie?.overview
                //todo: movie thumbnail
                //http://image.tmdb.org/t/p/w185
                Picasso.get().load("http://image.tmdb.org/t/p/w185/${movie?.posterPath}")
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.primary_dark_material_light)
                    .into(imageView)
            } ?: run {
            }

        }

    }
}
