package uz.smd.newsapp.utils

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Request


/**
 * Created by Siddikov Mukhriddin on 6/25/22
 */
fun provideCache(context: Context):Cache{
    val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
    val cache = Cache(context.cacheDir, cacheSize)
    return cache
}
fun providesOfflineInterceptor(context: Context): Interceptor {
    return Interceptor { chain ->
        var request: Request = chain.request()
        if (!hasInternetConnection(context)) {
            val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
            request = request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .removeHeader("Pragma")
                .build()
        }
        chain.proceed(request)
    }
}


fun providesOnlineInterceptor(): Interceptor {
    return Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
        response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAge")
            .removeHeader("Pragma")
            .build()
    }
}

//fun isInternetAvailable(context: Context): Boolean {
//    var isConnected: Boolean = false // Initial Value
//    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
//    if (activeNetwork != null && activeNetwork.isConnected)
//        isConnected = true
//    return isConnected
//}

//var onlineInterceptor = Interceptor { chain ->
//    val response = chain.proceed(chain.request())
//    val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
//    response.newBuilder()
//        .header("Cache-Control", "public, max-age=$maxAge")
//        .removeHeader("Pragma")
//        .build()
//}
//
//var offlineInterceptor = Interceptor { chain ->
//    var request: Request = chain.request()
//    if (!hasInternetConnection(NewsApp.instance)) {
//        val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
//        request = request.newBuilder()
//            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
//            .removeHeader("Pragma")
//            .build()
//    }
//    chain.proceed(request)
//}

