package com.example.moengage_assignment_android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moengage_assignment_android.fragment.NewsTitleFragment
import com.example.moengage_assignment_android.ui.theme.MoEngage_Assignment_AndroidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengage_assignment_android.activity.HeadingBasedOnSourceActivity
import com.example.moengage_assignment_android.adapter.NewsItemAdapter
import com.example.moengage_assignment_android.adapter.NewsSourcesAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var rvSourcesList:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvSourcesList=findViewById<RecyclerView>(R.id.rvSources)
        rvSourcesList.layoutManager=LinearLayoutManager(this)
//        rvNewsTitle.layoutManager = LinearLayoutManager(this)

        // Fetch news data
        FetchNewsTask(this@MainActivity).execute("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")
    }
    inner class FetchNewsTask(private val context: Context) : AsyncTask<String, Void, Pair<List<NewsArticleApiResponse.Article>, List<String>>>() {

        private val allArticles = mutableListOf<NewsArticleApiResponse.Article>()
        private val allSources = mutableListOf<String>()

        override fun doInBackground(vararg urls: String?): Pair<List<NewsArticleApiResponse.Article>, List<String>> {
            val url = urls[0]
            val articlesList = ArrayList<NewsArticleApiResponse.Article>()
            val uniqueSources = HashSet<String>()

            try {
                val url = URL(url)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"

                val inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }

                val response = stringBuilder.toString()
                Log.d("api_res", response.toString())

                // Parse JSON response
                val jsonResponse = JSONObject(response)
                val articlesJsonArray = jsonResponse.optJSONArray("articles")
                Log.d("api_res", articlesJsonArray.toString())

                for (i in 0 until articlesJsonArray.length()) {
                    val articleJsonObject = articlesJsonArray.optJSONObject(i)
                    val author = articleJsonObject.optString("author")
                    val authorName = if (author.isNullOrBlank()) "Unknown" else author

                    val sourceJsonObject = articleJsonObject.optJSONObject("source")
                    val source = NewsArticleApiResponse.Source(
                        id = sourceJsonObject?.optString("id"),
                        name = sourceJsonObject?.optString("name")
                    )

                    // Check if the source name is already logged
                    if (!uniqueSources.contains(source?.name)) {
                        Log.d("Unique Source Name", source?.name ?: "Unknown")
                        uniqueSources.add(source?.name ?: "")
                    }


                    val article = NewsArticleApiResponse.Article(
                        source = source,
                        author = authorName,
                        title = articleJsonObject.optString("title", "N/A"),
                        description = articleJsonObject.optString("description", "N/A"),
                        url = articleJsonObject.optString("url", ""),
                        urlToImage = articleJsonObject.optString("urlToImage", ""),
                        publishedAt = articleJsonObject.optString("publishedAt", ""),
                        content = articleJsonObject.optString("content", "")

                    )
                    articlesList.add(article)
                }

                inputStream.close()
                urlConnection.disconnect()
            } catch (e: Exception) {
                Log.e("FetchNewsTask", "Error fetching data: ${e.message}")
            }

            allArticles.addAll(articlesList)
            allSources.addAll(uniqueSources)
            return Pair(articlesList, uniqueSources.toList())
        }

        override fun onPostExecute(result: Pair<List<NewsArticleApiResponse.Article>, List<String>>?) {
            super.onPostExecute(result)
            result?.let { (articles, sources) ->
                // Set adapter to RecyclerView for sources
                Log.d("All Articles", articles.toString())
                Log.d("All Sources", sources.toString())
                val sourcesAdapter = NewsSourcesAdapter(context, sources) { sourceName ->
                    // When a source item is clicked, filter articles based on the clicked source name
                    val filteredArticles = allArticles.filter { it.source?.name == sourceName }
                    Log.d("Filtered Articles Size", filteredArticles.size.toString())

                    Log.d("sourcsClick", "Source Name: $sourceName")
                    filteredArticles.forEachIndexed { index, article ->
                        Log.d("sourcsClickArticle $index", article.toString())
                    }
                    val articlesJson = articlesToJsonString(filteredArticles)
                    Log.d("filterJson",articlesJson.toString())
                    val intent = Intent(context, HeadingBasedOnSourceActivity::class.java).apply {
                        putExtra("articlesJson", articlesJson)
                    }
                    context.startActivity(intent)

                }
                rvSourcesList.adapter = sourcesAdapter
            }
        }

    }

        private fun parseJsonResponse(response: String) {
        try {
            val jsonResponse = JSONObject(response)
            val status = jsonResponse.optString("status")
            val articlesJsonArray = jsonResponse.optJSONArray("articles")

            val articlesList = ArrayList<NewsArticleApiResponse.Article>()
            for (i in 0 until articlesJsonArray.length()) {
                val articleJsonObject = articlesJsonArray.optJSONObject(i)
                val sourceJsonObject = articleJsonObject.optJSONObject("source")
                val source = NewsArticleApiResponse.Source(
                    id = sourceJsonObject?.optString("id"),
                    name = sourceJsonObject?.optString("name")
                )
                val article = NewsArticleApiResponse.Article(
                    source = source,
                    author = articleJsonObject.optString("author"),
                    title = articleJsonObject.optString("title"),
                    description = articleJsonObject.optString("description"),
                    url = articleJsonObject.optString("url"),
                    urlToImage = articleJsonObject.optString("urlToImage"),
                    publishedAt = articleJsonObject.optString("publishedAt"),
                    content = articleJsonObject.optString("content")
                )
                articlesList.add(article)
            }


            // Now you can use the 'newsArticleApiResponse' object as needed
//            Log.d("NewsApiResponse", "Parsed Response: $newsArticleApiResponse")
        } catch (e: Exception) {
            Log.e("parseJsonResponse", "Error parsing JSON: ${e.message}")
        }
    }

    private fun articlesToJsonString(articles: List<NewsArticleApiResponse.Article>): String {
        val jsonArray = JSONArray()
        articles.forEach { article ->
            val jsonObject = JSONObject()
            jsonObject.put("author", article.author)
            jsonObject.put("title", article.title)
            jsonObject.put("description", article.description)
            jsonObject.put("url", article.url)
            jsonObject.put("urlToImage", article.urlToImage)
            jsonObject.put("publishedAt", article.publishedAt)
            jsonObject.put("content", article.content)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
}




