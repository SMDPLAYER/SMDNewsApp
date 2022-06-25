package uz.smd.newsapp.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.smd.newsapp.storage.room.NewsModel
import java.io.Serializable

data class NewsResponse(
    val status: String,
    val totalResult: Int,
    val articles: MutableList<Article>,
)

@Entity(tableName = "news_table")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
) : Serializable {
    fun toNewsModel(): NewsModel {
        return NewsModel(
            id = this.id,
            sourceId = this.source.id,
            sourceName = this.source.name,
            author = this.author,
            title = this.title,
            description = this.description,
            url = this.url,
            image = this.urlToImage,
            time = this.publishedAt,
            content = this.content
        )
    }
}

data class Source(
    val id: String,
    val name: String,
):Serializable
