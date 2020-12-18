package com.adrosonic.craftexchangemarketing.utils

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchangemarketing.database.predicates.UserPredicates
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.escalations.EscalationCategoryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.*
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QCData
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchangemarketing.repository.data.response.transaction.TranStatData
import com.adrosonic.craftexchangemarketing.repository.data.response.transaction.TransactionStatusData

import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.dialog_gen_enquiry_success.*
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*
import okhttp3.ResponseBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

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
        var mCraftUser = CraftUser()
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

        fun getAdminRoleDetails() : List<AdminRoleData>?{
            var details = UserConfig.shared.adminRoles
            val gson = GsonBuilder().create()
            val adminData = gson.fromJson(details, AdminRolesResponse::class.java)
            return adminData?.data
        }

        fun getAdminDetails() : List<AdminProfileData>?{
           var details = UserConfig.shared.adminProfile
            val gson = GsonBuilder().create()
            val adminData = gson.fromJson(details, AdminResponse::class.java)
            return adminData?.data
        }

        fun getAdminTeam() : List<AdminsData>? {
            var details = UserConfig.shared.adminTeam
            val gson = GsonBuilder().create()
            val adminData = gson.fromJson(details, AdminsResponse::class.java)
            return adminData?.data
        }

        fun getDeliveryTimeList():List<Datum>?{
            val moqDeliveryJson = UserConfig.shared.moqDeliveryDates
            val gson = GsonBuilder().create()
            val moqDeliveryTime = gson.fromJson(moqDeliveryJson, MoqDeliveryTimesResponse::class.java)
            return moqDeliveryTime.data
        }

        fun getQcQuesData() : QCQuestionData? {
            val gson = GsonBuilder().create()
            var qcObj = gson.fromJson(UserConfig.shared.qcQuestionData.toString(), QCQuestionData::class.java)
            return  qcObj
        }


        fun getQcStageData() : InnerStageData? {
            val gson = GsonBuilder().create()
            var qcObj = gson.fromJson(UserConfig.shared.qcStageData.toString(), InnerStageData::class.java)
            return  qcObj
        }

        fun writeTIResponseBodyToDisk(
            body: ResponseBody,
            enquiryId: String,
            isOld:String,
            context: Context
        ): Boolean {
            try {
                if (!File(context.cacheDir, ConstantsDirectory.TI_PDF_PATH).exists()) File(context.cacheDir,ConstantsDirectory.TI_PDF_PATH).mkdir()
//                if (!File(context.cacheDir, Utility.PI_PDF_PATH+ enquiryId).exists()) File(context.cacheDir,Utility.MANAGED_DOC_PATH+ enquiryId).mkdir()
                val myDir = File(context.cacheDir, "/"+ConstantsDirectory.TI_PDF_PATH + isOld+"Ti${enquiryId}.pdf")
                var inputStream: InputStream = body.byteStream()
                var outputStream: OutputStream = FileOutputStream(myDir)
                try {
                    var fileReader = ByteArray(4096)
                    var fileSize = body.contentLength()
                    var fileSizeDownloaded = 0
                    while (true) {
                        var read = inputStream.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read
                    }
                    outputStream.flush()
                    return true
                } catch (e: IOException) {
                    Log.e("SaveTiPdf", "Exception : "+e.printStackTrace())
                    return false
                } finally {
                    if (inputStream != null) {
                        inputStream.close()
                    }
                    if (outputStream != null) {
                        outputStream.close()
                    }
                }
            } catch (e: Exception) {
                Log.e("SaveTiPdf", "Exception : "+e.printStackTrace())
                return false
            }
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
                Log.e("FileName","1111 ${myDir.name} : ${myDir.name.length}")
                if(myDir.name!!.length>42){
                    val renamed = File(context.cacheDir, BROWSING_IMGS + "/" + System.currentTimeMillis()+ ".jpg")
                    Log.e("FileName","33333  ${renamed.name}")
                    myDir.renameTo( renamed)
                }
                Log.e("FileName","4444  ${myDir.name}")
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

        fun getArtisanQcResponse(respString : String) : QCData? {
            val gson = GsonBuilder().create()
            var qcResp = gson.fromJson(respString,QCData::class.java)
            return qcResp
        }
        fun openTaxInvFile(context: Activity,enquiryId:Long){
            val cacheFile = File(context.cacheDir, ConstantsDirectory.TI_PDF_PATH + "Ti${enquiryId}.pdf")
            try {
                val uri = FileProvider.getUriForFile(context, "com.adrosonic.craftexchange.provider", cacheFile)
                val myIntent = Intent(Intent.ACTION_VIEW)
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.packageName)
                val componentName = context.componentName
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName)
                myIntent.setDataAndType(uri, "application/pdf")
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(myIntent, "Open with"))

            } catch (e: Exception) {
                displayMessage(" Application not installed $e", context)
            }
        }
        fun getDeliveryChallanReceiptUrl(enquiryId : Long?,imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}deliveryChallanReceipt/${enquiryId}/${imagename}"
        }

        fun getAdvancePaymentImageUrl(receiptId : Long?,imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}AdvancedPayment/${receiptId}/${imagename}"
        }

        fun getFinalPaymentImageUrl(receiptId : Long?,imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}FinalPayment/${receiptId}/${imagename}"
        }


        fun overrideFileFromUri(context: Context, bitmap: Bitmap, filename:String) {
            var filePath = ""
            if (!File(context.cacheDir, BROWSING_IMGS).exists()) File(context.cacheDir, BROWSING_IMGS).mkdir()
            val pictureFile = File(context.cacheDir, BROWSING_IMGS + "/" + filename)
            Log.e("overrideFileFromUri","filename: $filename")
            if (pictureFile == null) {
                Log.e("overrideFileFromUri","Error creating media file, check storage permissions: " ) // e.getMessage());
                return
            }
            try {
                if (pictureFile.exists())  pictureFile.delete()
                Log.e("overrideFileFromUri","bitmap: "+bitmap.height )
                val fos = FileOutputStream(pictureFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                Log.e("overrideFileFromUri", "File not found: " + e.message)
            } catch (e: IOException) {
                Log.e("overrideFileFromUri", "Error accessing file: " + e.message)
            }catch (e: Exception) {
                Log.e("overrideFileFromUri", "Error accessing file: " + e.message)
            }
        }
        fun getTransactionStatusData() : List<TranStatData>? {
            val gson = GsonBuilder().create()
            var tranObj = gson.fromJson(UserConfig.shared.transactionStatusData.toString(), TransactionStatusData::class.java)

            return  tranObj?.data
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
            dialog.setContentView(com.adrosonic.craftexchangemarketing.R.layout.dialog_gen_enquiry_holdon)
            dialog.setCanceledOnTouchOutside(false) // disables outside the box touch
            dialog.setCancelable(false) // disables backbtn click when popup visible//
            dialog.create()
            return dialog
        }

        fun enquiryGenSuccessDialog(context : Context, enquiryId : String ,enquiryCode : String) : Dialog {
            var dialog = Dialog(context)
            dialog.setContentView(com.adrosonic.craftexchangemarketing.R.layout.dialog_gen_enquiry_success)

            var id = SpannableString(enquiryCode)
            id.setSpan(ForegroundColorSpan(Color.BLACK), 0, id.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dialog.success_enquiry_id?.append(id)

            dialog.btn_success_cancel?.setOnClickListener {
                dialog.cancel()
            }
            dialog.btn_success_view_enquiry?.setOnClickListener {
                //TODO : View Enquiry details in enquiry landing page
                dialog.cancel()
                messageDialog(context,"Fix in Progress") // TODO : to fix FC issue in view enquiry from popup

//                val intent = Intent(context?.enquiryDetails())
//                var bundle = Bundle()
//                Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId) //TODO change later
////                Prefs.putLong(ConstantsDirectory.ENQUIRY_STATUS_FLAG,2L)
//                bundle.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId)
//                bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG,"2")
////                bundle.putString(ConstantsDirectory.ENQUIRY_CODE,enquiry?.enquiryCode)
//                intent.putExtras(bundle)
//                context?.startActivity(intent)
            }
            dialog.setCanceledOnTouchOutside(false)

            return dialog
        }

        fun enquiryGenExistingDialog(context : Context, enquiryId : String ,enquiryCode: String, productName : String) : Dialog {
            var dialog = Dialog(context)
            dialog.setContentView(com.adrosonic.craftexchangemarketing.R.layout.dialog_gen_enquiry_update_or_new)

            var id = SpannableString(enquiryCode)
            id.setSpan(ForegroundColorSpan(Color.BLACK), 0, id.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            dialog.existing_enquiry_id?.append(id)

            dialog.existing_product_title?.text = productName

            dialog.existing_btn_cancel?.setOnClickListener {
                dialog.cancel()
            }
            dialog.existing_btn_view_enquiry?.setOnClickListener {
                dialog.cancel()
                messageDialog(context,"Fix in Progress") // TODO : to fix FC issue in view enquiry from popup
//                val intent = Intent(context?.enquiryDetails())
//                var bundle = Bundle()
//                Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId) //TODO change later
////                Prefs.putLong(ConstantsDirectory.ENQUIRY_STATUS_FLAG,2L)
//                bundle.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId)
//                bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG,"2")
//
////                bundle.putString(ConstantsDirectory.ENQUIRY_CODE,enquiry?.enquiryCode)
//                intent.putExtras(bundle)
//                context?.startActivity(intent)
            }

            dialog.setCanceledOnTouchOutside(false)


            return dialog
        }

        fun loadingDialog(context: Context) : Dialog {
            var dialog = Dialog(context)
            dialog.setContentView(com.adrosonic.craftexchangemarketing.R.layout.dialog_loading)
            dialog.setCanceledOnTouchOutside(false) // disables outside the box touch
            dialog.setCancelable(false) // disables backbtn click when popup visible//
            dialog.create()
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
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}User/${userId}/CompanyDetails/Logo/${imagename}"
        }

        fun getProfilePhotoUrl(artisanId : Long?, imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}User/${artisanId}/ProfilePics/${imagename}"
        }

        fun getProductsImagesUrl(productId : Long?,imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}Product/${productId}/${imagename}"
        }
        fun getCustomProductImagesUrl(productId : Long?,imagename : String?) : String{
            return "${ConstantsDirectory.IMAGE_LOAD_BASE_URL_DEV}CustomProduct/${productId}/${imagename}"
        }
        fun setImageResource(context: Context?,imageView:ImageView?,imageId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView?.setImageDrawable(context?.resources?.getDrawable(imageId,
                    context.theme
                ))
            } else {
                imageView?.setImageDrawable(context?.getDrawable(imageId))
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
        fun getEscalationData() : EscalationCategoryResponse? {
            val gson = GsonBuilder().create()
            var escObj = gson.fromJson(UserConfig.shared.escalationData.toString(), EscalationCategoryResponse::class.java)
            return  escObj
        }
        fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ConstantsDirectory.PERMISSION_REQUEST_CODE)
        }
        fun returnDisplayDate(date:String):String{
            try {
                var dt = date.substring(0, 10)//2020-08-07T10:25:02.000+0000
                return dt
            }catch (e:Exception){
                return ""
            }
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
        fun resetYarnData() {
            UserConfig.shared.warpDyeId=0
            UserConfig.shared.warpYarnCount=""
            UserConfig.shared.warpYarnId=0
            UserConfig.shared.weftDyeId=0
            UserConfig.shared.weftYarnCount=""
            UserConfig.shared.weftYarnId=0
            UserConfig.shared.extraWeftDyeId=0
            UserConfig.shared.extraWeftYarnCount=""
            UserConfig.shared.extraWeftYarnId=0
        }

        fun getEnquiryStagesData(): ArrayList<Pair<Long,String>>{
            val gson = GsonBuilder().create()
            var enqobj = gson.fromJson(UserConfig.shared.enquiryStageData.toString(), EnquiryStageData::class.java)
            var itr = enqobj?.data?.iterator()
            var list = ArrayList<Pair<Long,String>>()
            list.clear()
            if(itr!=null){
                while(itr.hasNext()){
                    var enq = itr.next()
                    list.add(Pair(enq.id,enq.desc))
                }
            }
            return list
        }

        fun getAvaiProdEnquiryStagesData(): ArrayList<Triple<Long,Long,String>>{
            val gson = GsonBuilder().create()
            var enqobj = gson.fromJson(UserConfig.shared.enquiryAvaProdStageData.toString(), EnquiryAvaProdStageData::class.java)
            var itr = enqobj?.data?.iterator()
            var list = ArrayList<Triple<Long,Long,String>>()
            list.clear()
            if(itr!=null){
                while(itr.hasNext()){
                    var enq = itr.next()
                    list.add(Triple(enq?.num,enq.orderStages?.id,enq.orderStages?.desc))
                }
            }
            return list
        }

        fun getWeaveType() : ArrayList<Pair<Long,String>>{
            val gson = GsonBuilder().create()
            var prodObj = gson.fromJson(UserConfig.shared.productUploadJson.toString(), ProductUploadData::class.java)
            var itr = prodObj?.data?.weaves?.iterator()
            var list = ArrayList<Pair<Long,String>>()
            list.clear()
            if(itr!=null){
                while(itr.hasNext()){
                    var enq = itr.next()
                    list.add(Pair(enq.id,enq.weaveDesc))
                }
            }
            return list
        }

        fun getProductCategory() : ArrayList<Pair<Long,String>>{
            val gson = GsonBuilder().create()
            var prodObj = gson.fromJson(UserConfig.shared.productUploadJson.toString(), ProductUploadData::class.java)
            var itr = prodObj?.data?.productCategories?.iterator()
            var list = ArrayList<Pair<Long,String>>()
            list.clear()
            if(itr!=null){
                while(itr.hasNext()){
                    var enq = itr.next()
                    list.add(Pair(enq.id,enq.productDesc))
                }
            }
            return list
        }
        fun getFileType(filename: String): String? {
            var type: String? = ""
            val extension = MimeTypeMap.getFileExtensionFromUrl(filename)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
            } else type = ""
            Log.e("getFileType", "type:   ${type}")
            return type
        }
        fun getRealPathFromFileUriForChat(context: Context, contentUri: Uri): String {
            var filePath = ""
            val myDir: File
            try {
                if (!File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA).exists()) File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA).mkdir()
                if (contentUri.path!!.contains(".")) {
                    if(contentUri.lastPathSegment!!.contains("/")) {
                        myDir = File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA + "/" + contentUri.lastPathSegment!!.substring(contentUri.lastPathSegment!!.lastIndexOf("/")+1))
                    } else myDir = File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA + "/" + contentUri.lastPathSegment + "")
                }
                else myDir = File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA + "/" + contentUri.lastPathSegment + "")
                Log.e("FileName","1111 ${myDir.name} : ${myDir.name.length}")
                if(myDir.name!!.length>42){
                    val renamed = File(context.cacheDir, BROWSING_IMGS + "/" + System.currentTimeMillis()+ "")
                    Log.e("FileName","33333  ${renamed.name}")
                    myDir.renameTo( renamed)
                }
                Log.e("FileName","4444  ${myDir.name}")
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

        fun openChatMedia(context: Activity,enquiryId:Long,imageName:String){
//            File(context.cacheDir,  Utility.CHAT_MEDIA).exists()) File(context.cacheDir, Utility.CHAT_MEDIA).mkdir()
            val cacheFile = File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA +imageName)
            try {
                val uri = FileProvider.getUriForFile(context, "com.adrosonic.craftexchange.provider", cacheFile)
                val myIntent = Intent(Intent.ACTION_VIEW)
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.getPackageName())
                val componentName = context.componentName
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName)
                myIntent.setDataAndType(uri, getFileType(imageName))
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(myIntent, "Open with"))

            } catch (e: Exception) {
                displayMessage("  Application not installed " + e, context)
            }
        }


        fun writeResponseBodyToDisk(
            body: ResponseBody,
            enquiryId: String,
            isOld:String,
            context: Context
        ): Boolean {
            try {
                if (!File(context.cacheDir, ConstantsDirectory.PI_PDF_PATH).exists()) File(context.cacheDir,ConstantsDirectory.PI_PDF_PATH).mkdir()
//                if (!File(context.cacheDir, Utility.PI_PDF_PATH+ enquiryId).exists()) File(context.cacheDir,Utility.MANAGED_DOC_PATH+ enquiryId).mkdir()
                val myDir = File(context.cacheDir, "/"+ConstantsDirectory.PI_PDF_PATH + "Pi${enquiryId}.pdf")
                var inputStream: InputStream = body.byteStream()
                var outputStream: OutputStream = FileOutputStream(myDir)
                try {
                    var fileReader = ByteArray(4096)
                    var fileSize = body.contentLength()
                    var fileSizeDownloaded = 0
                    while (true) {
                        var read = inputStream.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read
                    }
                    outputStream.flush()
                    return true
                } catch (e: IOException) {
                    Log.e("SavePiPdf", "Exception : "+e.printStackTrace())
                    return false
                } finally {
                    if (inputStream != null) {
                        inputStream.close()
                    }
                    if (outputStream != null) {
                        outputStream.close()
                    }
                }
            } catch (e: Exception) {
                Log.e("SavePiPdf", "Exception : "+e.printStackTrace())
                return false
            }
        }
        fun getDateDiffInDays(orderCreatedOn:String?):Long?{
            try {
                val currentDateTime=System.currentTimeMillis()
//            val orderCreatedOn=Utility.returnDisplayDate(orderDetails?.orderCreatedOn?:"")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val dateString =dateFormat.parse(orderCreatedOn)
                Log.e("RaiseCr","orderDateMillis $dateString")

                val diff: Long = currentDateTime - dateString.getTime()
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                return days
            } catch (e: Exception) {
                return 0
            }
        }

        fun openFile(context: Activity,enquiryId:Long,old:String){
            val cacheFile = File(context.cacheDir, ConstantsDirectory.PI_PDF_PATH +old+"Pi${enquiryId}.pdf")
            try {
                val uri = FileProvider.getUriForFile(context, "com.adrosonic.craftexchange.provider", cacheFile)
                val myIntent = Intent(Intent.ACTION_VIEW)
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, context.getPackageName())
                val componentName = context.componentName
                myIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, componentName)
                myIntent.setDataAndType(uri, "application/pdf")
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(myIntent, "Open with"))

            } catch (e: Exception) {
                displayMessage("  Application not installed " + e, context)
            }
        }
    }
}