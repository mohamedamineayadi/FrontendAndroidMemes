package com.example.memesappes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.adapters.HomeMemesAdapter
import com.example.memesappes.models.Meme
import com.example.memesappes.models.User
import com.example.memesappes.utils.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeMemesFragment : Fragment() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: HomeMemesAdapter

    lateinit var memeList : MutableList<Meme>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home_memes, container, false)

        memesRecyclerView = rootView.findViewById(R.id.recyclerMemesHome)

        memeList = mutableListOf()

         ApiInterface.create().getAllMemes()
            .enqueue(object : Callback<MutableList<Meme>> {
                override fun onResponse(call: Call<MutableList<Meme>>?, response: Response<MutableList<Meme>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                        memeList.add(Meme(m._id, m.text, m.createdBy, m.fullname_creator, m.image))
                        memeAdapter = HomeMemesAdapter(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = HomeMemesAdapter(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<Meme>>?, t: Throwable?) {
                    println("erreur get user")
                    memeAdapter = HomeMemesAdapter(requireContext(),memeList)

                    memesRecyclerView.adapter = memeAdapter

                    memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            })

        return rootView
    }



    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            HomeMemesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}