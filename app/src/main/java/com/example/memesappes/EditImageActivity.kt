package com.example.memesappes

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.memesappes.filters.FilterViewAdapter
import com.example.memesappes.models.Meme
import com.example.memesappes.tools.EditingToolsAdapter
import com.example.memesappes.utils.ApiInterface
import com.example.memesappes.utils.BackendUrl
import com.malkinfo.myphotoeditor.base.BaseActivity
import com.malkinfo.myphotoeditor.filters.FilterListener
import com.malkinfo.myphotoeditor.tools.ToolType
import ja.burhanrashid52.photoeditor.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class EditImageActivity : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties,
    EmojiBSFragment.EmojiListener,
    StickerBSFragment.StickerListener,
    EditingToolsAdapter.OnItemSelected,
    FilterListener {
    private var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private var mRvFilters: RecyclerView? = null
    private val mEditingToolsAdapter: EditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter: FilterViewAdapter = FilterViewAdapter(this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet: ConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false
    lateinit var mSharedPref: SharedPreferences
    lateinit var service:ApiInterface

    //lateinit var image : ImageView
    private var imageUri: Uri ? = null
    lateinit var file: File
    private var currentBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        //mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf")
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        val llmTools = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.setLayoutManager(llmTools)
        mRvTools!!.setAdapter(mEditingToolsAdapter)
        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.setLayoutManager(llmFilters)
        mRvFilters!!.setAdapter(mFilterViewAdapter)


        mSharedPref = getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)


        println("============>"+intent.getStringExtra("desc").toString())
        service = Retrofit.Builder()
            .baseUrl(BackendUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView!!)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor!!.setOnPhotoEditorListener(this)

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private fun initViews() {
        val imgUndo: ImageView
        val imgRedo: ImageView
        val imgGallery: ImageView
        val imgSave: ImageView
        val imgClose: ImageView
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)
        imgUndo = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)
        imgRedo = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)
        imgGallery = findViewById(R.id.imgGallery)
        imgGallery.setOnClickListener(this)
        imgSave = findViewById(R.id.imgSave)
        imgSave.setOnClickListener(this)
        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener(this)
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment: TextEditorDialogFragment =
            TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
            override fun onDone(inputText: String?, colorCode: Int) {
                mPhotoEditor!!.editText(rootView!!, inputText, colorCode)
                mTxtCurrentTool!!.setText(R.string.label_text)
            }
        })
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    fun onRemoveViewListener(numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        Log.d(
            TAG,
            "onStartViewChangeListener() called with: viewType = [$viewType]"
        )
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        Log.d(
            TAG,
            "onStopViewChangeListener() called with: viewType = [$viewType]"
        )
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgUndo -> mPhotoEditor!!.undo()
            R.id.imgRedo -> mPhotoEditor!!.redo()
            R.id.imgSave -> saveImage()
            R.id.imgClose -> onBackPressed()
            R.id.imgGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...")
            val file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ""
                        + System.currentTimeMillis() + ".png"
            )
            try {
                file.createNewFile()
                /*val saveSettings: SaveSettings = SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build()*/
                mPhotoEditor!!.saveAsFile(
                    file.absolutePath,
                    //saveSettings,
                    object : PhotoEditor.OnSaveListener {
                        override fun onSuccess(@NonNull imagePath: String) {
                            hideLoading()
                            //showSnackbar("Image Saved Successfully")
                            //mPhotoEditorView!!.source.setImageURI(Uri.fromFile(File(imagePath)))

                            if(Uri.fromFile(File(imagePath)) != null)
                            {
                                val reqFile = RequestBody.create("image/*".toMediaType(), file)
                                val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
                                val name = RequestBody.create("text/plain".toMediaType(), "image")


                                println("==>"+mSharedPref.getString(EMAIL,"NULL")+ " && "
                                        +mSharedPref.getString(FULLNAME,"NULL")+ " && "+ name+" && "+body
                                        +" && "+intent.getStringExtra("desc").toString())
                                service.createMeme(body,name,intent.getStringExtra("desc").toString(),
                                    mSharedPref.getString(EMAIL,"NULL").toString(),
                                    mSharedPref.getString(FULLNAME,"NULL").toString()
                                )
                                    .enqueue(object:
                                        Callback<Meme> {

                                        override fun onResponse(call: Call<Meme>, response: Response<Meme>) {
                                            if (response.code()==200){
                                                Toast.makeText(this@EditImageActivity, "Meme create", Toast.LENGTH_LONG).show()
                                                val mainIntent = Intent(applicationContext, MainActivity::class.java)
                                                startActivity(mainIntent)
                                                finish()
                                                finish()
                                            }else
                                                Toast.makeText(this@EditImageActivity, "Cannot create meme error", Toast.LENGTH_LONG).show()
                                        }

                                        override fun onFailure(call: Call<Meme>, t: Throwable) {
                                            Toast.makeText(this@EditImageActivity, "Error: "+t.toString(), Toast.LENGTH_LONG).show()
                                        }

                                    })
                            }else
                            {
                                hideLoading()
                                AlertDialog.Builder(this@EditImageActivity)
                                    .setTitle("Failed")
                                    .setMessage("please select image")
                                    .setPositiveButton("Ok"){ dialogInterface, which ->
                                        dialogInterface.dismiss()
                                    }.create().show()
                                println("===>please select image")
                            }

                        }

                        override fun onFailure(@NonNull exception: Exception) {
                            hideLoading()
                            showSnackbar("Failed to save")
                        }
                    })
            } catch (e: IOException) {
                e.printStackTrace()
                hideLoading()
                showSnackbar(e.message)
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap?): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

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


        //currentBitmap = (image.drawable as BitmapDrawable).bitmap
        // Return the saved bitmap uri
        return Uri.parse(file?.absolutePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo = data!!.extras!!["data"] as Bitmap?
                    mPhotoEditorView!!.source.setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri = data!!.data
                    val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                    mPhotoEditorView!!.source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
        mTxtCurrentTool!!.setText(R.string.label_emoji)
    }

    override  fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
    }

    override fun isPermissionGranted(isGranted: Boolean, permission: String?) {
        if (isGranted) {
            saveImage()
        }
    }

    private fun showSaveDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure to cancel creation of this meme ?")
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog, which -> finish() })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        /*builder.setNeutralButton("Discard",
            DialogInterface.OnClickListener { dialog, which -> finish() })*/
        builder.create().show()
    }

    override fun onFilterSelected(photoFilter: PhotoFilter?) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    override fun onToolSelected(toolType: ToolType?) {
        when (toolType) {
            ToolType.BRUSH -> {
                mPhotoEditor!!.setBrushDrawingMode(true)
                mTxtCurrentTool!!.setText(R.string.label_brush)
                mPropertiesBSFragment!!.show(
                    getSupportFragmentManager(),
                    mPropertiesBSFragment!!.getTag()
                )
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment: TextEditorDialogFragment =
                    TextEditorDialogFragment.show(this)
                textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
                    override fun onDone(inputText: String?, colorCode: Int) {
                        mPhotoEditor!!.addText(inputText, colorCode)
                        mTxtCurrentTool!!.setText(R.string.label_text)
                    }
                })
            }
            ToolType.ERASER -> {
                mPhotoEditor!!.brushEraser()
                mTxtCurrentTool!!.setText(R.string.label_eraser)
            }
            ToolType.FILTER -> {
                mTxtCurrentTool!!.setText(R.string.label_filter)
                showFilter(true)
            }
            ToolType.EMOJI -> mEmojiBSFragment!!.show(getSupportFragmentManager(), mEmojiBSFragment!!.getTag())
            ToolType.STICKER -> mStickerBSFragment!!.show(
                getSupportFragmentManager(),
                mStickerBSFragment!!.getTag()
            )
        }
    }

    fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.getId(), ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.getId(), ConstraintSet.END)
        }
        val changeBounds = ChangeBounds()
        changeBounds.setDuration(350)
        changeBounds.setInterpolator(AnticipateOvershootInterpolator(1.0f))
        TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor!!.isCacheEmpty) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG = EditImageActivity::class.java.simpleName
        const val EXTRA_IMAGE_PATHS = "extra_image_paths"
        private const val CAMERA_REQUEST = 52
        private const val PICK_REQUEST = 53
    }
}
