package com.example.assignmentyounite

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class DownLoadFunction {

    private val TAG = "Download"
    suspend fun downloadFile(position: Int, fileUrl: String, directory: File, downLoadListener: DownLoadListener) {
        val MEGABYTE = 1024 * 1024;
        try {
            Log.v(TAG, "downloadFile() invoked ")
            Log.v(TAG, "downloadFile() fileUrl $fileUrl")
            Log.v(TAG, "downloadFile() directory $directory")
            val url = URL(fileUrl)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(directory)
            val totalSize: Int = urlConnection.contentLength
            val buffer = ByteArray(MEGABYTE)
            var total: Long = 0
            var bufferLength = 0
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                total += bufferLength;
                if (totalSize > 0) {// only if total length is known
                    val progress = total * 100 / totalSize
                    Log.v(TAG, "downloadFile Progress $progress")
                }
                withContext(Dispatchers.Main){
                    downLoadListener.progress(position,((total * 100 / totalSize).toInt()))
                }
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()
            Log.v(TAG, "downloadFile() completed ")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e(TAG, "downloadFile() error" + e.message)
            Log.e(TAG, "downloadFile() error" + e.stackTrace)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.e(TAG, "downloadFile() error" + e.message)
            Log.e(TAG, "downloadFile() error" + e.stackTrace)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "downloadFile() error" + e.message)
            Log.e(TAG, "downloadFile() error" + e.stackTrace)
        }
    }


    @Throws(IOException::class)
    fun createVideoFile(context: Context): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "MP4_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".mp4",  /* suffix */
            storageDir /* directory */
        )
    }
}