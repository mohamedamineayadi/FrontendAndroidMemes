package com.example.memesappes.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.memesappes.Profile2Activity
import com.example.memesappes.R
import com.example.memesappes.models.MemeLeaderboard
import com.example.memesappes.utils.BackendUrl


class LeaderboardAdapter(private val context: Context, var memList: MutableList<MemeLeaderboard>) : RecyclerView.Adapter<LeaderboardAdapter.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_leaderboard, parent, false)
        return MemeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {

        println("==>"+position)
        holder.memeDesc.text = memList[position].fullname_creator
        holder.memePoint.text = memList[position].points.toString()+" Points"

        if(position == 0){
            holder.memePic.setImageResource(R.drawable.one)
        }
        else if (position == 1){
            holder.memePic.setImageResource(R.drawable.two)
        }else if (position == 2){
            holder.memePic.setImageResource(R.drawable.three)
        }else{
            holder.memePic.setImageResource(R.drawable.ic_no_rank)
        }

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
    }

    override fun getItemCount() = memList.size



    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val memePic : ImageView = itemView.findViewById<ImageView>(R.id.memeImageViewz)
        val memeDesc : TextView = itemView.findViewById<TextView>(R.id.memeTextViewz)
        val memePoint : TextView = itemView.findViewById<TextView>(R.id.memePointTextViewz)
    }
}


/*
 */