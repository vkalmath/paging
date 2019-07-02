package com.vk.latestmovies

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.latestmovies.epoxy.MovieListEpoxyController
import com.vk.latestmovies.service.Movie
import com.vk.latestmovies.service.Response
import com.vk.latestmovies.service.tmdbService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "LatestMoviesActivity"

class LatestMoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val viewModel = ViewModelProviders.of(this).get(MovieListActivityViewModel::class.java)

        movies_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val pagedListController = MovieListEpoxyController()
        movies_rv.adapter = pagedListController.adapter

        viewModel.fetchPages({ list ->
            pagedListController.submitList(list)
        }, { error ->
            Log.e(TAG, "error: ${error}")
        })


        viewModel.getNetworkState().observe(this, Observer<ApiState> { apiState ->
            apiState?.let {
                when (it) {
                    ApiState.Loading -> {
                        //removes error view from List
                        pagedListController.error = null
                        pagedListController.isLoading = true
                    }

                    ApiState.Success -> {
                        //removes Error view
                        pagedListController.error = null
                        pagedListController.isLoading = false
                    }

                    is ApiState.Error -> {
                        pagedListController.isLoading = false
                        //sets Error view from list
                        pagedListController.error = it.error.localizedMessage
                    }

                }
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
