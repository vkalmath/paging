package com.vk.latestmovies

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.latestmovies.service.Movie
import com.vk.latestmovies.service.Response
import com.vk.latestmovies.service.tmdbService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            tmdbService.getTopRatedMovies(page = 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<Response>() {
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response) {
                        Log.e(TAG, "response: ${t}")
                    }

                    override fun onError(e: Throwable) {

                    }

                })

        }

        val config = PagedList.Config.Builder()
            .setPageSize(5)
//            .setInitialLoadSizeHint(2 * 15)
            .setEnablePlaceholders(true)
            .build()

        val datasourceFactory = MovieDatasourceFactory(tmdbService)
        val rxPageList =  RxPagedListBuilder(datasourceFactory, config)
            .buildObservable()

        movies_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter =  MoviesAdapter(datasourceFactory.getRelay())
        movies_rv.adapter = adapter




        rxPageList
            .subscribeWith(object: DisposableObserver<PagedList<Movie>>() {
                override fun onComplete() {

                }

                override fun onNext(pagedMovieList: PagedList<Movie>) {
                    adapter.submitList(pagedMovieList)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "error: ${e}")
                }

            })

        datasourceFactory.getRelay().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<ApiState>() {
                override fun onComplete() {

                }

                override fun onNext(state: ApiState) {
                    when(state) {
                        ApiState.Loading -> {
                            loading_progress.visibility = View.VISIBLE
                            //error_tv.visibility = View.GONE
                        }

                        ApiState.Success -> {
                            movies_rv.visibility = View.VISIBLE
                            loading_progress.visibility = View.GONE
                            //error_tv.visibility = View.GONE
                        }

                        is ApiState.Error -> {
                            //movies_rv.visibility = View.GONE
                            loading_progress.visibility = View.GONE
                            error_tv.visibility = View.VISIBLE
                            error_tv.text = state.error.localizedMessage
                        }
                    }
                }

                override fun onError(e: Throwable) {

                }

            })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
