package com.example.memesappes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.adapters.HomeMemesAdapter
import com.example.memesappes.adapters.LeaderboardAdapter
import com.example.memesappes.adapters.ProfileMemesAdapter
import com.example.memesappes.models.MemeHome
import com.example.memesappes.models.MemeLeaderboard
import com.example.memesappes.utils.ApiInterface
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaderboardActivity : AppCompatActivity() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: LeaderboardAdapter

    lateinit var memeList : MutableList<MemeLeaderboard>

    lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)


        memesRecyclerView = findViewById(R.id.recyclerMemesLeaderboard)

        btnBack = findViewById(R.id.btnBack)

        btnBack = findViewById(R.id.btnBack)

        memeList = mutableListOf()


        btnBack.setOnClickListener{
            finish()
        }

        ApiInterface.create().getAllMemesLeaderboard()
            .enqueue(object : Callback<MutableList<MemeLeaderboard>> {
                override fun onResponse(call: Call<MutableList<MemeLeaderboard>>?, response: Response<MutableList<MemeLeaderboard>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                        {
                            memeList.add(MemeLeaderboard(m._id, m.createdBy, m.fullname_creator, m.points))
                        }

                        memeAdapter = LeaderboardAdapter(this@LeaderboardActivity,memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(this@LeaderboardActivity, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = LeaderboardAdapter(this@LeaderboardActivity,memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(this@LeaderboardActivity, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<MemeLeaderboard>>?, t: Throwable?) {
                    println("erreur get user")
                    memeAdapter = LeaderboardAdapter(this@LeaderboardActivity,memeList)

                    memesRecyclerView.adapter = memeAdapter

                    memesRecyclerView.layoutManager = LinearLayoutManager(this@LeaderboardActivity, LinearLayoutManager.VERTICAL, false)
                }
            })


    }
}