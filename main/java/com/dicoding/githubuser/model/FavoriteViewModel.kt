package com.dicoding.githubuser.model

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.DetailResponse
import com.dicoding.githubuser.api.ApiConfig
import com.dicoding.githubuser.database.GithubUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteViewModel : ViewModel() {
    private val list = ArrayList<GithubUser>()

    private val _githubUser = MutableLiveData<ArrayList<GithubUser>>()
    val githubUser: LiveData<ArrayList<GithubUser>> = _githubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val token = "token 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"
    }

    fun getGithubUserDetail(dataUser: List<GithubUser>) {
        val size = dataUser.size
        for (i in 0 until size) {
            _isLoading.value = true
            val client =
                ApiConfig.getApiService().getDetailUser(dataUser[i].username, token)
            client.enqueue(object : Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {

                            val userGithub = GithubUser(
                                responseBody.name,
                                responseBody.login,
                                responseBody.followers,
                                responseBody.following,
                                responseBody.avatarUrl,
                                responseBody.location,
                                responseBody.publicRepos,
                                responseBody.company
                            )
//                            list.clear()
                            list.add(userGithub)
                            _githubUser.value = list
                        }
                    } else {
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
        }
    }
}