package com.dicoding.githubuser.ui.main

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.adapter.ListGithubUserAdapter
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.databinding.ActivityFavoriteBinding
import com.dicoding.githubuser.helper.SettingPreferences
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.model.FavoriteViewModel
import com.dicoding.githubuser.model.GetDataRoomViewModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var rvFavUser: RecyclerView
    private lateinit var getDataRoomViewModel: GetDataRoomViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private val list = ArrayList<GithubUser>()

    private val title: String = "Favorite User"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rvFavUser = binding.rvFavuser

        val pref = SettingPreferences.getInstance(dataStore)
        supportActionBar?.title = title

        getDataRoomViewModel = obtainGetViewModel(this@FavoriteActivity,pref)
        favoriteViewModel = obtainFavoriteViewModel(this@FavoriteActivity,pref)

        getDataRoomViewModel.getAllGithubUser().observe(this, { userList ->
            if (userList.isNotEmpty()) {
                favoriteViewModel.isLoading.observe(this,{
                    showLoading(it)
                })

                favoriteViewModel.githubUser.observe(this@FavoriteActivity,{
                    list.clear()
                    list.addAll(it)
//                    val nilai: Int = it.size
                    showRecycleList()
//                    Toast.makeText(this@FavoriteActivity, "Ada Data Favorite,Username ${list[nilai-1].username}", Toast.LENGTH_SHORT).show()
                })
                list.clear()
                favoriteViewModel.getGithubUserDetail(userList)

            }else{
                Toast.makeText(this@FavoriteActivity, "Tidak Ada Data Favorite", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun showRecycleList(){
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvFavUser.layoutManager = GridLayoutManager(this, 2)
        } else {
            rvFavUser.layoutManager = LinearLayoutManager(this)
        }

        val listGithubUserAdapter = getAdapter()

        listGithubUserAdapter.setOnItemClickCallback(object : ListGithubUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GithubUser) {
                moveDetailActivity(data)
            }
        })
    }

    private fun moveDetailActivity(data: GithubUser) {
        val moveWithDataIntent = Intent(this@FavoriteActivity, MoveDetailUserActivity::class.java)
        moveWithDataIntent.putExtra(MoveDetailUserActivity.DETAIL_DATA, data)
        startActivity(moveWithDataIntent)
    }

    private fun getAdapter(): ListGithubUserAdapter {
        val listGithubUserAdapter = ListGithubUserAdapter(list)
        rvFavUser.adapter = listGithubUserAdapter
        return listGithubUserAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun obtainGetViewModel(
        activity: FavoriteActivity,
        pref: SettingPreferences
    ): GetDataRoomViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[GetDataRoomViewModel::class.java]
    }

    private fun obtainFavoriteViewModel(
        activity: FavoriteActivity,
        pref: SettingPreferences
    ): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }
}