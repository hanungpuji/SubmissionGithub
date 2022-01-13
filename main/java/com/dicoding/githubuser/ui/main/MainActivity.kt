package com.dicoding.githubuser.ui.main

import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.ListGithubUserAdapter
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.helper.SettingPreferences
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.model.MainViewModel
import com.dicoding.githubuser.model.SearchViewModel
import com.dicoding.githubuser.model.SettingViewModel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvGithubUser: RecyclerView
    private val list = ArrayList<GithubUser>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rvGithubUser = binding.rvGithubuser
        rvGithubUser.setHasFixedSize(true)
        supportActionBar?.title = "Github User's"

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = obtainSettingViewModel(this@MainActivity,pref)
        settingViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            })


        val mainViewModel = obtainViewModel(this@MainActivity,pref)
        mainViewModel.isLoading.observe(this,{
            showLoading(it)
        })

        mainViewModel.githubUser.observe(this, {
            if (it.size != 0){
                list.clear()
                list.addAll(it)
                showRecycleList()
            }else{
                Toast.makeText(this@MainActivity, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecycleList(){
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvGithubUser.layoutManager = GridLayoutManager(this, 2)
        } else {
            rvGithubUser.layoutManager = LinearLayoutManager(this)
        }
        val listGithubUserAdapter = getAdapter()

        listGithubUserAdapter.setOnItemClickCallback(object : ListGithubUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GithubUser) {
                moveDetailActivity(data)
            }
        })
    }

    private fun moveDetailActivity(data: GithubUser) {
        val moveWithDataIntent = Intent(this@MainActivity, MoveDetailUserActivity::class.java)
        moveWithDataIntent.putExtra(MoveDetailUserActivity.DETAIL_DATA, data)
        startActivity(moveWithDataIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                supportActionBar?.title = "Search Result : $query"
                val pref = SettingPreferences.getInstance(dataStore)
                val searchViewModel = obtainSearchViewModel(this@MainActivity,pref)
                searchViewModel.isLoading.observe(this@MainActivity,{
                    showLoading(it)
                })

                searchViewModel.searchUser.observe(this@MainActivity, {
                    if (it.size != 0){
                        list.clear()
                        list.addAll(it)
                        showRecycleList()
                    }else{
                        Toast.makeText(this@MainActivity, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show()
                    }
                })
                list.clear()
                searchViewModel.searchGithubUser("https://api.github.com/search/users?q=$query")
                searchView.clearFocus()
                return true

            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                val moveActivity = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(moveActivity)
                this.finish()
                true
            }
            R.id.favorite -> {
                val moveActivity = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(moveActivity)
                true
            }
            R.id.setting -> {
                val moveActivity = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(moveActivity)
                true
            }
            else -> true
        }
    }

    private fun getAdapter(): ListGithubUserAdapter {
        val listGithubUserAdapter = ListGithubUserAdapter(list)
        rvGithubUser.adapter = listGithubUserAdapter
        return listGithubUserAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity, pref: SettingPreferences): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun obtainSearchViewModel(activity: AppCompatActivity, pref: SettingPreferences): SearchViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[SearchViewModel::class.java]
    }

    private fun obtainSettingViewModel(activity: AppCompatActivity,pref: SettingPreferences): SettingViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[SettingViewModel::class.java]
    }
}