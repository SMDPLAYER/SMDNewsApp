package uz.smd.newsapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.smd.newsapp.databinding.ItemsNewsBinding
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.view.main.MainActivity

class NewsAdapter(private val context: Context,val block:(Article)->Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.NewsViewHolder {
        return NewsViewHolder(
            ItemsNewsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        val news = differ.currentList[position]
        holder.bind(news)

    }

    private val differCallback =
        object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NewsViewHolder(private val itemsNewsBinding: ItemsNewsBinding) :
        RecyclerView.ViewHolder(itemsNewsBinding.root) {
        fun bind(articleResponse: Article) {
            itemsNewsBinding.apply {
                itemsNewsBinding.news = articleResponse
                itemsNewsBinding.executePendingBindings()

                itemsNewsBinding.ivShareNews.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, articleResponse.url)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }

                ivSavedNews.setOnClickListener {
                    block(articleResponse)
                }

                ivGotoNews.setOnClickListener {
                    MainActivity.openReadNews.value=(articleResponse)
//                    val builder = CustomTabsIntent.Builder()
//                    val customTabsIntent = builder.build()
//                    customTabsIntent.launchUrl(context, Uri.parse(articleResponse.url))
                }

                itemsNewsBinding.root.setOnClickListener {
                    MainActivity.openReadNews.value=(articleResponse)
//                    val builder = CustomTabsIntent.Builder()
//                    val customTabsIntent = builder.build()
//                    customTabsIntent.launchUrl(context, Uri.parse(articleResponse.url))
                }


            }

        }
    }
}