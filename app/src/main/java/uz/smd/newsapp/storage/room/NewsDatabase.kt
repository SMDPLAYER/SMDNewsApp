package uz.smd.newsapp.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsModel::class, UserModel::class], version = 3, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
    abstract fun usersDao(): UsersDao

    companion object {

        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDataseClient(context: Context): NewsDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, NewsDatabase::class.java, "SMD_NEWS")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}