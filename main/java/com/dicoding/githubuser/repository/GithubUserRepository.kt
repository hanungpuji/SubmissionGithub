package com.dicoding.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.database.GithubUserDao
import com.dicoding.githubuser.database.GithubUserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GithubUserRepository(application: Application) {
    private val mGithubUserDao: GithubUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = GithubUserRoomDatabase.getDatabase(application)
        mGithubUserDao = db.githubUserDao()
    }

    fun getAllGithubUser(): LiveData<List<GithubUser>> = mGithubUserDao.getAllGithubUser()

    fun getSearchGithubUser(username: String): LiveData<List<GithubUser>> = mGithubUserDao.getSearchGithubUser(username)

    fun insert(githubUser: GithubUser) {
        executorService.execute { mGithubUserDao.insert(githubUser) }
    }
    fun delete(githubUser: GithubUser) {
        executorService.execute { mGithubUserDao.delete(githubUser) }
    }

}