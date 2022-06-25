package uz.smd.newsapp.view.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import uz.smd.newsapp.adapter.NewsAdapter
import uz.smd.newsapp.databinding.FragmentSavedNewsBinding
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.view.base.BaseFragment
import uz.smd.newsapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment : BaseFragment<FragmentSavedNewsBinding, NewsViewModel>() {
    override val viewModel: NewsViewModel by viewModels()
    var newsAdapter: NewsAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(requireContext()){
            viewModel.deleteNews(it)
        }

        binding.rvSavednews.adapter = newsAdapter
        viewModel.getNewsFromDB()?.observe(viewLifecycleOwner){news->
            val list = mutableListOf<Article>()
            news.forEach { list.add(it.toArticle()) }
            newsAdapter?.differ?.submitList(list)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSavedNewsBinding.inflate(inflater, container, false)
}