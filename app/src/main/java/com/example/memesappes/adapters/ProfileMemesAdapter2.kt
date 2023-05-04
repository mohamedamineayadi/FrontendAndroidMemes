package com.example.memesappes.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.models.Meme
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.memesappes.*
import com.example.memesappes.models.MemeHome
import com.example.memesappes.models.User
import com.example.memesappes.utils.ApiInterface
import com.example.memesappes.utils.BackendUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileMemesAdapter2(private val context: Context,var memList: MutableList<MemeHome>) : RecyclerView.Adapter<ProfileMemesAdapter2.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mems_single_row_profile2, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {

        val uri = Uri.parse(BackendUrl+""+memList[position].image)
        Glide.with(context)
            .load(uri)
            .into(holder.memePic)

        holder.itemView.setOnClickListener{
            println("item pressed"+memList[position].text)
            val mainIntent = Intent(context, Profile2Activity::class.java).apply {
                putExtra("id", memList[position]._id)
            }
            context.startActivity(mainIntent)
            (context as Activity).finish()
            /*val intent = Intent(holder.itemView.context, DetailActivity::class.java)
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

        val memePic : ImageView = itemView.findViewById<ImageView>(R.id.memeImageView)
    }
}