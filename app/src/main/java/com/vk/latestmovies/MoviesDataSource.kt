package com.vk.latestmovies

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.vk.latestmovies.service.Movie
import com.vk.latestmovies.service.Response
import com.vk.latestmovies.service.TMDBApi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

sealed class ApiState {

    object Loading : ApiState()

    class Error(val error: Throwable) : ApiState()

    object Success : ApiState()
}

private const val TAG = "MoviesDataSource"

class MoviesDataSource(val service: TMDBApi, val compositeDisposable: CompositeDisposable) :
    PageKeyedDataSource<Int, Movie>() {

    val networkStatusLiveData: MutableLiveData<ApiState> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        networkStatusLiveData.postValue(ApiState.Loading)
        compositeDisposable.add(
            service.getTopRatedMovies(page = 1)
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<Response>() {
                    override fun onComplete() {

                    }

                    override fun onNext(response: Response) {
                        callback.onResult(response.movies!!, 0, response.totalResults ?: 0, null, 2)
                        networkStatusLiveData.postValue(ApiState.Success)
                    }

                    override fun onError(e: Throwable) {
                        networkStatusLiveData.postValue(ApiState.Error(e))
                    }

                })
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkStatusLiveData.postValue(ApiState.Loading)

        compositeDisposable.add(
            service.getTopRatedMovies(params.key)
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<Response>() {
                    override fun onComplete() {

                    }

                    override fun onNext(response: Response) {
                        //after 2 pages we are making it throw and exception to show how error is handled
                        if (params.key > 5) {
                            throw Exception("Something Terrible with Api")
                        } else {
                            callback.onResult(response.movies!!, (response.page)?.plus(1))
                            networkStatusLiveData.postValue(ApiState.Success)
                        }
                    }

                    override fun onError(e: Throwable) {
                        networkStatusLiveData.postValue(ApiState.Error(e))
                    }

                })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

    }

}

class MovieDatasourceFactory(val service: TMDBApi, val compositeDisposable: CompositeDisposable) :
    DataSource.Factory<Int, Movie>() {

    private val movieDataSource = MoviesDataSource(service, compositeDisposable)

    override fun create(): DataSource<Int, Movie> {
        return movieDataSource
    }

    fun getNetworkStatusLiveData(): MutableLiveData<ApiState> = movieDataSource.networkStatusLiveData
}
