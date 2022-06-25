package uz.smd.newsapp.storage.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.smd.newsapp.response.Article
import uz.smd.newsapp.response.Source


@Entity(tableName = "Users_Table")
data class UserModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "mobile")
    val mobile: String,

    @ColumnInfo(name = "password")
    val password: String,
)