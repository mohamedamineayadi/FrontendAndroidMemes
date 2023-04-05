package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.memesappes.MainActivity
import com.example.memesappes.models.ForgotUser
import com.example.memesappes.models.User
import com.example.memesappes.utils.ApiInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.tasks.Task

const val PREF_NAME = "DATA_MEMES"
const val FULLNAME = "FULLNAME"
const val EMAIL = "EMAIL"
const val TOKEN = "TOKEN"
const val USERID= "USERID"
const val IS_REMEMBRED = "IS_REMEMBRED"

class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    lateinit var btnLogin: MaterialButton
    lateinit var txtcreate: MaterialButton
    lateinit var btnForgotPassword: MaterialButton
    lateinit var btnLoginGoogle: MaterialButton

    lateinit var txtEmail: TextInputEditText
    lateinit var txtLayoutEmail: TextInputLayout

    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var cbRememberMe: CheckBox
    lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        txtEmail = findViewById(R.id.txtEmail)
        txtLayoutEmail = findViewById(R.id.txtLayoutEmail)
        txtLayoutEmail.editText?.setHintTextColor(ContextCompat.getColor(this, R.color.white))

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnLogin = findViewById(R.id.btnLogin)
        txtcreate = findViewById(R.id.btnCreate)
        btnForgotPassword = findViewById(R.id.btnForgotPassowrd)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        if (mSharedPref.getBoolean(IS_REMEMBRED, false)) {
            val mainIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        btnForgotPassword.setOnClickListener {
            txtLayoutEmail!!.error = null

            if (txtEmail?.text!!.isEmpty()) {
                txtLayoutEmail!!.error = "must not be empty"
                return@setOnClickListener
            }

            if (!isEmailValid(txtEmail?.text.toString())){
                txtLayoutEmail!!.error = "Check your email !"

                return@setOnClickListener
            }

            doForgotPassword()

        }

        btnLogin.setOnClickListener{
            txtLayoutEmail!!.error = null
            txtLayoutPassword!!.error = null

            if (txtEmail?.text!!.isEmpty()) {
                txtLayoutEmail!!.error = "must not be empty"
                return@setOnClickListener
            }
            if (!isEmailValid(txtEmail?.text.toString())){
                txtLayoutEmail!!.error = "Check your email !"

                return@setOnClickListener
            }
            if (txtPassword?.text!!.isEmpty()) {
                txtLayoutPassword!!.error = "must not be empty"
                return@setOnClickListener
            }

            if (cbRememberMe.isChecked){
                mSharedPref.edit().apply{
                    putBoolean(IS_REMEMBRED, cbRememberMe.isChecked)
                }.apply()
            }

            doLogin()


        }

        txtcreate.setOnClickListener{
            val mainIntent = Intent(this, RegisterActivity::class.java)
            startActivity(mainIntent)
            finish()
        }


        btnLoginGoogle = findViewById(R.id.btnLoginGoogle)
        btnLoginGoogle.setOnClickListener {
            signIn();
        }

    }


    // Call this function when user clicks on the "Sign in with Google" button
    fun signIn() {
        googleSignInClient.signOut()
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Gmail
                val account = task.getResult(ApiException::class.java)
                val userEmail = account?.email

                ApiInterface.create().EmailExist(userEmail.toString())
                    .enqueue(object : Callback<User>{
                        override fun onResponse(call: Call<User>?, response: Response<User>?) {

                            val user = response?.body()

                            if(user != null){
                                if(user.exist == true){
                                    println("true user exist!")
                                    Toast.makeText(this@LoginActivity, "Logged in successfully with $userEmail", Toast.LENGTH_SHORT).show()

                                    if (cbRememberMe.isChecked){
                                        mSharedPref.edit().apply{
                                            putBoolean(IS_REMEMBRED, cbRememberMe.isChecked)
                                        }.apply()
                                    }

                                    mSharedPref.edit().apply {
                                        putString(USERID, user._id)
                                        putString(FULLNAME, user.fullname)
                                        putString(EMAIL, user.email)
                                    }.apply()

                                    val mainIntent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                }else{
                                    val mainIntent = Intent(this@LoginActivity, RegisterGmailActivity::class.java).apply {
                                        putExtra(EMAIL, userEmail.toString())
                                        putExtra(FULLNAME, account?.displayName.toString())
                                    }
                                    startActivity(mainIntent)
                                    finish()
                                }
                            }

                        }

                        override fun onFailure(call: Call<User>?, t: Throwable?) {
                            println("erreur get user")
                        }
                    })

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("MainActivity", "Google sign in failed", e)
                Toast.makeText(this, "Failed to log in with Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun doLogin(){

        val apiInterface = ApiInterface.create()


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = txtEmail!!.text.toString()
        jsonParams["password"] = txtPassword!!.text.toString()

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.seConnecter(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()
                println("==>"+response.body())

                var tokenv = user?.token

                if (tokenv != null){
                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()

                    mSharedPref.edit().apply {
                        putString(USERID, user?._id)
                        putString(TOKEN, tokenv)
                        putString(FULLNAME, user?.fullname)
                        putString(EMAIL, user?.email)

                    }.apply()

                    val mainIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()

                }else{
                    Toast.makeText(this@LoginActivity, "Password or Mail incorrect !" , Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })
    }

    private fun doForgotPassword(){

        val apiInterface = ApiInterface.create()


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = txtEmail!!.text.toString()

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.sendMailForgetPassword(body).enqueue(object : Callback<ForgotUser> {

            override fun onResponse(call: Call<ForgotUser>, response: Response<ForgotUser>) {

                val data = response.body()

                if (data?.created == true){
                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Success:")
                        .setMessage("Code reset password sent successfully to you're mail: "+txtEmail!!.text)
                        .setPositiveButton("Ok"){ dialogInterface, which ->
                            dialogInterface.dismiss()

                            mSharedPref.edit().apply {
                                putString(EMAIL, txtEmail!!.text.toString())
                            }.apply()

                            val mainIntent = Intent(applicationContext, ForgotPasswordActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }.create().show()
                }else{
                    AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Warning:")
                        .setMessage("Email doesn't exist")
                        .setPositiveButton("Ok"){ dialogInterface, which ->
                            dialogInterface.dismiss()
                        }.create().show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<ForgotUser>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })
    }

}