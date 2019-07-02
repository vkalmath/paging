package com.vk.latestmovies.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.*
import com.squareup.picasso.Picasso
import com.vk.latestmovies.R

@EpoxyModelClass(layout = R.layout.movie_item_layout)
abstract class MovieItemModel : EpoxyModelWithHolder<MovieItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var title: String
    @EpoxyAttribute
    lateinit var description: String
    @EpoxyAttribute
    lateinit var thumbnailUrl: String

    override fun bind(holder: Holder) {
        if (title.equals("loading")) {
            holder.titleView?.text = title
            holder.descriptionView?.visibility = View.GONE
            holder.thumbnailImageView?.visibility = View.GONE
        } else {
            holder.descriptionView?.visibility = View.VISIBLE
            holder.thumbnailImageView?.visibility = View.VISIBLE

            holder.titleView?.text = title
            holder.descriptionView?.text = description
            Picasso.get().load(thumbnailUrl)
                .fit()
                .centerCrop()
                .placeholder(R.color.primary_dark_material_light)
                .into(holder.thumbnailImageView)
        }
    }

    class Holder : EpoxyHolder() {
        var titleView: TextView? = null
        var descriptionView: TextView? = null
        var thumbnailImageView: ImageView? = null

        override fun bindView(itemView: View) {
            titleView = itemView.findViewById(R.id.movie_title)
            descriptionView = itemView.findViewById(R.id.movie_description)
            thumbnailImageView = itemView.findViewById(R.id.movie_thumbnail)
        }

    }
}
