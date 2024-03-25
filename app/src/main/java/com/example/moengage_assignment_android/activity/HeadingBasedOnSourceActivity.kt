package com.example.moengage_assignment_android.activity

import android.os.Bundle
import android.util.Log
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
        rvNewsTitle.adapter = NewsItemAdapter(articlesList)



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
}