package com.example.moengage_assignment_android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoEngage_Assignment_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsScreen()
                }
            }
        }
    }
}

@Composable
fun NewsScreen() {
    var newsList by remember { mutableStateOf<List<News>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchData { news ->
            newsList = news
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "News Articles",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        NewsList(newsList)
    }
}

@Composable
fun NewsList(newsList: List<News>) {
    Column {
        newsList.forEach { news ->
            NewsItem(news)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsItem(news: News) {
    val context = LocalContext.current
    Text(
        text = news.title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.clickable {
            openNewsUrl(context, news.url)
        }
    )
}

data class News(
    val title: String,
    val url: String
)

private fun openNewsUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.`package` = "com.android.chrome" // Package name of Chrome browser

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // If Chrome browser is not installed, open URL in default browser
        intent.`package` = null
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Log.d("NewsItem", "No activity found to handle URL: $url")
        }
    }
}


private suspend fun fetchData(onSuccess: (List<News>) -> Unit) {
    val response = withContext(Dispatchers.IO) {
        getJsonResponse("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")
    }
    val newsList = parseJson(response)
    onSuccess(newsList)
}

private suspend fun getJsonResponse(urlString: String): String = suspendCoroutine { continuation ->
    val url = URL(urlString)
    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
    val response = StringBuilder()
    try {
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        continuation.resume(response.toString())
    } catch (e: Exception) {
        continuation.resumeWithException(e)
    } finally {
        connection.disconnect()
    }
}

private fun parseJson(jsonString: String): List<News> {
    val jsonObject = JSONObject(jsonString)
    val jsonArray = jsonObject.getJSONArray("articles")
    val newsList = mutableListOf<News>()
    for (i in 0 until jsonArray.length()) {
        val article = jsonArray.getJSONObject(i)
        val title = article.getString("title")
        val url = article.getString("url")
        newsList.add(News(title, url))
    }
    return newsList
}