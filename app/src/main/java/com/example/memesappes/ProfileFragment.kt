package com.example.memesappes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memesappes.adapters.HomeMemesAdapter
import com.example.memesappes.adapters.ProfileMemesAdapter
import com.example.memesappes.models.Meme
import com.example.memesappes.models.MemeHome
import com.example.memesappes.utils.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: ProfileMemesAdapter

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

        txtusername = rootView.findViewById(R.id.txtUserName)
        txtusername.text = ""+requireArguments().getString(FULLNAME,"NULL")
        txtemail = rootView.findViewById(R.id.txtUserEmail)

        memeList = mutableListOf()

        ApiInterface.create().getAllMemesByUser(requireArguments().getString(EMAIL,"NULL"))
            .enqueue(object : Callback<MutableList<MemeHome>> {
                override fun onResponse(call: Call<MutableList<MemeHome>>?, response: Response<MutableList<MemeHome>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                            memeList.add(MemeHome(m._id, m.text, m.createdBy, m.fullname_creator, m.image, m.nbrLike, m.participants))
                        memeAdapter = ProfileMemesAdapter(requireContext(),memeList)


                        txtemail.text = "Posts: "+memes.count()

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = ProfileMemesAdapter(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<MemeHome>>?, t: Throwable?) {
                    println("erreur get user")
                    memeAdapter = ProfileMemesAdapter(requireContext(),memeList)

                    memesRecyclerView.adapter = memeAdapter

                    memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            })

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(createdby: String, fullname_creator: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(EMAIL, createdby)
                    putString(FULLNAME, fullname_creator)
                }
            }
    }
}