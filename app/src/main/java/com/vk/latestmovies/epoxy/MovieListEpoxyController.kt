package com.vk.latestmovies.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.vk.latestmovies.service.Movie


/**
 * EpoxyController which works with PagedLists
 */
class MovieListEpoxyController : PagedListEpoxyController<Movie>() {

    private var isError: Boolean = false

    var error: String? = ""
        set(value) {
            field = value?.let {
                isError = true
                it
            } ?: run {
                isError = false
                null
            }
            if (isError) {
                requestModelBuild()
            }
        }

    var isLoading = false
        set(value) {
            field = value
            if (field) {
                requestModelBuild()
            }
        }


    /**
     * Create the EpoxyViewModels
     */
    override fun buildItemModel(currentPosition: Int, item: Movie?): EpoxyModel<*> {
        item?.let {
            //Movie Item View Model
            return MovieItemModel_()
                .id("movie${currentPosition}")
                .title(item.title ?: "Unknown")
                .description(item.overview ?: "Uknown")
                .thumbnailUrl("http://image.tmdb.org/t/p/w185/${item.posterPath}")

        } ?: run {
            //Loading View Model
            return LoadingEpoxyModel_()
                .id("loading")
        }
    }

    /**
     * Adding models
     */
    override fun addModels(models: List<EpoxyModel<*>>) {
        if (isError) {
            super.addModels(
                models.plus(
                    //Error View Model
                    ErrorEpoxyModel_()
                        .id("Error")
                        .errorStr(error)
                ).filter { !(it is LoadingEpoxyModel_) }
            )
        } else if (isLoading) {
            super.addModels(
                models.plus(
                    //Error View Model
                    LoadingEpoxyModel_()
                        .id("loading")
                ).distinct()
            )
        } else {
            super.addModels(models.distinct())
        }
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {

    }
}
