package com.balance.displayingdeviceimageapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    companion object{
        const val PICK_FROM_GALLERY = 34
    }


    private lateinit var imageAdapter: ImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mContext : Context
    var imagePath = ""
    private val imagePathList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialization  of context to current activity
        mContext = this

        // select multiple images.
        val selectMultipleImageButton = findViewById<Button>(R.id.multiple_image)

        selectMultipleImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_FROM_GALLERY
            )
        }


        // initialize the recycler view with the ui
        recyclerView = findViewById(R.id.recycler_view)
        imageAdapter = ImageAdapter()
        recyclerView.apply {

            adapter = imageAdapter
            layoutManager = GridLayoutManager(mContext, 4)
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

    private fun getAllImages(){

        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID + " DESC"
        //Stores all the images from the gallery in Cursor
        //Stores all the images from the gallery in Cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        )

        cursor?.let {
            val count: Int = it.count

            //Create an array to store path to all the images
            val arrPath = mutableListOf<String>()

            for (i in 0 until count) {
                it.moveToPosition(i)
                val dataColumnIndex: Int = it.getColumnIndex(MediaStore.Images.Media.DATA)
                //Store the path of the image
                arrPath.add(it.getString(dataColumnIndex))
            }

            arrPath.let { items ->
                imageAdapter.setImageList(items)
            }

        }

        cursor?.close()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            //First clear all list.
            imagePathList.clear()

            if (data.clipData != null) {
                val count: Int = data.clipData!!.itemCount

                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    getImageFilePath(imageUri)
                }
                imageAdapter.setImageList(imagePathList)

            } else if (data.data != null) {
                val imgUri: Uri = data.data!!
                getImageFilePath(imgUri)
                imageAdapter.setImageList(imagePathList)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getImageFilePath(uri: Uri) {

        val file = File(uri.path)
        val filePath: List<String> = file.path.split(":")
        val imageId = filePath[filePath.size - 1]
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", arrayOf(imageId), null)
        if (cursor != null) {
            cursor.moveToFirst()
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            imagePathList.add(imagePath)
            cursor.close()
        }
    }

}