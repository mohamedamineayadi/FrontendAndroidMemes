package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import com.example.memesappes.models.User
import com.example.memesappes.utils.ApiInterface
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterGmailActivity : AppCompatActivity() {
    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var txtCPassword: TextInputEditText
    lateinit var txtLayoutCPassword: TextInputLayout

    lateinit var btnLogin: MaterialButton
    lateinit var btnCreate: MaterialButton

    lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gmail)

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        txtCPassword = findViewById(R.id.txtPasswordConfirm)
        txtLayoutCPassword = findViewById(R.id.txtLayoutPasswordConfirm)


        btnLogin = findViewById(R.id.btnLogin)
        btnCreate = findViewById(R.id.btnCreate)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        btnLogin.setOnClickListener{
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        btnCreate.setOnClickListener{
            if(validate()){
                doCreate()
            }
        }
    }


    private fun doCreate(){
        println(intent.getStringExtra(FULLNAME).toString())
        println("==>"+intent.getStringExtra(EMAIL).toString())
        val apiInterface = ApiInterface.create()


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["fullname"] =intent.getStringExtra(FULLNAME).toString()
        jsonParams["email"] = intent.getStringExtra(EMAIL).toString()
        jsonParams["password"] = txtPassword!!.text.toString()

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.createUser(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@RegisterGmailActivity, "Creation Success", Toast.LENGTH_SHORT).show()
                    mSharedPref.edit().apply {
                        putString(USERID, user?._id)
                        putString(FULLNAME, user?.fullname)
                        putString(EMAIL, user?.email)
                    }.apply()

                    val mainIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@RegisterGmailActivity, "User already Exist !", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterGmailActivity, t.message, Toast.LENGTH_SHORT).show()
                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }

    private fun validate(): Boolean {
        txtLayoutPassword.error = null
        txtLayoutCPassword.error = null

        if (txtPassword.text!!.isEmpty()){
            txtLayoutPassword.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtCPassword.text!!.isEmpty()){
            txtLayoutCPassword.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if (txtCPassword.text!!.toString() != txtPassword.text!!.toString()){
            txtLayoutCPassword.error = "confirm password not match "
            return false
        }

        return true
    }

}