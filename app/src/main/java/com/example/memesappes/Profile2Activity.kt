package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.adapters.ProfileMemesAdapter
import com.example.memesappes.models.MemeHome
import com.example.memesappes.utils.ApiInterface
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile2Activity : AppCompatActivity() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: ProfileMemesAdapter

    lateinit var memeList : MutableList<MemeHome>

    lateinit var mSharedPref: SharedPreferences
    private lateinit var txtusername: TextView
    private lateinit var txtemail: TextView

    lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        mSharedPref = getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        memesRecyclerView = findViewById(R.id.recyclerMemesProfile)

        txtusername = findViewById(R.id.txtUserName)
        txtusername.text = ""+mSharedPref.getString(FULLNAME,"NULL")
        //txtemail = findViewById(R.id.txtUserEmail)

        btnBack = findViewById(R.id.btnBack)

        memeList = mutableListOf()


        btnBack.setOnClickListener{
            val mainIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainIntent)
            finish()

        }

        ApiInterface.create().getAllMemesByUser(mSharedPref.getString(EMAIL,"NULL").toString())
            .enqueue(object : Callback<MutableList<MemeHome>> {
                override fun onResponse(call: Call<MutableList<MemeHome>>?, response: Response<MutableList<MemeHome>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                        {
                            if(m._id == intent.getStringExtra("id").toString())
                                memeList.add(MemeHome(m._id, m.text, m.createdBy, m.fullname_creator, m.image, m.nbrLike, m.participants))
                        }

                        memeAdapter = ProfileMemesAdapter(this@Profile2Activity,memeList)


                        /*txtemail.text = "Posts: "+memes.count()

                         */
                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(this@Profile2Activity, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = ProfileMemesAdapter(this@Profile2Activity,memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(this@Profile2Activity, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<MemeHome>>?, t: Throwable?) {
                    println("erreur get user")
                    memeAdapter = ProfileMemesAdapter(this@Profile2Activity,memeList)

                    memesRecyclerView.adapter = memeAdapter

                    memesRecyclerView.layoutManager = LinearLayoutManager(this@Profile2Activity, LinearLayoutManager.VERTICAL, false)
                }
            })
    }
}