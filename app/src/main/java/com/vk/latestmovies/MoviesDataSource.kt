package com.vk.latestmovies

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.jakewharton.rxrelay2.PublishRelay
import com.vk.latestmovies.service.Movie
import com.vk.latestmovies.service.Response
import com.vk.latestmovies.service.TMDBApi
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

sealed class ApiState {

    object Loading : ApiState()

    class Error(val error: Throwable) : ApiState()

    object Success : ApiState()
}

private const val TAG = "MoviesDataSource"

class MoviesDataSource(val service: TMDBApi) : PageKeyedDataSource<Int, Movie>() {

    val publishRelay: PublishRelay<ApiState> = PublishRelay.create()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {

        publishRelay.accept(ApiState.Loading)

        service.getTopRatedMovies(page = 1)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onComplete() {

                }

                override fun onNext(response: Response) {
                    callback.onResult(response.movies!!, 0, response.totalResults ?: 0, null, 2)
                    publishRelay.accept(ApiState.Success)
                }

                override fun onError(e: Throwable) {
                    publishRelay.accept(ApiState.Error(e))
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        publishRelay.accept(ApiState.Loading)

        service.getTopRatedMovies(params.key)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<Response>() {
                override fun onComplete() {

                }

                override fun onNext(response: Response) {
                    if (params.key > 4) {
                        throw Exception("crashhed")
                    } else {
                        callback.onResult(response.movies!!, (response.page)?.plus(1))
                        publishRelay.accept(ApiState.Success)
                    }
                }

                override fun onError(e: Throwable) {
                    publishRelay.accept(ApiState.Error(e))
                }

            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

    }

}

class MovieDatasourceFactory(val service: TMDBApi) : DataSource.Factory<Int, Movie>() {

    private val movieDataSource = MoviesDataSource(service)

    override fun create(): DataSource<Int, Movie> {
        return movieDataSource
    }

    fun getRelay(): PublishRelay<ApiState> = movieDataSource.publishRelay
}
