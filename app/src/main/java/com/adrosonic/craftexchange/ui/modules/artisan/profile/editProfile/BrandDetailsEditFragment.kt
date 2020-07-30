package com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandDetailsEditBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.CompanyDetails
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanBrand
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditDetailsResponse
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandDetailsEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200

    private var mBinding: FragmentBrandDetailsEditBinding ?= null
    var productArray = ArrayList<String>()
    var listProducts = ArrayList<Long>()
    private var filePath : String?=""
    private var file : File?= null
    private var brandLogoBody : MultipartBody?= null
    private var fileReqBody : RequestBody?= null
    private var headerBoundary : String ?=""
    private var editBrandCall : Call<EditDetailsResponse>?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_details_edit, container, false)

        productArray.add("Saree")
        productArray.add("Dupatta")
        productArray.add("Stole")
        productArray.add("Fabric")
        productArray.add("Home Accessories")
        productArray.add("Fashion Accessories")
        mBinding?.prodCategory?.setItems(productArray)

        mBinding?.cluster?.text = craftUser?.clusterdesc ?: "-"
        mBinding?.name?.setText(craftUser?.companyName ?: "")
        mBinding?.description?.setText(craftUser?.companyDesc ?: "")
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var brandLogo = Utility.craftUser?.brandLogo
        var urlBrand = Utility?.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),brandLogo)
        mBinding?.changeLogoImg?.let {
            ImageSetter.setImage(requireContext(),urlBrand, it,
                R.drawable.buyer_logo_placeholder,R.drawable.buyer_logo_placeholder,R.drawable.buyer_logo_placeholder)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.changeLogoText?.setOnClickListener {
            if(Utility.checkPermission(requireContext())){
                selectFromGallery()
            }else{
                requestPermission()
            }
        }

        mBinding?.removeLogoText?.setOnClickListener {
            Utility.messageDialog(requireContext(),"Feature To Be Implemented")
        }

        mBinding?.btnSave?.setOnClickListener {
//            Toast.makeText(requireContext(),"Save Brand Details Feature to be implemented", Toast.LENGTH_LONG).show()
            listProducts = mBinding?.prodCategory?.selectedIndicies!!
            var brandDetails = CompanyDetails(mBinding?.name?.text.toString(),mBinding?.description?.text.toString())
            var editBrandDetailsObj = EditArtisanBrand(brandDetails,listProducts)

            var editBrandDetails = Gson().toJson(editBrandDetailsObj)

            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

            if(filePath != "" ){
                file = File(filePath)
                fileReqBody = RequestBody.create(MediaType.parse("image/*"), file!!)
                brandLogoBody = MultipartBody.Builder()
                    .addFormDataPart("logo",file?.name,fileReqBody!!)
                    .build()
                headerBoundary="multipart/form-data;boundary="+ brandLogoBody?.boundary

                editBrandCall = CraftExchangeRepository.getUserService().editArtisanBrandDetailsPhoto(headerBoundary!!,token,editBrandDetails,brandLogoBody!!)
            }else{
                //TODO implement later
//                fileReqBody = RequestBody.create(MediaType.parse("text/plain"), "")
//                brandLogoBody = MultipartBody.Builder()
//                    .addFormDataPart("brandLogo","",fileReqBody!!)
//                    .build()
//                headerBoundary="multipart/form-data;boundary="+ brandLogoBody?.boundary

                editBrandCall = CraftExchangeRepository
                    .getUserService()
                    .editArtisanBrandDetails(token,editBrandDetails)

            }

            editBrandCall?.enqueue(object: Callback, retrofit2.Callback<EditDetailsResponse> {
                    override fun onFailure(call: Call<EditDetailsResponse>, t: Throwable) {
//                        hideProgress()
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<EditDetailsResponse>,
                        response: retrofit2.Response<EditDetailsResponse>) {

                        Log.e(TAG,response.body().toString())

//                        successDialog()
                        if(response.body()?.valid == true){
////                        Toast.makeText(requireContext(),response.body()?.data, Toast.LENGTH_SHORT).show()
////                            successDialog()
                            Toast.makeText(requireContext(),R.string.profile_update_success,Toast.LENGTH_SHORT).show()
                            startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//
////                            AddressPredicates.editArtisanAddress(Prefs.getString(ConstantsDirectory.USER_ID,""),addressObj)
                        }else{
//
                            Toast.makeText(requireContext(),response.body()?.errorMessage, Toast.LENGTH_SHORT).show()
////
//////                        var jsonObject: JSONObject?
//////                        try
//////                        {
//////                            jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
//////                            val errorMessage = jsonObject.getString("message")
//////                            Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_SHORT).show()
//////                        }
//////                        catch (e: JSONException) {
//////                            e.printStackTrace()
//////                        }
                        }
                    }

                })


        }
    }

    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf<String>("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data)
            when (requestCode) {
                PICK_IMAGE -> {
                    val uri = data.data
                    var absolutePath = Utility.getRealPathFromFileURI(requireContext(),uri!!)

                    if(Utility.validFileSize(absolutePath)){

                        mBinding?.changeLogoImg?.let {
                            ImageSetter.setImageUri(requireContext(),uri,
                                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
                        }
                        filePath = absolutePath
                    }else{
                        Utility.messageDialog(requireContext(), requireActivity().getString(R.string.file_size_exceeded))
                    }



                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandDetailsEditFragment()
        const val TAG = "BrandEditFragment"
    }
}
