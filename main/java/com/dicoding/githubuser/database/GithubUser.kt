package com.dicoding.githubuser.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class GithubUser(
    @ColumnInfo(name = "name")
    var name: String? = null,
    @PrimaryKey()
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "jmlfollower")
    var jmlfollower: Int? = null,
    @ColumnInfo(name = "jmlfollowing")
    var jmlfollowing: Int? = null,
    @ColumnInfo(name = "photo")
    var photo: String? = null,
    @ColumnInfo(name = "location")
    var location: String? = null,
    @ColumnInfo(name = "repository")
    var repository: Int? = null,
    @ColumnInfo(name = "company")
    var company: String? = null
) : Parcelable