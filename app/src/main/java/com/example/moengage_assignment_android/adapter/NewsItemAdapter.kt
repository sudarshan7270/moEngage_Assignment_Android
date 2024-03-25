package com.example.moengage_assignment_android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengage_assignment_android.NewsArticleApiResponse
import com.example.moengage_assignment_android.R

class NewsItemAdapter(private val articles: List<NewsArticleApiResponse.Article>) : RecyclerView.Adapter<NewsItemAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]

        // Set data to views
        holder.itemView.findViewById<TextView>(R.id.txtTitle).text = article.title
        holder.itemView.findViewById<TextView>(R.id.txtPublishedAt).text = article.publishedAt
        holder.itemView.findViewById<TextView>(R.id.txtAuthor).text = article.author

        // Example: Load image using Glide library
         Glide.with(holder.itemView.context)
             .load(article.urlToImage)
             .error(R.drawable.icon_failed_hedingimg)
             .into(holder.itemView.findViewById(R.id.imgNews))

        // You can load images using any image loading library like Glide, Picasso, etc.
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}