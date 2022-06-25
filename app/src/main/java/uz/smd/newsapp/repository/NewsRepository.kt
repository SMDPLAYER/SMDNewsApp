package uz.smd.newsapp.repository

import androidx.lifecycle.LiveData
import uz.smd.newsapp.response.NewsResponse
import uz.smd.newsapp.storage.room.NewsDatabase
import uz.smd.newsapp.storage.room.NewsModel
import uz.smd.newsapp.webapi.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiService,private val newsDatabase: NewsDatabase) {




    fun insertNews(news: NewsModel) {
        CoroutineScope(Dispatchers.IO).launch {
            newsDatabase.newsDao().insertNews(news)
        }
    }

    fun deleteNews( news: NewsModel) {

        CoroutineScope(Dispatchers.IO).launch {
            newsDatabase.newsDao().deleteNews(news)
        }
    }

    fun getAllNews(): LiveData<List<NewsModel>> {
        return newsDatabase.newsDao().getNewsFromDatabase()
    }

    suspend fun getNews() = apiService.getNews()

    suspend fun getTopHeadlines(category: String, page: Int): Response<NewsResponse> {
        return apiService.getTopHeadlines(category = category, page = page)
    }

    suspend fun getSearchNews(query: String, page: Int): Response<NewsResponse> {
        return apiService.getSearchNews(searchQuery = query, page = page)
    }

    suspend fun getSourceNews() = apiService.getSourcesNews()


}