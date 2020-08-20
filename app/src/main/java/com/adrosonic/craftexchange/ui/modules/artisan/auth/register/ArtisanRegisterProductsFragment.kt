package com.adrosonic.craftexchange.ui.modules.artisan.auth.register

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.artisan.Address
import com.adrosonic.craftexchange.repository.data.request.artisan.Country
import com.adrosonic.craftexchange.repository.data.request.artisan.User
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback

class ArtisanRegisterProductsFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterProductsFragment()
        const val TAG = "ArtisanRegProducts"
    }

    private var mBinding: FragmentArtisanRegisterProductsBinding ?= null
    private var file : File?= null
    private var profileBody : MultipartBody?= null
    private var fileReqBody : RequestBody?= null
    private var headerBoundary : String ?=""
    private var registerCall : Call<RegisterResponse> ?= null
    var productArray = ArrayList<String>()
    var listProducts = ArrayList<Long>()
    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_products, container, false)

        mBinding?.sendOtpLoader = false

        productArray.add("Saree")
        productArray.add("Dupatta")
        productArray.add("Stole")
        productArray.add("Fabric")
        productArray.add("Home Accessories")
        productArray.add("Fashion Accessories")
        mBinding?.listProducts?.setItems(productArray)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val clickSpan = SpannableString("terms & Condition")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(activity,"Terms n Conditions", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        clickSpan.setSpan(clickableSpan, 0, clickSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding?.textTnct?.append(clickSpan)
        mBinding?.textTnct?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.textTnct?.highlightColor = Color.TRANSPARENT

        var country = Country(1,"INDIA")
        var addr = Address(country,Prefs.getString(ConstantsDirectory.DISTRICT, ""),
            Prefs.getString(ConstantsDirectory.ADDR_LINE1, ""),
            Prefs.getString(ConstantsDirectory.PINCODE, ""),
            Prefs.getString(ConstantsDirectory.STATE, ""))

        mBinding?.uploadProduct?.setOnClickListener{
            if(Utility.checkPermission(requireContext())){
                selectFromGallery()
            }else{
                requestPermission()
            }
        }

        mBinding?.buttonComplete?.setOnClickListener {
            if(mBinding?.checkBoxTnc?.isChecked == true){

                showProgress()

                listProducts = mBinding?.listProducts?.selectedIndicies!!
                var registerRequestObj = User(addr,Prefs.getString(ConstantsDirectory.CLUSTER_ID,"1").toLong(),
                    Prefs.getString(ConstantsDirectory.USER_EMAIL,""),Prefs.getString(ConstantsDirectory.FIRST_NAME,""),
                    Prefs.getString(ConstantsDirectory.LAST_NAME,""),Prefs.getString(ConstantsDirectory.MOBILE,""),
                    Prefs.getString(ConstantsDirectory.PAN,""),Prefs.getString(ConstantsDirectory.USER_PWD,""),
                    listProducts,Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,1),Prefs.getString(ConstantsDirectory.ARTISAN_ID,""))

                var registerRequest = Gson().toJson(registerRequestObj)

                var filePath = Prefs.getString(ConstantsDirectory.PROFILE_PHOTO,"")
                if(filePath.isNotEmpty()){
                    file = File(filePath)
                    fileReqBody = file!!.toRequestBody(MediaType.parse("image/*"))
                    profileBody = MultipartBody.Builder()
                        .addFormDataPart("profilePic", file?.name, fileReqBody!!)
//                        .addFormDataPart("brandLogo",file?.name,fileReqBody!!)
                        .build()
                    headerBoundary="multipart/form-data;boundary="+ profileBody?.boundary

                    registerCall = CraftExchangeRepository
                        .getRegisterService()
                        .registerUserPhoto(headerBoundary!!,registerRequest, profileBody!!)
                }else{
                    registerCall = CraftExchangeRepository
                        .getRegisterService()
                        .registerUser(registerRequest)
                }

//                if(filePath.isNotEmpty()){
//                    file = File(filePath)
//                    fileReqBody = RequestBody.create(MediaType.parse("image/*"), file!!)
//                    profileBody = MultipartBody.Builder()
//                        .addFormDataPart("profilePic", file?.name, fileReqBody!!)
////                        .addFormDataPart("brandLogo",file?.name,fileReqBody!!)
//                        .build()
//                    headerBoundary="multipart/form-data;boundary="+ profileBody?.boundary
//                }else{
//                    fileReqBody = RequestBody.create(MediaType.parse("text/plain"), "")
//                    profileBody = MultipartBody.Builder()
//                        .addFormDataPart("profilePic","",fileReqBody!!)
//                        .build()
//                    headerBoundary="multipart/form-data;boundary="+ profileBody?.boundary
//                }
                registerCall?.enqueue(object: Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            hideProgress()
                            t.printStackTrace()
                        }
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: retrofit2.Response<RegisterResponse>) {
                            if(response.body()?.valid == true){
                                hideProgress()
                                Log.e(TAG, response.toString())
                                Toast.makeText(activity,activity?.getString(R.string.registration_success_msg),Toast.LENGTH_SHORT).show()
                                Prefs.clear()
                                Prefs.putString(ConstantsDirectory.PROFILE,"Artisan")
                                Prefs.putLong(ConstantsDirectory.REF_ROLE_ID,1)
                                Utility.deleteCache(requireContext())
                                startActivity(Intent(activity, LoginActivity::class.java).addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                ))}else{
//                                if(response.code() == 400){
                                hideProgress()
                                    var jsonObject:JSONObject ?= null
                                    try
                                    {
                                        jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
                                        val errorMessage = jsonObject.getString("message")
                                        Toast.makeText(activity,errorMessage,Toast.LENGTH_SHORT).show()
                                    }
                                    catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                            }
                        }
                    })
            }else{
                Toast.makeText(activity,"Read TnC",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf<String>("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data)
            when (requestCode) {
                PICK_IMAGE -> {
                    val uri = data.data
                    var absolutePath = Utility.getRealPathFromFileURI(requireContext(),uri!!)

                    if(Utility.validFileSize(absolutePath)){
                        Glide.with(this)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.upload_icon_artist)
                            .into(mBinding!!.uploadProduct)
                        val filename = absolutePath.substring(absolutePath.lastIndexOf("/") + 1)
                        mBinding?.imgName?.text = filename
                        Prefs.putString(ConstantsDirectory.PROFILE_PHOTO,absolutePath)
                        Prefs.putString(ConstantsDirectory.PROFILE_PHOTO_NAME,filename)
                    }else{
                        Utility.messageDialog(requireContext(), requireActivity().getString(R.string.file_size_exceeded))
                    }



                }
            }
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults:IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty())
            {
                val storageReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (storageReadAccepted){
                    selectFromGallery()
                }
                else
                {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        showMessageOKCancel("You need to allow access to both the permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                                selectFromGallery()
                            })
                        return
                    }
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showProgress(){
        mBinding?.sendOtpLoader = true
        mBinding?.listProducts?.isFocusableInTouchMode = false
        mBinding?.uploadProduct?.isFocusableInTouchMode = false
        mBinding?.buttonComplete?.isClickable = false
        mBinding?.checkBoxTnc?.isClickable = false
        mBinding?.textTnct?.isClickable = false
    }
    private fun hideProgress(){
        mBinding?.sendOtpLoader = false
        mBinding?.listProducts?.isFocusableInTouchMode = true
        mBinding?.uploadProduct?.isFocusableInTouchMode = true
        mBinding?.buttonComplete?.isClickable = true
        mBinding?.checkBoxTnc?.isClickable = true
        mBinding?.textTnct?.isClickable = true
    }

}
