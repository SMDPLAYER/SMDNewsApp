package uz.smd.newsapp.view.headline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import uz.smd.newsapp.adapter.NewsAdapter
import uz.smd.newsapp.databinding.FragmentHeadlinesNewsBinding
import uz.smd.newsapp.utils.Resource
import uz.smd.newsapp.utils.categories
import uz.smd.newsapp.view.base.BaseFragment
import uz.smd.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_headlines_news.*
import kotlinx.android.synthetic.main.fragment_news.*

@AndroidEntryPoint
class HeadLinesNewsFragment : BaseFragment<FragmentHeadlinesNewsBinding, NewsViewModel>() {
    override val viewModel: NewsViewModel by viewModels()
    lateinit var categoriesAdapter: uz.smd.newsapp.adapter.CategoriesAdapter
    lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initsview()
        initCategoriesRv()
    }

    private fun initCategoriesRv() {
        categoriesAdapter = uz.smd.newsapp.adapter.CategoriesAdapter(categories)
        categoriesAdapter.onItemClickListener {
            viewModel.getHeadlinesNews(it)

        }

        rv_categories.apply {
            adapter = categoriesAdapter
        }

    }

    private fun initsview() = with(binding) {

        newsAdapter = NewsAdapter(requireContext()){
            viewModel.insertNews(it)
        }
        rvHeadlinesnews.apply {
            setHasFixedSize(true)
            adapter = newsAdapter
            setupobserver()
        }


    }

    private fun setupobserver() {
        viewModel.newsData.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    progressBarStatus(false)
                    tryAgainStatus(false)
                    newsAdapter.differ.submitList(response.data!!.articles)
                }
                is Resource.Error -> {
                    tryAgainStatus(true, response.message!!)
                    progressBarStatus(false)
                }
                is Resource.Loading -> {
                    tryAgainStatus(false)
                    progressBarStatus(true)
                }
            }
        })

    }


    private fun tryAgainStatus(status: Boolean, message: String = "message") {
        if (status) {
            tryAgainMessage_headlinenews.text = message
            tryAgainLayout_headlinenews.visibility = View.VISIBLE
        } else {
            tryAgainLayout_headlinenews.visibility = View.GONE
        }

    }

    private fun progressBarStatus(status: Boolean) {
        progressBar_headlinenews.visibility = if (status) View.VISIBLE else View.GONE

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHeadlinesNewsBinding.inflate(inflater, container, false)
}