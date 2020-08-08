package com.adrosonic.craftexchange.utils

import android.Manifest
import android.R
import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.bumptech.glide.Glide
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.dialog_gen_enquiry_success.*
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*
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
                if (contentUri.toString().contains("attachmentprovider")) {
                    myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + "" + System.currentTimeMillis() + ".jpg")
                } else {
                    if (contentUri.path!!.contains(".")) {
                        if(contentUri.lastPathSegment!!.contains("/")) {
                            myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment!!.substring(
                                contentUri.lastPathSegment!!.lastIndexOf("/")+1))
                        } else myDir = File(context.cacheDir, BROWSING_IMGS + "/" + contentUri.lastPathSegment + "")
                    }
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

        fun enquiryGenProgressDialog(context : Context): Dialog {
            var dialog = Dialog(context)
            dialog?.setContentView(com.adrosonic.craftexchange.R.layout.dialog_gen_enquiry_holdon)
            dialog?.setCanceledOnTouchOutside(false) // disables outside the box touch
            dialog?.setCancelable(false) // disables backbtn click when popup visible//
            dialog?.create()
            return dialog
        }

        fun enquiryGenSuccessDialog(context : Context, enquiryId : String) : Dialog {
            var dialog = Dialog(context)
            dialog?.setContentView(com.adrosonic.craftexchange.R.layout.dialog_gen_enquiry_success)

            var id = SpannableString(enquiryId)
            id.setSpan(ForegroundColorSpan(Color.BLACK), 0, id.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dialog?.success_enquiry_id?.append(id)

            dialog?.btn_success_cancel?.setOnClickListener {
                dialog?.dismiss()
            }
            dialog?.btn_success_view_enquiry?.setOnClickListener {
                //TODO : View Enquiry details in enquiry landing page
            }
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.create()
            return dialog
        }

        fun enquiryGenExistingDialog(context : Context,enquiryId: String, productName : String) : Dialog {
            var dialog = Dialog(context)
            dialog?.setContentView(com.adrosonic.craftexchange.R.layout.dialog_gen_enquiry_update_or_new)

            var id = SpannableString(enquiryId)
            id.setSpan(ForegroundColorSpan(Color.BLACK), 0, id.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dialog?.existing_enquiry_id?.append(id)

            dialog?.existing_product_title?.text = productName

            dialog?.existing_btn_cancel?.setOnClickListener {
                dialog?.dismiss()
            }
            dialog?.existing_btn_view_enquiry?.setOnClickListener {
                //TODO : View Enquiry details in enquiry landing page
            }
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.create()

            return dialog
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
        fun setImageResource(context: Context?,imageView:ImageView,imageId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageDrawable(context?.getResources()?.getDrawable(imageId, context?.getTheme()));
            } else {
                imageView.setImageDrawable(context?.getDrawable(imageId));
            }
        }
    }
}