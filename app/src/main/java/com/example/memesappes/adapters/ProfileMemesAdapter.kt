package com.example.memesappes.adapters

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

class ProfileMemesAdapter(private val context: Context,var memList: MutableList<MemeHome>) : RecyclerView.Adapter<ProfileMemesAdapter.MemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mems_single_row_profile, parent, false)
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

        /*holder.btnEdit.setOnClickListener {
            println("edit pressed")
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
        }*/
        holder.btnDelete.setOnClickListener {
            println("delete pressed")
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Meme: ")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes"){ dialogInterface, which ->
                    println("delete pressed")


                    ApiInterface.create().DeleteMemes(memList[position]._id)
                        .enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                                val msg = response?.body()

                                println(msg)
                            }

                            override fun onFailure(call: Call<String>?, t: Throwable?) {
                                println("erreur Delete user")
                            }
                        })


                    memList.removeAt(position)
                    notifyDataSetChanged()
                }.setNegativeButton("No"){ dialogInterface, which ->
                    dialogInterface.dismiss()
                }.create().show()
        }

    }

    override fun getItemCount() = memList.size


    class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val memePic : ImageView = itemView.findViewById<ImageView>(R.id.memeImageView)
        val memeDesc : TextView = itemView.findViewById<TextView>(R.id.memeTextView)
       // val btnEdit : ImageView = itemView.findViewById(R.id.editBtn)
        val btnDelete : ImageView = itemView.findViewById(R.id.deleteBtn)
    }
}