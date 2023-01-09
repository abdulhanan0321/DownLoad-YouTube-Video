package com.example.assignmentyounite

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), DownLoadListener {
    private var arrayOfLink = mutableListOf<DownLoadModel>()
    var downLoadAbleLink = mutableListOf<String>()
    lateinit var downLoad: DownLoadFunction
    private var downLoadBtn: Button? = null
    private var recyclerView: RecyclerView? = null
    private var downLoadAdapter: DownloadingAdapter? = null
    private val multiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){ isGranted ->
        if (isGranted.containsValue(true)) {
            if (downLoadAbleLink.size > 2)
                runDownLoad()
            else {
                Toast.makeText(this, "please wait making link ready", Toast.LENGTH_LONG).show()
            }
        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downLoadBtn = findViewById(R.id.downLoad)
        recyclerView = findViewById(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        downLoad = DownLoadFunction()

        for (u in 0 until 3){
            val model = DownLoadModel()
            model.progress = 0
            when(u){
                0 -> model.link = "https://www.youtube.com/watch?v=C7OQHIpDlvA"
                1 -> model.link = "https://www.youtube.com/watch?v=5hPtU8Jbpg0"
                2 -> model.link = "https://www.youtube.com/watch?v=YLslsZuEaNE"
            }
            arrayOfLink.add(model)
        }

        setOnClick()

        downLoadAdapter = DownloadingAdapter(arrayOfLink)
        recyclerView?.adapter = downLoadAdapter

        for (i in 0 until arrayOfLink.size){
            object : YouTubeExtractor(this) {
                @SuppressLint("StaticFieldLeak")
                override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?,
                                                  vMeta: VideoMeta?
                ) {
                    if (ytFiles != null) {
                        val itag = 18
                        val downloadUrl = ytFiles[itag].url
                        downLoadAbleLink.add(downloadUrl)
                        if (downLoadAbleLink.size == 3){
                            Toast.makeText(this@MainActivity, "downLoad it now", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.extract(arrayOfLink[i].link)
        }

    }

    /**
     * function to set on Click listener to item
     * */
    private fun setOnClick(){
        downLoadBtn?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                if (downLoadAbleLink.size > 2)
                    runDownLoad()
                else {
                    Toast.makeText(this, "please wait making link ready", Toast.LENGTH_LONG).show()
                }

            }else {
                val builder1 = AlertDialog.Builder(this)
                builder1.setMessage("Storage Permission needed")
                builder1.setCancelable(true)

                builder1.setPositiveButton("Yes") { dialog, id ->
                    val permission = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    multiplePermissionLauncher.launch(permission)
                    dialog.cancel()
                }
                builder1.setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                }
                val alert11 = builder1.create()
                alert11.show()
            }

        }
    }

    /**
     * function to downLoad video in background thread
     * */
    private fun runDownLoad(){

        CoroutineScope(Dispatchers.IO).launch{
            async {
                downLoad.downloadFile(0,downLoadAbleLink[0],
                    downLoad.createVideoFile(this@MainActivity)!!,
                    this@MainActivity)
            }

            async {
                downLoad.downloadFile(1,downLoadAbleLink[1],
                    downLoad.createVideoFile(this@MainActivity)!!,
                    this@MainActivity)
            }

            async {
                downLoad.downloadFile(2,downLoadAbleLink[2],
                    downLoad.createVideoFile(this@MainActivity)!!,
                    this@MainActivity)
            }
                    }
    }

    /**
     * function to update recycler with progress
     * @param position list position
     * @param progress progress percentage
     * */
    override fun progress(position: Int ,progress: Int) {
        arrayOfLink[position].apply {
            this.progress = progress
        }
        downLoadAdapter?.updateList(arrayOfLink)
    }
}