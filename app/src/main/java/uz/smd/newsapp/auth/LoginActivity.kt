package uz.smd.newsapp.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import uz.smd.newsapp.R
import uz.smd.newsapp.storage.UIModeDataStore
import uz.smd.newsapp.storage.room.NewsDatabase
import uz.smd.newsapp.utils.toast
import uz.smd.newsapp.view.main.MainActivity
import uz.smd.newsapp.viewmodel.NewsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    var _emailText: EditText? = null
    var _passwordText: EditText? = null
    var _loginButton: Button? = null
    var _signupLink: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel.isLogged{
            onLoginSuccess()
        }
        _loginButton = findViewById(R.id.btn_login) as Button
        _signupLink = findViewById(R.id.link_signup) as TextView
        _passwordText = findViewById(R.id.input_password) as EditText
        _emailText = findViewById(R.id.input_email) as EditText
        _loginButton!!.setOnClickListener { login() }

        _signupLink!!.setOnClickListener {
            // Start the Signup activity
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
            //                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        _loginButton!!.isEnabled = false


//        val progressDialog = ProgressDialog(this@LoginActivity,
//                R.style.AppTheme_Dark_Dialog)
//        progressDialog.isIndeterminate = true
//        progressDialog.setMessage("Login...")
//        progressDialog.show()


        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()
        viewModel.liveError.observe(this){
            if (it!=null){
                _loginButton!!.isEnabled = true
                toast(this, it)
                viewModel.liveError.value=null
            }
        }
        viewModel.getUsers(email,password){
//            toast(this, "Login...")
            onLoginSuccess()
        }


        // TODO: Implement your own authentication logic here.

//        android.os.Handler().postDelayed(
//                {
//                    // On complete call either onLoginSuccess or onLoginFailed
//                    // onLoginFailed();
//                    progressDialog.dismiss()
//                }, 3000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    fun onLoginSuccess() {
        _loginButton!!.isEnabled = true
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()

        _loginButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "enter a valid email address"
            valid = false
        } else {
            _emailText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText!!.error = null
        }

        return valid
    }

    companion object {
        private val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
    }
}
