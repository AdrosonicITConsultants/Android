package com.adrosonic.craftexchange.utils

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.bumptech.glide.Glide
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
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

        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())


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

        fun deleteImageCache(context: Context) {
            try {
                Glide.get(context).clearMemory()
                AsyncTask.execute {
                    Glide.get(context).clearDiskCache()
                }
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

        fun filterSpinner(context : Context ,array : List<String>, spinner : Spinner?) {
            var adapter= ArrayAdapter(context, R.layout.simple_spinner_item, array)
            var filterBy : String
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = adapter
            spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    filterBy = ""
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(position > 0){
                        filterBy = parent?.getItemAtPosition(position).toString()
                        Log.e("spin","fil : $filterBy")

                    }else{
                        filterBy = ""
                    }
                }
            })
        }


        fun getRealPathFromFileURI(context: Context, contentUri: Uri): String {
            var filePath = ""
            val myDir: File
            try {
                if (!File(context.cacheDir, BROWSING_IMGS).exists()) File(context.cacheDir, BROWSING_IMGS).mkdir()

                if (contentUri.path!!.contains(".")) {
                        if(contentUri.lastPathSegment!!.contains("/")) {
                            myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment!!.substring(
                                contentUri.lastPathSegment!!.lastIndexOf("/")+1))
                        } else myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + "")
                    }
                    else myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + ".jpg")

                if(contentUri.lastPathSegment!!.length>42){
                    val renamed = File(context.cacheDir, BROWSING_IMGS + "/" + System.currentTimeMillis()+ ".jpg")
                    myDir.renameTo( renamed)
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
                    outputStream?.close()
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

        fun isValidGST(gst:String):Boolean {
            val mPattern = Pattern.compile("[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9]{1}[A-Z]{1}[0-9]{1}")
            val mMatcher = mPattern.matcher(gst)
            return mMatcher.matches()
        }

        fun isValidCIN(cin:String):Boolean {
            val mPattern = Pattern.compile("[A-Z]{1}[0-9]{5}[A-Z]{2}[0-9]{4}[A-Z]{3}[0-9]{6}")
            val mMatcher = mPattern.matcher(cin)
            return mMatcher.matches()
        }

        fun validFileSize(path:String): Boolean {
            var size = 0.00
                val file = File(path)
                val fileSizeInBytes = file.length()
                val fileSizeInKB: Double = (fileSizeInBytes / 1024).toDouble()
                size += fileSizeInKB
            val fileSizeInMB: Double = (size / 1024)
            return fileSizeInMB < 1
        }

        fun messageDialog(context : Context,message: String) {
            val builder = AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            builder.setMessage(message)
                .setPositiveButton("Ok"){ dialog, id ->
                    dialog.cancel()
                }
            builder.create().show()
        }

        fun clearPrefs(){
            val editor = Prefs.edit()
            editor.clear()
            editor.commit()
            editor.apply()
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

        fun getBrandLogoUrl(userId : Long?, imagename : String?) : String{
            return "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${userId}/CompanyDetails/Logo/${imagename}"
        }

        fun getProfilePhotoUrl(artisanId : Long?, imagename : String?) : String{
            return "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${artisanId}/ProfilePics/${imagename}"
        }

        fun getProductsImagesUrl(productId : Long?,imagename : String?) : String{
            return "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/Product/${productId}/${imagename}"
        }
        fun getCustomProductImagesUrl(productId : Long?,imagename : String?) : String{
            return "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/CustomProduct/${productId}/${imagename}"
        }
        fun setImageResource(context: Context?,imageView:ImageView,imageId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageDrawable(context?.getResources()?.getDrawable(imageId, context?.getTheme()));
            } else {
                imageView.setImageDrawable(context?.getDrawable(imageId));
            }
        }

        fun validTotalFileSize(filePaths: ArrayList<String>): Pair<Boolean,String> {
            var files=""
            var statusList=ArrayList<Pair<Boolean,String>>()
            var size = 0.00
            val iterator = filePaths.iterator()
            while (iterator.hasNext()) {
                val path = iterator.next()
                val file = File(path)
                val fileSizeInBytes = file.length()
                val fileSizeInKB: Double = (fileSizeInBytes / 1024).toDouble()
                if((fileSizeInKB/1024)>1)statusList.add(Pair( false,file.name))
                else statusList.add(Pair( true,file.name))
                size += fileSizeInKB
            }
            val fileSizeInMB: Double = (size / 1024)
            for(s in statusList)
            {
                if(s.first==false) files=files+s.second+","
            }
            Log.e("validate","files:"+files)
            if(files.isEmpty()) return Pair(true,files)
            else return Pair(false,files)
        }

        fun returnDisplayDate(date:String):String{
            var dt=date.substring(0,10)//2020-08-07T10:25:02.000+0000
            return dt
//            if (date != null) {
//                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
//                format.timeZone = TimeZone.getTimeZone("UTC")
//                try {
//                    val datelong = format.parse(date)
//                    val inputdate = datelong.time //1519710742000L
//                    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm aaa", Locale.ENGLISH)
//                    val inputdatedt = formatter.format(inputdate)//complete date time for email details screen
//                    return inputdatedt
//                } catch (e: Exception) {
//                    return ""
//                }
//            } else ""
        }
    }
}