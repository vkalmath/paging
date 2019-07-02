package com.vk.latestmovies

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.vk.latestmovies.service.Movie
import com.vk.latestmovies.service.tmdbService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver

private const val TAG = "MovieListViewModel"

class MovieListActivityViewModel(
    app: Application
) : AndroidViewModel(app) {

    private var rxPageList: Observable<PagedList<Movie>>

    private val compositeDisposable = CompositeDisposable()

    private val config = PagedList.Config.Builder()
        .setPageSize(5)
        .setInitialLoadSizeHint(2 * 15)
        .setEnablePlaceholders(true)
        .build()

    private val datasourceFactory = MovieDatasourceFactory(tmdbService, compositeDisposable)

    init {
        rxPageList = RxPagedListBuilder(datasourceFactory, config)
            .buildObservable()
    }

    fun getNetworkState() = datasourceFactory.getNetworkStatusLiveData()

    fun fetchPages(
        onPageReady: (PagedList<Movie>) -> Unit,
        onPageLoadFailed: (Throwable) -> Unit
    ) {
        compositeDisposable.add(
            rxPageList
                .subscribeWith(object : DisposableObserver<PagedList<Movie>>() {
                    override fun onComplete() {

                    }

                    override fun onNext(pagedMovieList: PagedList<Movie>) {
                        onPageReady(pagedMovieList)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "error: ${e}")
                        onPageLoadFailed(e)
                    }

                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}
