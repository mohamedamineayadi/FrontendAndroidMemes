package com.example.memesappes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.memesappes.EditImageActivity
import com.example.memesappes.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val actionBar = supportActionBar
        actionBar?.hide()
        try {
            Handler().postDelayed({
                startActivity(Intent(this@WelcomeActivity, EditImageActivity::class.java))
                finish()
            },3000)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}