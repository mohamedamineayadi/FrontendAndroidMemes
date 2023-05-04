package com.example.memesappes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.adapters.HomeMemesAdapter
import com.example.memesappes.adapters.ProfileMemesAdapter
import com.example.memesappes.adapters.ProfileMemesAdapter2
import com.example.memesappes.models.Meme
import com.example.memesappes.models.MemeHome
import com.example.memesappes.utils.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: ProfileMemesAdapter2

    lateinit var memeList : MutableList<MemeHome>

    private lateinit var txtusername: TextView
    private lateinit var txtemail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        memesRecyclerView = rootView.findViewById(R.id.recyclerMemesProfile)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        memesRecyclerView.layoutManager = layoutManager

        val sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        txtusername = rootView.findViewById(R.id.txtUserName)
        txtusername.text = ""+sharedPreferences.getString(FULLNAME,"NULL")
        txtemail = rootView.findViewById(R.id.txtUserEmail)

        memeList = mutableListOf()

        ApiInterface.create().getAllMemesByUser(sharedPreferences.getString(EMAIL,"NULL").toString())
            .enqueue(object : Callback<MutableList<MemeHome>> {
                override fun onResponse(call: Call<MutableList<MemeHome>>?, response: Response<MutableList<MemeHome>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                            memeList.add(MemeHome(m._id, m.text, m.createdBy, m.fullname_creator, m.image, m.nbrLike, m.participants))
                        memeAdapter = ProfileMemesAdapter2(requireContext(),memeList)


                        txtemail.text = "Posts: "+memes.count()

                        memesRecyclerView.adapter = memeAdapter

                        //memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = ProfileMemesAdapter2(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        //memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<MemeHome>>?, t: Throwable?) {
                    println("erreur get user")
                    memeAdapter = ProfileMemesAdapter2(requireContext(),memeList)

                    memesRecyclerView.adapter = memeAdapter

                    //memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            })

        return rootView
    }

}