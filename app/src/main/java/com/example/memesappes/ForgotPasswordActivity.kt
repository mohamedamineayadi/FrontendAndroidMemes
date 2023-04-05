package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.memesappes.models.ForgotUser
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


class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var btnLogin: MaterialButton
    lateinit var btnChangePassword: MaterialButton

    lateinit var txtCode: TextInputEditText
    lateinit var txtLayoutCode: TextInputLayout

    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout

    lateinit var txtCPassword: TextInputEditText
    lateinit var txtLayoutCPassword: TextInputLayout

    private lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        txtCode = findViewById(R.id.txtCode)
        txtLayoutCode = findViewById(R.id.txtLayoutCode)

        txtPassword = findViewById(R.id.txtPassword)
        txtLayoutPassword = findViewById(R.id.txtLayoutPassword)

        txtCPassword = findViewById(R.id.txtPasswordConfirm)
        txtLayoutCPassword = findViewById(R.id.txtLayoutPasswordConfirm)

        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener{
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        btnChangePassword = findViewById(R.id.btnChanePassword)

        btnChangePassword.setOnClickListener {
            if(validate()){
                doChangePassword()
            }
        }

    }


    private fun doChangePassword(){

        val apiInterface = ApiInterface.create()


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = mSharedPref.getString(EMAIL, "").toString()
        jsonParams["password"] = txtPassword.text!!.toString()
        jsonParams["resetpwd"] = txtCode.text!!.toString()

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.sendModifiedPassword(body).enqueue(object : Callback<ForgotUser> {

            override fun onResponse(call: Call<ForgotUser>, response: Response<ForgotUser>) {

                val data = response.body()

                if (data?.key == true){
                    AlertDialog.Builder(this@ForgotPasswordActivity)
                        .setTitle("Succes")
                        .setMessage("Your password was reset Successfully ")
                        .setPositiveButton("Ok"){ dialogInterface, which ->
                            dialogInterface.dismiss()
                            val mainIntent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }.create().show()

                }else{
                    AlertDialog.Builder(this@ForgotPasswordActivity)
                        .setTitle("Warning:")
                        .setMessage("Wrong Code !")
                        .setPositiveButton("Ok"){ dialogInterface, which ->
                            dialogInterface.dismiss()
                        }.create().show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<ForgotUser>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })
    }

    private fun validate(): Boolean {
        txtLayoutCode.error = null
        txtLayoutPassword.error = null
        txtLayoutCPassword.error = null



        if (txtCode.text!!.isEmpty()){
            txtLayoutCode.error = getString(R.string.mustNotBeEmpty)
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
            txtLayoutCPassword.error = "confirm new password not match "
            return false
        }

        return true
    }

}