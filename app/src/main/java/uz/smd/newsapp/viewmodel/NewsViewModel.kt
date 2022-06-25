package uz.smd.newsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import uz.smd.newsapp.repository.NewsRepository
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.response.NewsResponse
import uz.smd.newsapp.response.SourceResponse
import uz.smd.newsapp.storage.UIModeDataStore
import uz.smd.newsapp.storage.room.NewsModel
import uz.smd.newsapp.utils.Resource
import uz.smd.newsapp.utils.categories
//import uz.smd.newsapp.utils.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    application: Application,
    private val repository: NewsRepository,
    private val uiModeDataStore: UIModeDataStore
) : AndroidViewModel(application) {

    fun setLogged(){
        viewModelScope.launch {
            uiModeDataStore.setLogged(true)
        }
    }
    // get ui mode
    val getUIMode = uiModeDataStore.uiMode

    // save ui mode
    fun saveToDataStore(isNightMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            uiModeDataStore.saveToDataStore(isNightMode)
        }
    }

    val newsData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    val newsSourcesData: MutableLiveData<Resource<SourceResponse>> = MutableLiveData()

    private val newsDataTemp = MutableLiveData<Resource<NewsResponse>>()

    private var news = 1
    private var headlinenews = 1
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getNews()
    }

    fun insertNews( news: Article?) {
        if (news!=null)
        repository.insertNews(news.toNewsModel())
    }

    fun deleteNews( news: Article) {
        repository.deleteNews(news.toNewsModel())
    }

    fun getNewsFromDB(): LiveData<List<NewsModel>>? {
        return repository.getAllNews()
    }

    fun getNews() = viewModelScope.launch {
        fetchNews()
    }

    fun getHeadlinesNews(category: String = categories.first()) = viewModelScope.launch {
        fetchheadlinews(category)
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        fetchsearchnews(searchQuery)
    }

    fun getSourcesNews() = viewModelScope.launch {
        fetchSourcesNews()
    }

    private suspend fun fetchNews() {
        newsData.postValue(Resource.Loading())
        try {
//            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getNews()
                newsDataTemp.postValue(Resource.Success(response.body()!!))
                newsData.postValue(handleNewsResponse(response))
//            } else {
//                newsData.postValue(Resource.Error("No Internet Connection"))
//                toast(getApplication(), "No Internet Connection.!")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private suspend fun fetchheadlinews(category: String = categories.first()) {
        newsData.postValue(Resource.Loading())
        try {
//            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getTopHeadlines(category, headlinenews)
                newsData.postValue(handleNewsResponse(response))
//            } else {
//                newsData.postValue(Resource.Error("No Internet Connection"))
//                toast(getApplication(), "No Internet Connection.!")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private suspend fun fetchsearchnews(searchQuery: String) {
        newsData.postValue(Resource.Loading())
        try {
//            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getSearchNews(searchQuery, searchNewsPage)
                newsData.postValue(handleSearchNewsResponse(response))
//            } else {
//                newsData.postValue(Resource.Error("No Internet Connection"))
//                toast(getApplication(), "No Internet Connection.!")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private suspend fun fetchSourcesNews() {
        newsSourcesData.postValue(Resource.Loading())
        try {
//            if (hasInternetConnection<NewsApp>()) {
                val response = repository.getSourceNews()
                newsSourcesData.postValue(handleSourceNewsResponse(response))
//            } else {
//                newsSourcesData.postValue(Resource.Error("No Internet Connection"))
//                toast(getApplication(), "No Internet Connection.!")
//            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsSourcesData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
                else -> newsSourcesData.postValue(
                    Resource.Error(
                        t.message!!
                    )
                )
            }
        }
    }

    private fun handleSourceNewsResponse(response: Response<SourceResponse>): Resource<SourceResponse>? {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    fun onSearchClose() {
        newsData.postValue(newsDataTemp.value)
    }
}