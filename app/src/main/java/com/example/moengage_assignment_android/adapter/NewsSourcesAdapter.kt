package com.example.moengage_assignment_android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moengage_assignment_android.NewsArticleApiResponse
import com.example.moengage_assignment_android.R

class NewsSourcesAdapter(private val context: Context, private val sources: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<NewsSourcesAdapter.SourceViewHolder>() {

    inner class SourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sourceNameTextView1: TextView = itemView.findViewById(R.id.txtSourceName1)
        val sourceNameTextView2: TextView = itemView.findViewById(R.id.txtSourceName2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source, parent, false)
        return SourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val index = position * 2 // Calculate index for the current item

        // Get sources for the current and next item
        val source1 = if (index < sources.size) sources[index] else null
        val source2 = if (index + 1 < sources.size) sources[index + 1] else null

        // Bind data to TextViews
        holder.sourceNameTextView1.text = source1 ?: "Unknown" // Show "Unknown" if name is null
        holder.sourceNameTextView2.text = source2 ?: "Unknown" // Show "Unknown" if name is null

        // Set click listeners
        holder.sourceNameTextView1.setOnClickListener {
            source1?.let { onItemClick(it) }
        }
        holder.sourceNameTextView2.setOnClickListener {
            source2?.let { onItemClick(it) }
        }
    }

    override fun getItemCount(): Int {
        // Calculate the number of items based on the number of sources
        return (sources.size + 1) / 2
    }
}