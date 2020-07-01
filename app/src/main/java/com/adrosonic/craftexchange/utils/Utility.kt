package com.adrosonic.craftexchange.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*
import java.util.regex.Pattern

class Utility {
    companion object{

        val URL_REGEX = Pattern.compile(
            "[a-zA-Z0-9\\'\\`\\‘\\’\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        const val BROWSING_IMGS: String = "BrowsedImages/"

        fun displayMessage(message: String, context: Context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun checkIfInternetConnected(context: Context): Boolean {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connMgr.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun deleteCache(context: Context) {
            try {
                var dir = context.cacheDir
                deleteDir(dir)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

         fun checkPermission(context:Context):Boolean {
            val result = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            return result == PackageManager.PERMISSION_GRANTED
        }

        fun getRealPathFromFileURI(context: Context, contentUri: Uri): String {
            var filePath = ""
            val myDir: File
            try {
                if (!File(context.cacheDir, BROWSING_IMGS).exists()) File(context.cacheDir, BROWSING_IMGS).mkdir()
                if (contentUri.toString().contains("attachmentprovider")) {
                    myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + "" + System.currentTimeMillis() + ".jpg")
                } else {
                    if (contentUri.path!!.contains(".")) myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + "")
                    else myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + ".jpg")
                }
                var inputStream: InputStream? = context.contentResolver.openInputStream(contentUri)
                var outputStream: OutputStream = FileOutputStream(myDir)
                try {
                    var fileReader = ByteArray(4096)
                    var fileSizeDownloaded = 0
                    while (true) {
                        var read = inputStream?.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        read?.let { outputStream.write(fileReader, 0, it) }
                        if (read != null) {
                            fileSizeDownloaded += read
                        }
                    }
                    outputStream.flush()
                    filePath = myDir.path
                    return filePath
                } catch (e: IOException) {
                    return filePath
                } finally {
                    inputStream?.close()
                    outputStream.close()
                }
            } catch (e: IOException) {
                Log.e("ShareIntent", "IOException : $e")
                return filePath
            }
        }

        fun isValidPan(pan:String):Boolean {
            val mPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
            val mMatcher = mPattern.matcher(pan)
            return mMatcher.matches()
        }

        fun messageDialog(context : Context,message: String) {
            val builder = AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            builder.setMessage(message)
                .setPositiveButton("Ok"){ dialog, id ->
                    dialog.cancel()
                }
            builder.create().show()
        }


        fun deleteDir(dir: File): Boolean {
            if (dir.isDirectory) {
                var children = dir.list()
                for (i in children) {
                    var success = deleteDir(File(dir, i))
                    if (!success) {
                        return false
                    }
                }
                return dir.delete()
            } else if (dir.isFile) {
                return dir.delete()
            } else {
                return false
            }
        }

    }
}