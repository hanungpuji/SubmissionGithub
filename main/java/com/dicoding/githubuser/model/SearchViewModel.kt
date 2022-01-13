package com.dicoding.githubuser.model

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.DetailResponse
import com.dicoding.githubuser.api.ApiConfig
import com.dicoding.githubuser.database.GithubUser
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    private val listSearch = ArrayList<GithubUser>()

    private val _searchUser = MutableLiveData<ArrayList<GithubUser>>()
    val searchUser: LiveData<ArrayList<GithubUser>> = _searchUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val token = "token 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"
    }

    fun searchGithubUser(link: String) {
        _isLoading.value = true
        listSearch.clear()
        val client = AsyncHttpClient()
        client.addHeader("Authorization", token)
        client.addHeader("User-Agent", "request")
        val url = link
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray = jsonObject.getJSONArray("items")
                    val lengthData:Int = jsonArray.length()
                    for (i in 0 until lengthData) {
                        val usernameSearch: String = jsonArray.getJSONObject(i).getString("login")
                        getGithubUserDetail(usernameSearch)
                    }
                    _isLoading.value = false
                } catch (e: Exception) {
//                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.e(ContentValues.TAG, "onFailure: ${e.message}}")
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.e(ContentValues.TAG, "onFailure: $errorMessage}")
//                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getGithubUserDetail(username: String) {
        _isLoading.value= true
        val client = ApiConfig.getApiService().getDetailUser(username, token)
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
                        Log.e(ContentValues.TAG, "Size: ${listSearch.size}")
                        listSearch.add(userGithub)
                        _searchUser.value = listSearch
                    }
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value= false
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}