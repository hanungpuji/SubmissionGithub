package com.dicoding.githubuser.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GithubUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(githubUser: GithubUser)

    @Delete
    fun delete(githubUser: GithubUser)

    @Query("SELECT * from githubUser ORDER BY username ASC")
    fun getAllGithubUser(): LiveData<List<GithubUser>>

    @Query("SELECT * from githubUser WHERE username = :username ORDER BY username ASC")
    fun getSearchGithubUser(username: String): LiveData<List<GithubUser>>
}