package com.dicoding.githubuser.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.adapter.FollowingAdapter
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.ui.main.MoveDetailUserActivity
import com.dicoding.githubuser.R
import com.dicoding.githubuser.databinding.FragmentFollowingBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowingFragment : Fragment() {
    private val list = ArrayList<GithubUser>()
    private lateinit var rvFollowing: RecyclerView
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding as FragmentFollowingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater,container,false)
        val view = binding.root
        rvFollowing = view.findViewById(R.id.rv_following)
        rvFollowing.setHasFixedSize(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val dataDetail = activity?.intent?.getParcelableExtra<GithubUser>(MoveDetailUserActivity.DETAIL_DATA) as GithubUser
        dataDetail.username?.let { getGithubUser(it) }
    }

    private fun getGithubUser(uname: String) {
        showLoading(true)
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$uname/following"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val resultArray = JSONArray(result)
                    for (i in 0 until resultArray.length()) {
                        val username = resultArray.getJSONObject(i).getString("login")
                        getGithubUserDetail(username)
                    }
                    showLoading(false)
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    showLoading(false)
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        })
    }

    private fun getGithubUserDetail(username: String) {
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$username"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val responseObject = JSONObject(result)
                    val nameUser = responseObject.getString("name").replace("null","-")
                    val repository =responseObject.getInt("public_repos")
                    val followers =responseObject.getInt("followers")
                    val following =responseObject.getInt("following")
                    val location =responseObject.getString("location").replace("null","-")
                    val company =responseObject.getString("company").replace("null","-")
                    val photoUrl = responseObject.getString("avatar_url")

                    val userGithub = GithubUser(nameUser, username,followers,
                        following, photoUrl,location,repository,company)
                    list.add(userGithub)
                    showRecycleList()

                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    showLoading(false)
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        })
    }

    private fun showRecycleList(){
        rvFollowing.layoutManager = LinearLayoutManager(activity)

        val followingAdapter = FollowingAdapter(list)
        rvFollowing.adapter = followingAdapter

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarFollowing.visibility = View.VISIBLE
        } else {
            binding.progressBarFollowing.visibility = View.GONE
        }
    }
}