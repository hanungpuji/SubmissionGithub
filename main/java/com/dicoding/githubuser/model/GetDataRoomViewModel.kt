package com.dicoding.githubuser.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.repository.GithubUserRepository

class GetDataRoomViewModel(application: Application) : ViewModel() {
    private val mGithubUserRepository: GithubUserRepository = GithubUserRepository(application)

    fun getSearchGithubUser(username: String): LiveData<List<GithubUser>> = mGithubUserRepository.getSearchGithubUser(username)

    fun getAllGithubUser(): LiveData<List<GithubUser>> = mGithubUserRepository.getAllGithubUser()
}

