package uz.smd.newsapp.storage.room

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: UserModel)

    @Query("SELECT * FROM Users_Table")
    fun getNewsFromDatabase(): LiveData<List<UserModel>>


    @Delete
    fun deleteNews(news: UserModel)


}