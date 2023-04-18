package com.example.memesappes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.R
import com.example.memesappes.models.Meme
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.memesappes.EMAIL
import com.example.memesappes.FULLNAME
import com.example.memesappes.PREF_NAME
import com.example.memesappes.models.MemeHome
import com.example.memesappes.models.MemeLikeHome
import com.example.memesappes.models.UserHome
import com.example.memesappes.utils.ApiInterface
import com.example.memesappes.utils.BackendUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeMemesAdapter(private val context: Context,var memList: MutableList<MemeHome>) : RecyclerView.Adapter<HomeMemesAdapter.HomeMemesAdapter>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMemesAdapter {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.memes_single_row, parent, false)
        return HomeMemesAdapter(view)
    }

    private var mSharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)


    override fun onBindViewHolder(holder: HomeMemesAdapter, position:Int) {

        val desc = memList[position].text

        if(memList[position].participants.any { user -> user.email == mSharedPref.getString(EMAIL, "").toString() }) {
            holder.btnFavoriser.setColorFilter(Color.RED)
        } else {
            holder.btnFavoriser.setColorFilter(Color.WHITE)
        }

        val uri = Uri.parse(BackendUrl+""+memList[position].image)
        holder.memeDesc.text = desc
        holder.likeTextV.text = memList[position].nbrLike.toString()
        Glide.with(context)
            .load(uri)
            .into(holder.memePic)

        holder.itemView.setOnClickListener{
            println("item pressed")
            /*   val intent = Intent(holder.itemView.context, DetailActivity::class.java)
               intent.apply {
                   putExtra(PICTURE, championsList[position].champPic)
                   putExtra(NAME, name)
                   putExtra(ROLE, role)
               }
               holder.itemView.context.startActivity(intent)*/
        }

        holder.btnFavoriser.setOnClickListener {
            println("==============================>"+position)
            println("favorite pressed"+memList[position]._id)

            ApiInterface.create().LikeMemes(memList[position]._id,mSharedPref.getString(EMAIL, "").toString(),
                mSharedPref.getString(FULLNAME, "").toString(),
                position)
                .enqueue(object : Callback<MemeLikeHome> {
                    override fun onResponse(call: Call<MemeLikeHome>?, response: Response<MemeLikeHome>?) {

                        val msg = response?.body()
                        if (msg != null) {
                            memList[msg.p].participants = msg.participants
                            memList[msg.p].nbrLike = msg.nbrLike
                            notifyDataSetChanged()
                            println(msg)
                        }
                        println(msg)
                    }

                    override fun onFailure(call: Call<MemeLikeHome>?, t: Throwable?) {
                        println("erreur like user")
                    }
                })
            notifyDataSetChanged()
        }

    }

    override fun getItemCount() = memList.size


    class HomeMemesAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val memePic : ImageView = itemView.findViewById<ImageView>(R.id.memeImageView)
        val memeDesc : TextView = itemView.findViewById<TextView>(R.id.memeTextView)
        val btnFavoriser : ImageView = itemView.findViewById(R.id.likeBtn)
        val likeTextV : TextView = itemView.findViewById<TextView>(R.id.likeTextView)
    }
}