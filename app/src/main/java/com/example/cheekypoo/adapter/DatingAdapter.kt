package com.example.cheekypoo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheekypoo.databinding.ItemUserLayoutBinding
import com.example.cheekypoo.model.UserModel

class DatingAdapter(
    private val context: Context,
    private val userList: ArrayList<UserModel>
) : RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {

    inner class DatingViewHolder(val binding: ItemUserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        val binding = ItemUserLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return DatingViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.textView2.text = user.Name
        holder.binding.textView.text = user.Email

        Glide.with(context)
            .load(user.Image)
            .into(holder.binding.userimage)
    }
}
