package com.balance.displayingdeviceimageapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
class MainActivity : AppCompatActivity() {

    companion object{
        const val PICK_FROM_GALLERY = 34
    }


    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialization  of context to current activity
        mContext = this

        // initialize the recycler view with the ui
        recyclerView = findViewById(R.id.recycler_view)
        imageAdapter = ImageAdapter()
        recyclerView.apply {

            adapter = imageAdapter
            layoutManager = GridLayoutManager(mContext,4)
        }

        if (hasPermission()) getAllImages()

    }


    private fun hasPermission() : Boolean{

        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PICK_FROM_GALLERY
            )
            false
        } else true
    }


    var isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private fun getAllImages(){
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID + " DESC"
        //Stores all the images from the gallery in Cursor
        //Stores all the images from the gallery in Cursor
        val cursor: Cursor? = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy)

        cursor?.let {
            val count: Int = it.count

            //Create an array to store path to all the images
            val arrPath = arrayOfNulls<String>(count)

            for (i in 0 until count) {
                it.moveToPosition(i)
                val dataColumnIndex: Int = it.getColumnIndex(MediaStore.Images.Media.DATA)
                //Store the path of the image
                arrPath[i] = it.getString(dataColumnIndex)
            }
            if (!arrPath.isNullOrEmpty()){
                arrPath.toMutableList().let { it1 -> imageAdapter.setImageList(it1) }
            }
        }

        cursor?.close()
    }

}