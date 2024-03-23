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
import com.example.moengage_assignment_android.adapter.NewsItemAdapter



class MainActivity : AppCompatActivity() {

    private lateinit var rvNewsTitle: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvNewsTitle=findViewById<RecyclerView>(R.id.rvNewsTitle)
        rvNewsTitle.layoutManager = LinearLayoutManager(this)

        // Fetch news data
        FetchNewsTask(this@MainActivity).execute("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")
    }
    inner class FetchNewsTask(private val context: Context) : AsyncTask<String, Void, List<NewsArticleApiResponse.Article>>() {
        override fun doInBackground(vararg urls: String?): List<NewsArticleApiResponse.Article> {
            val url = urls[0]
            val articlesList = ArrayList<NewsArticleApiResponse.Article>()

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
                Log.d("api_res",response.toString())

                // Parse JSON response
                val jsonResponse = JSONObject(response)
                val articlesJsonArray = jsonResponse.optJSONArray("articles")

                for (i in 0 until articlesJsonArray.length()) {
                    val articleJsonObject = articlesJsonArray.optJSONObject(i)
                    val article = NewsArticleApiResponse.Article(
                        title = articleJsonObject.optString("title", "N/A"),
                        description = articleJsonObject.optString("description", "N/A"),
                        url = articleJsonObject.optString("url", ""),
                        urlToImage = articleJsonObject.optString("urlToImage", ""),
                        publishedAt = articleJsonObject.optString("publishedAt", "")
                    )
                    articlesList.add(article)
                }

                inputStream.close()
                urlConnection.disconnect()
            } catch (e: Exception) {
                Log.e("FetchNewsTask", "Error fetching data: ${e.message}")
            }

            return articlesList
        }

        override fun onPostExecute(result: List<NewsArticleApiResponse.Article>?) {
            super.onPostExecute(result)
            result?.let {
                // Set adapter to RecyclerView
                val adapter = NewsItemAdapter(context, it)
                rvNewsTitle.adapter = adapter
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
}



