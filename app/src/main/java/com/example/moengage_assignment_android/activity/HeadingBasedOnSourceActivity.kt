package com.example.moengage_assignment_android.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengage_assignment_android.NewsArticleApiResponse
import com.example.moengage_assignment_android.R
import com.example.moengage_assignment_android.adapter.NewsItemAdapter
import org.json.JSONArray
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HeadingBasedOnSourceActivity : AppCompatActivity() {

    private lateinit var rvNewsTitle: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_heading_based_on_source)
        rvNewsTitle=findViewById<RecyclerView>(R.id.rvNewsTitle)
        val articlesJson = intent.getStringExtra("articlesJson")
        Log.d("inHeadBased",articlesJson.toString())
        val articlesList = parseArticlesFromJson(articlesJson)

        // Setup RecyclerView
        rvNewsTitle.layoutManager = LinearLayoutManager(this)
//        rvNewsTitle.adapter = NewsItemAdapter(articlesList)

        val latestArticles = articlesList.sortedByDescending { parseDateTime(it.publishedAt) }
        showArticles(latestArticles)
        val tvLatest=findViewById<TextView>(R.id.tvLatest)
        val tvEarliest=findViewById<TextView>(R.id.tvEarliest)

        tvLatest.setOnClickListener {
            val latestArticles = articlesList.sortedByDescending { parseDateTime(it.publishedAt) }
            showArticles(latestArticles)
            tvLatest.setBackgroundResource(R.drawable.rounded_background)
            tvEarliest.setBackgroundResource(R.drawable.plaiin_backgroubd)

        }

        tvEarliest.setOnClickListener {
            val earliestArticles = articlesList.sortedBy { parseDateTime(it.publishedAt) }
            showArticles(earliestArticles)
            tvEarliest.setBackgroundResource(R.drawable.rounded_background)
            tvLatest.setBackgroundResource(R.drawable.plaiin_backgroubd)


        }



    }
    private fun parseArticlesFromJson(jsonString: String?): List<NewsArticleApiResponse.Article> {
        val articlesList = mutableListOf<NewsArticleApiResponse.Article>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val articleJson = jsonArray.getJSONObject(i)
                val sourceJson = articleJson.optJSONObject("source")
                val source = NewsArticleApiResponse.Source(
                    id = sourceJson?.optString("id"),
                    name = sourceJson?.optString("name")
                )
                val article = NewsArticleApiResponse.Article(
                    source = source,
                    author = articleJson.optString("author"),
                    title = articleJson.optString("title"),
                    description = articleJson.optString("description"),
                    url = articleJson.optString("url"),
                    urlToImage = articleJson.optString("urlToImage"),
                    publishedAt = articleJson.optString("publishedAt"),
                    content = articleJson.optString("content")
                )
                articlesList.add(article)
            }
        } catch (e: Exception) {
            Log.e("ParsingError", "Error parsing JSON: ${e.message}")
        }
        return articlesList
    }
    private fun parseDateTime(dateTimeString: String?): Date {
        try {
            if (dateTimeString.isNullOrEmpty()) {
                // Return a default date if the input string is null or empty
                return Date(0)
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            return formatter.parse(dateTimeString) ?: Date(0)
        } catch (e: ParseException) {
            // Handle parsing exception here
            Log.e("DateTimeParsing", "Error parsing date: ${e.message}")
            return Date(0) // Return a default date in case of parsing error
        }
    }



    private fun showArticles(articlesList: List<NewsArticleApiResponse.Article>) {
        rvNewsTitle.adapter = NewsItemAdapter(articlesList)
    }
}