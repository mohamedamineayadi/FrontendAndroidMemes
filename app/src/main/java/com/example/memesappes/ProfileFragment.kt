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
import com.example.memesappes.utils.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    lateinit var memesRecyclerView: RecyclerView
    lateinit var memeAdapter: ProfileMemesAdapter

    lateinit var memeList : MutableList<Meme>

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
        txtusername.text = "FullName: "+requireArguments().getString(FULLNAME,"NULL")
        txtemail = rootView.findViewById(R.id.txtUserEmail)
        txtemail.text = "Email: "+requireArguments().getString(EMAIL,"NULL")

        memeList = mutableListOf()

        ApiInterface.create().getAllMemesByUser(requireArguments().getString(EMAIL,"NULL"))
            .enqueue(object : Callback<MutableList<Meme>> {
                override fun onResponse(call: Call<MutableList<Meme>>?, response: Response<MutableList<Meme>>?) {

                    val memes = response?.body()
                    println("==>"+memes)
                    if(memes != null){
                        for (m in memes)
                            memeList.add(Meme(m._id, m.text, m.createdBy, m.fullname_creator, m.image))
                        memeAdapter = ProfileMemesAdapter(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


                    }else{
                        println("empty list")
                        memeAdapter = ProfileMemesAdapter(requireContext(),memeList)

                        memesRecyclerView.adapter = memeAdapter

                        memesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    }

                }

                override fun onFailure(call: Call<MutableList<Meme>>?, t: Throwable?) {
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