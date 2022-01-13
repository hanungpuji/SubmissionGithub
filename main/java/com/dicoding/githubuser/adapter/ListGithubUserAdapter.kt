package com.dicoding.githubuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.database.GithubUser
import com.dicoding.githubuser.databinding.ItemGithubUserBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ListGithubUserAdapter (private val listUser: ArrayList<GithubUser> ) :  RecyclerView.Adapter<ListGithubUserAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemGithubUserBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(data: GithubUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemGithubUserBinding.inflate(LayoutInflater.from(parent.context),parent, false)
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
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUser[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listUser.size
}