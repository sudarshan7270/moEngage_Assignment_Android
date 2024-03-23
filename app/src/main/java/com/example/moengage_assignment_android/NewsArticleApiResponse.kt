package com.example.moengage_assignment_android

data class NewsArticleApiResponse (
    var status: String? = null,
    var articles: ArrayList<Article> = arrayListOf()
) {
    data class Source (
        var id: String? = null,
        var name: String? = null
    )

    data class Article (
        var source: Source? = Source(),
        var author: String? = null,
        var title: String? = null,
        var description: String? = null,
        var url: String? = null,
        var urlToImage: String? = null,
        var publishedAt: String? = null,
        var content: String? = null
    )
}