package com.dicoding.githubuser.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.adapter.SectionsPagerAdapter
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.databinding.ActivityMoveDetailUserBinding
import com.dicoding.githubuser.helper.SettingPreferences
import com.dicoding.githubuser.helper.ViewModelFactory
import com.dicoding.githubuser.model.GetDataRoomViewModel
import com.dicoding.githubuser.ui.insert.GithubUserAddDeleteViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class MoveDetailUserActivity : AppCompatActivity(), View.OnClickListener {
    private var isfav: Boolean = false
    private lateinit var binding: ActivityMoveDetailUserBinding
    private lateinit var githubUserAddDeleteViewModel: GithubUserAddDeleteViewModel
    private lateinit var getDataRoomViewModel: GetDataRoomViewModel
    private var dataDetail: GithubUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = SettingPreferences.getInstance(dataStore)
        githubUserAddDeleteViewModel = obtainGithubUserViewModel(this@MoveDetailUserActivity,pref)

        binding = ActivityMoveDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail User"

        dataDetail = intent.getParcelableExtra<GithubUser>(DETAIL_DATA) as GithubUser
        val username: String? = dataDetail?.username
        getDataJSON(username)


        getDataRoomViewModel = obtainGetViewModel(this@MoveDetailUserActivity,pref)
        username?.let {
            getDataRoomViewModel.getSearchGithubUser(it).observe(this, { userList ->
                if (userList.isNotEmpty()) {
                    isfav = true
                    binding.fabAdd.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_24, null))
                }else{
                    isfav = false
                    binding.fabAdd.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_border_24, null))
                }
            })
        }


        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

        binding.fabAdd.setOnClickListener(this)
    }

    private fun getDataJSON(username: String?) {
        showLoading(true)
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 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")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$username"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val nameUser = responseObject.getString("name").replace("null","-")
                    val repository =responseObject.getInt("public_repos")
                    val followers =responseObject.getInt("followers")
                    val following =responseObject.getInt("following")
                    val location =responseObject.getString("location").replace("null","-")
                    val company =responseObject.getString("company").replace("null","-")
                    binding.detailname.text = nameUser
                    val usernameConcenate = "@$username"
                    val repositoryConcenate = NumberFormat.getInstance(Locale.US).format(repository)+" Repository"
                    val photoUrl = responseObject.getString("avatar_url")
                    binding.detailUsername.text = usernameConcenate
                    binding.detailfollower.text = NumberFormat.getInstance(Locale.US).format(followers)
                    binding.detailfollowing.text = NumberFormat.getInstance(Locale.US).format(following)
                    binding.detailAlamat.text = location
                    binding.detailCompany.text = company
                    binding.detailRepository.text = repositoryConcenate
                    Glide.with(binding.imgItemDetailPhoto)
                        .load(photoUrl)
                        .circleCrop()
                        .into(binding.imgItemDetailPhoto)
                    showLoading(false)
                } catch (e: Exception) {
                    Toast.makeText(this@MoveDetailUserActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MoveDetailUserActivity, errorMessage, Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )

        const val DETAIL_DATA = "detail_data"
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onClick(p0: View?) {
        isfav = if (!isfav){
            githubUserAddDeleteViewModel.insert(dataDetail as GithubUser)
            Toast.makeText(this@MoveDetailUserActivity, "Favorite Has Been Inserted", Toast.LENGTH_SHORT).show()
    //            noteAddUpdateViewModel.delete(note as Note)
    //            showToast(getString(R.string.deleted))
            binding.fabAdd.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_24, null))
            true
        }else{
            githubUserAddDeleteViewModel.delete(dataDetail as GithubUser)
            Toast.makeText(this@MoveDetailUserActivity, "Favorite Has Been Removed", Toast.LENGTH_SHORT).show()
            binding.fabAdd.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_favorite_border_24, null))
            false
        }

    }

    private fun obtainGithubUserViewModel(
        activity: MoveDetailUserActivity,
        pref: SettingPreferences
    ): GithubUserAddDeleteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[GithubUserAddDeleteViewModel::class.java]
    }

    private fun obtainGetViewModel(
        activity: MoveDetailUserActivity,
        pref: SettingPreferences
    ): GetDataRoomViewModel {
        val factory = ViewModelFactory.getInstance(activity.application,pref)
        return ViewModelProvider(activity, factory)[GetDataRoomViewModel::class.java]
    }


}