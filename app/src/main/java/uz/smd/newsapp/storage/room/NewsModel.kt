package uz.smd.newsapp.storage.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.response.Source


@Entity(tableName = "News_Table")
data class NewsModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "sourceId")
    val sourceId: String?,

    @ColumnInfo(name = "sourceName")
    val sourceName: String?,

    @ColumnInfo(name = "author")
    val author: String?,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "description")
    val description: String?,



    @ColumnInfo(name = "imgurl")
    val image: String?,

    @ColumnInfo(name = "time")
    val time: String?,

    @ColumnInfo(name = "content")
    val content: String?
){
    fun toArticle(): Article {
        return Article(
            id = this.id,
            source = Source(this.sourceId?:"",this.sourceName?:""),
            author = this.author?:"",
            title = this.title?:"",
            description = this.description?:"",
            url = this.url?:"",
            urlToImage = this.image?:"",
            publishedAt = this.time?:"",
            content = this.content?:""
        )
    }
}