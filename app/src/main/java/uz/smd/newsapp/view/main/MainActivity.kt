package uz.smd.newsapp.view.main


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import uz.smd.newsapp.R
import uz.smd.newsapp.databinding.ActivityMainBinding
import uz.smd.newsapp.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.view.read.ReadNewsActivity


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NewsApp)
        super.onCreate(savedInstanceState)
        newsViewModel.setLogged()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsViewModel

        initNavigation(binding)
    }

    private fun initNavigation(binding: ActivityMainBinding) {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//         Passing each menu ID as a set of Ids because each
//         menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_newsFragment,
                R.id.nav_headlinesNewsFragment,
                R.id.nav_sourcesNewsFragment,
                R.id.nav_savedNewsFragment
            )
        )

        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

        openReadNews.observe(this){
            if (it!=null){
                val readIntent= Intent(this, ReadNewsActivity::class.java)
                readIntent.putExtra(NEWS_ARTICLE,it)
                startActivity(readIntent)
                openReadNews.value = null
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        val openReadNews = MutableLiveData<Article?>()
        const val NEWS_ARTICLE = "news_article"
    }


}