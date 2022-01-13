package com.dicoding.githubuser.ui.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.repository.GithubUserRepository

class GithubUserAddDeleteViewModel (application: Application) : ViewModel() {
    private val mNoteRepository: GithubUserRepository = GithubUserRepository(application)
    fun insert(githubUser: GithubUser) {
        mNoteRepository.insert(githubUser)
    }

    fun delete(githubUser: GithubUser) {
        mNoteRepository.delete(githubUser)
    }

    fun getSearchGithubUser(username: String){
        mNoteRepository.getSearchGithubUser(username)
    }
}