package com.example.memesappes

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
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
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
    lateinit var txtMemesTitle: TextInputEditText
    lateinit var txtLayoutMemesTitle: TextInputLayout
    private var rbD: RadioButton? = null
    private var rbBW: RadioButton? = null

    lateinit var image : ImageView
    private var imageUri: Uri ? = null
    lateinit var file: File
    private var currentBitmap: Bitmap? = null
    //private var defaultBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {
            imageUri = data?.data!!
            image?.setImageURI(imageUri)
            // Récupérer le bitmap de l'image sélectionnée
            var bitmap : Bitmap? = null
            try {
                bitmap = image?.drawToBitmap();
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Ajouter du texte sur l'image
            val canvas = Canvas(bitmap!!)
            val paint = Paint()
            val text = txtMemesTitle.text.toString()
            paint.textSize = 50f
            val canvasWidth = canvas.width
            val canvasHeight = canvas.height

            val textWidth = paint.measureText(text)
            val textHeight = paint.fontMetrics.bottom - paint.fontMetrics.top


// Calculate the x and y coordinates for the text to be centered
            val x = (canvasWidth - textWidth) / 2
            val y = (canvasHeight + textHeight) / 2
            canvas.drawText(""+text, x, y, paint)

            // Appliquer un filtre sur l'image
            val filter = LightingColorFilter(0xFF0000, 0x000000)
            val matrix = ColorMatrix()
            val colorMatrix = ColorMatrix()

            matrix.setSaturation(0f)
            val filterColorMatrix = ColorMatrixColorFilter(matrix)
            val drawable = BitmapDrawable(resources, bitmap)
            drawable.colorFilter = filterColorMatrix
            drawable.setColorFilter(filter)

            if(rbBW!!.isChecked)
            {

                paint.color = Color.WHITE
                // Appliquer le filtre "Noir et blanc"
                colorMatrix.setSaturation(0f)
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)

                // Appliquer le filtre "Noir"
                colorMatrix.set(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            if(rbD!!.isChecked)
            {
                paint.color = Color.RED
            }
            // Appliquer une rotation sur l'image
            /*val matrixRotation = Matrix()
            matrixRotation.postRotate(90f)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrixRotation, true)*/

            // Appliquer un effet de flou sur l'image
            val radius = 25f
            val filterBlur = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
            paint.maskFilter = filterBlur
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            // Afficher l'image personnalisée
            image?.setImageBitmap(bitmap)
            var src : Uri  = bitmapToFile(bitmap);
            image?.setImageURI(src)


            /*var b : Bitmap?;
            b = image?.drawToBitmap();
            var src : Uri  = bitmapToFile(b);
            image?.setImageURI(src);*/
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


        currentBitmap = (image.drawable as BitmapDrawable).bitmap
        // Return the saved bitmap uri
        return Uri.parse(file?.absolutePath)
    }


    fun applyBlackWhiteFilter(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val blackWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(blackWhiteBitmap)
        val paint = Paint()

        val matrix = ColorMatrix(floatArrayOf(
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0.33f, 0.33f, 0.33f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        val filter = ColorMatrixColorFilter(matrix)
        paint.colorFilter = filter

        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        return blackWhiteBitmap
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

        txtMemesTitle = view.findViewById(R.id.txtMemesTitle)
        txtLayoutMemesTitle = view.findViewById(R.id.txtLayoutMemesTitle)

        rbD = view.findViewById(R.id.themeD)
        rbBW = view.findViewById(R.id.themeBW)



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

        val themeRadioGroup: RadioGroup = view.findViewById(R.id.radioGroup)

        themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.themeBW && currentBitmap != null) {
                val originalBitmap = (image.drawable as BitmapDrawable).bitmap
                val blackWhiteBitmap = applyBlackWhiteFilter(originalBitmap)
                var src : Uri  = bitmapToFile(blackWhiteBitmap);
                image?.setImageURI(src)
            }
            if (checkedId == R.id.themeD && currentBitmap != null) {
                var src : Uri  = bitmapToFile(currentBitmap);
                image.setImageURI(src)
            }
        }

        createMemeButton?.setOnClickListener{
            if(currentBitmap!=null && rbBW!!.isChecked) {
                println("=================ici")
            }
            println("create meme clicked")
            if (validate()){
                if (imageUri != null) {
                    val reqFile = RequestBody.create("image/*".toMediaType(), this.file)
                    val body = MultipartBody.Part.createFormData("image", this.file!!.name, reqFile)
                    val name = RequestBody.create("text/plain".toMediaType(), "image")


                    println("==>"+requireArguments().getString(EMAIL,"NULL")+ " && "
                    +requireArguments().getString(FULLNAME,"NULL")+ " && "+ name+" && "+body
                    +" && "+txtMemesDesc?.text.toString())
                    service.createMeme(body,name,txtMemesDesc?.text.toString(),
                        requireArguments().getString(EMAIL,"NULL"),
                        requireArguments().getString(FULLNAME,"NULL")
                    )
                        .enqueue(object:
                            Callback<Meme> {

                            override fun onResponse(call: Call<Meme>, response: Response<Meme>) {
                                if (response.code()==200){
                                    txtMemesDesc.setText("");
                                    txtMemesTitle.setText("");
                                    Toast.makeText(context, "Meme create", Toast.LENGTH_LONG).show()
                                }else
                                    Toast.makeText(context, "Cannot create error", Toast.LENGTH_LONG).show()
                            }

                            override fun onFailure(call: Call<Meme>, t: Throwable) {
                                Toast.makeText(context, "Error: "+t.toString(), Toast.LENGTH_LONG).show()
                            }

                        })

                }else{
                    AlertDialog.Builder(requireContext())
                        .setTitle("Failed")
                        .setMessage("please select image")
                        .setPositiveButton("Ok"){ dialogInterface, which ->
                            dialogInterface.dismiss()
                        }.create().show()
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