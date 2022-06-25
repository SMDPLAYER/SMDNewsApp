package uz.smd.newsapp.view.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import uz.smd.newsapp.adapter.NewsAdapter
import uz.smd.newsapp.databinding.FragmentSearchNewsBinding
import uz.smd.newsapp.utils.EndlessScrollListener
import uz.smd.newsapp.utils.QUERY_PAGE_SIZE
import uz.smd.newsapp.utils.Resource
import uz.smd.newsapp.utils.SEARCH_NEWS_TIME_DELAY
import uz.smd.newsapp.view.base.BaseFragment
import uz.smd.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchNewsFragment : BaseFragment<FragmentSearchNewsBinding, NewsViewModel>() {

    override val viewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter
    var job: Job? = null

    val TAG = "SearchNewsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       inistview()
    }

    private fun inistview() = with(binding) {
        newsAdapter = NewsAdapter(requireContext()){
            viewModel.insertNews(it)
            toast("News Saved")
        }
        rv_SearchNews.apply {
            adapter = newsAdapter

            val scrollListener = object : EndlessScrollListener() {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    viewModel.getSearchNews(etSearch.text.toString())
                }
            }
            addOnScrollListener(scrollListener)
        }

        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.newsData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse?.articles?.toList())
                        val totalPagesResult =
                            newsResponse!!.totalResult / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPagesResult
                        if (isLastPage) {
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured $message")
                        Toast.makeText(context, "Error!! $message", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private fun showProgressBar() {
        progressBar_searchnews.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        progressBar_searchnews.visibility = View.INVISIBLE
        isLoading = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSearchNews.adapter=null
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchNewsBinding.inflate(inflater, container, false)
}