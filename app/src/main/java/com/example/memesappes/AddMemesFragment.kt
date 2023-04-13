package com.example.memesappes

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.example.memesappes.models.Meme
import com.example.memesappes.utils.ApiInterface
import com.example.memesappes.utils.BackendUrl
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.MediaType.Companion.toMediaType


class AddMemesFragment : Fragment() {
    lateinit var back2btn: Button
    lateinit var createMemeButton: Button
    lateinit var txtMemesDesc: TextInputEditText
    lateinit var txtLayoutMemesDesc: TextInputLayout
    lateinit var image : ImageView
    private var imageUri: Uri ? = null
    lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {
            imageUri = data?.data!!
            image?.setImageURI(imageUri)
            var b : Bitmap?;
            b = image?.drawToBitmap();
            var src : Uri  = bitmapToFile(b);
            image?.setImageURI(src);
        }
    }
    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap?): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(context)

        // Initialize a new file instance to save bitmap object
        this.file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file?.absolutePath)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_memes, container, false)

        image = view.findViewById(R.id.imageView8)
        txtMemesDesc = view.findViewById(R.id.txtMemesDesc)
        txtLayoutMemesDesc = view.findViewById(R.id.txtLayoutMemesDesc)
        createMemeButton = view.findViewById(R.id.createMemeButton)

        val service = Retrofit.Builder()
            .baseUrl(BackendUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        image?.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }



        createMemeButton?.setOnClickListener{
            if (validate()){
                if (imageUri != null) {
                    val reqFile = RequestBody.create("image/*".toMediaType(), this.file)
                    val body = MultipartBody.Part.createFormData("image", this.file!!.name, reqFile)
                    val name = RequestBody.create("text/plain".toMediaType(), "image")


                    service.createMeme(body,name,txtMemesDesc?.text.toString(),
                        requireArguments().getString(EMAIL,"NULL"),
                        requireArguments().getString(FULLNAME,"NULL")
                    )
                        .enqueue(object:
                            Callback<Meme> {

                            override fun onResponse(call: Call<Meme>, response: Response<Meme>) {
                                if (response.code()==200){
                                    txtMemesDesc.setText("");
                                    Toast.makeText(context, "Meme create", Toast.LENGTH_LONG).show()
                                }else
                                    Toast.makeText(context, "Cannot create error", Toast.LENGTH_LONG).show()
                            }

                            override fun onFailure(call: Call<Meme>, t: Throwable) {
                                Toast.makeText(context, "Error: "+t.toString(), Toast.LENGTH_LONG).show()
                            }

                        })

                }else{
                    println("===>please select image")
                }
            }

        }

        return view
    }

    private fun validate(): Boolean {
        txtLayoutMemesDesc.error = null
        if (txtMemesDesc.text!!.isEmpty()){
            txtLayoutMemesDesc.error = getString(R.string.mustNotBeEmpty)
            return false
        }

        return true
    }

    companion object {
        @JvmStatic
        fun newInstance(createdby: String, fullname_creator: String) =
            AddMemesFragment().apply {
                arguments = Bundle().apply {
                    putString(EMAIL, createdby)
                    putString(FULLNAME, fullname_creator)
                }
            }
    }
}