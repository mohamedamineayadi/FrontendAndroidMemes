package com.example.memesappes

import android.util.Patterns.EMAIL_ADDRESS

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import com.example.memesappes.models.User
import com.example.memesappes.utils.ApiInterface
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


class RegisterActivity : AppCompatActivity() {
    lateinit var txtName: TextInputEditText
    lateinit var txtLayoutName: TextInputLayout

    lateinit var txtEmail: TextInputEditText
    lateinit var txtLayoutEmail: TextInputLayout

    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var txtCPassword: TextInputEditText
    lateinit var txtLayoutCPassword: TextInputLayout

    lateinit var btnLogin: MaterialButton
    lateinit var btnCreate: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtName = findViewById(R.id.txtName)
        txtLayoutName = findViewById(R.id.txtLayoutName)

        txtEmail = findViewById(R.id.txtEmail)
        txtLayoutEmail = findViewById(R.id.txtLayoutEmail)

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        txtCPassword = findViewById(R.id.txtPasswordConfirm)
        txtLayoutCPassword = findViewById(R.id.txtLayoutPasswordConfirm)


        btnLogin = findViewById(R.id.btnLogin)
        btnCreate = findViewById(R.id.btnCreate)


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


    fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun doCreate(){
        val apiInterface = ApiInterface.create()


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["fullname"] = txtName!!.text.toString()
        jsonParams["email"] = txtEmail!!.text.toString()
        jsonParams["password"] = txtPassword!!.text.toString()

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.createUser(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@RegisterActivity, "Creation Success", Toast.LENGTH_SHORT).show()
                    val mainIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@RegisterActivity, "User already Exist !", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }

    private fun validate(): Boolean {
        txtLayoutName.error = null
        txtLayoutEmail.error = null
        txtLayoutPassword.error = null
        txtLayoutCPassword.error = null



        if (txtName.text!!.isEmpty()){
            txtLayoutName.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        if (txtEmail.text!!.isEmpty()){
            txtLayoutEmail.error = getString(R.string.mustNotBeEmpty)
            return false
        }
        if(!EMAIL_ADDRESS.matcher(txtEmail?.text!!).matches()){
            txtLayoutEmail!!.error = "must be a valid email"
            return false
        }

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