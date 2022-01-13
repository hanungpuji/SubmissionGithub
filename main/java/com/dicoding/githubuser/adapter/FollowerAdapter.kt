package com.dicoding.githubuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.databinding.ItemGithubFollBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class FollowerAdapter(private val listUser: ArrayList<GithubUser>) :  RecyclerView.Adapter<FollowerAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemGithubFollBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemGithubFollBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, username, jmlfollower, jmlfollowing, photo) = listUser[position]
        val usernameConcanate = "@$username"
        Glide.with(holder.itemView.context)
            .load(photo)
            .circleCrop()
            .into(holder.binding.imgItemPhoto)
        holder.binding.tvItemName.text = name
        holder.binding.tvItemUsername.text = usernameConcanate
        holder.binding.tvItemJmlfollower.text = NumberFormat.getInstance(Locale.US).format(jmlfollower)
        holder.binding.tvItemJmlfollowing.text = NumberFormat.getInstance(Locale.US).format(jmlfollowing)

    }

    override fun getItemCount(): Int = listUser.size

}