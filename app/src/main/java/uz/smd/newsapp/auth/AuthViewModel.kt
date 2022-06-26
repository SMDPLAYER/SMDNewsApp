package uz.smd.newsapp.auth

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.smd.newsapp.repository.NewsRepository
import uz.smd.newsapp.storage.UIModeDataStore
import uz.smd.newsapp.storage.room.NewsDatabase
import uz.smd.newsapp.storage.room.UserModel
import uz.smd.newsapp.utils.toast
import javax.inject.Inject

/**
 * Created by Siddikov Mukhriddin on 6/25/22
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val newsDatabase: NewsDatabase,
    private val uiModeDataStore: UIModeDataStore
) : AndroidViewModel(application) {

    fun insertUser(userModel: UserModel) {
        CoroutineScope(Dispatchers.IO).launch {
            newsDatabase.usersDao().insertNews(userModel)
        }
    }

    fun isLogged(function: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (uiModeDataStore.isLogged.first()) {
                function()
            }
        }
    }
val liveError=MutableLiveData<String?>()
    fun getUsers(email:String,password:String, loginSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            newsDatabase.usersDao().getNewsFromDatabase().value.let { list ->
                var userNotFound = true
                var checkPassword = true
                list?.forEach {
                    if (it.email == email) {
                        userNotFound=false
                        if (it.password == password) {
                            loginSuccess()
                        }
                    }
                }
                if (userNotFound)
                    liveError.postValue("User Not Found")
                else  if (checkPassword)
                    liveError.postValue("Check Password")

            }
        }

    }
}