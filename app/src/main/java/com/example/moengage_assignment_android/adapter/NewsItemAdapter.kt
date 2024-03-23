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

class NewsItemAdapter(private val context: Context, private val articles: List<NewsArticleApiResponse.Article>) : RecyclerView.Adapter<NewsItemAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtAuthor: TextView= itemView.findViewById(R.id.txtAuthor)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtPublishedAt: TextView = itemView.findViewById(R.id.txtPublishedAt)
        private val imgNews: ImageView = itemView.findViewById(R.id.imgNews)

        fun bind(article: NewsArticleApiResponse.Article) {
            txtAuthor.text = article.author ?: "Unknown"
            txtTitle.text = article.title ?: ""
            txtPublishedAt.text = article.publishedAt ?: ""

            // Load image using Glide library
            Glide.with(context)
                .load(article.urlToImage)
                .into(imgNews)
        }
    }
}