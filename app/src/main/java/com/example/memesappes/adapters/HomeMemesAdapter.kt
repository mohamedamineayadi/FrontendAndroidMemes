package com.example.memesappes.adapters

import android.content.Context
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
import com.example.memesappes.utils.BackendUrl

class HomeMemesAdapter(private val context: Context,var memList: MutableList<Meme>) : RecyclerView.Adapter<HomeMemesAdapter.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.memes_single_row, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {

        val desc = memList[position].text

        val uri = Uri.parse(BackendUrl+""+memList[position].image)
        holder.memeDesc.text = desc
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
            println("favorite pressed")
            /*val car = Car(
                name,
                model,
                price,
                carList[position].carLogo
            )
            if(AppDataBase.getDatabase(holder.itemView.context).carDao().findCar(car.carName) == null){
                AppDataBase.getDatabase(holder.itemView.context).carDao().insert(car)
                println("must save")
            }else{
                Snackbar.make(holder.itemView, "car already added in Favorite!",Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(holder.itemView.context ,R.color.colorSecondaryLight))
                    .show();
            }*/
        }

    }

    override fun getItemCount() = memList.size


    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val memePic : ImageView = itemView.findViewById<ImageView>(R.id.memeImageView)
        val memeDesc : TextView = itemView.findViewById<TextView>(R.id.memeTextView)
        val btnFavoriser : ImageView = itemView.findViewById(R.id.likeBtn)
    }
}