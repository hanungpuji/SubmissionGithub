package com.dicoding.githubuser.model

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.DetailResponse
import com.dicoding.githubuser.database.GithubUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.dicoding.githubuser.api.ApiConfig


class MainViewModel(application: Application) : ViewModel() {
    private val list = ArrayList<GithubUser>()

    private val _githubUser = MutableLiveData<ArrayList<GithubUser>>()
    val githubUser: LiveData<ArrayList<GithubUser>> = _githubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    companion object{
        private const val token = "token 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"
    }

    init {
        getGithubUser()
    }

    private fun getGithubUser() {
        _isLoading.value= true
        val client = AsyncHttpClient()
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val resultArray = JSONArray(result)
                    for (i in 0 until resultArray.length()) {
                        val username = resultArray.getJSONObject(i).getString("login")
                        getGithubUserDetail(username)
                    }

                    _isLoading.value = false
                } catch (e: Exception) {
                    Log.e(TAG, "onFailure: ${e.message}")
                    e.printStackTrace()
                    _isLoading.value = false
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                    Log.e(TAG, "onFailure: $errorMessage")
//                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getGithubUserDetail(username: String) {
        _isLoading.value= true
        val client = ApiConfig.getApiService().getDetailUser(username,token)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value= false
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
//                        list.clear()
                        list.add(userGithub)
                        _githubUser.value = list
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value= false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}

