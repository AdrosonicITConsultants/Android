package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandEditBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandEditBinding ?= null
    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_edit, container, false)

        var brandLogo = Utility.craftUser?.brandLogo
        var urlBrand = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),brandLogo)
        mBinding?.changeLogoImg?.let {
            ImageSetter.setImage(requireActivity(),urlBrand, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }

        mBinding?.gst?.setText(craftUser?.gstNo ?: "")
        mBinding?.cin?.setText(craftUser?.cin ?: "")
        mBinding?.pan?.setText(craftUser?.pancard ?: "")

        mBinding?.name?.setText(craftUser?.poc_firstName ?: "")
        mBinding?.mobile?.setText(craftUser?.poc_contactNo ?: "")
        mBinding?.email?.setText(craftUser?.poc_email ?: "")

        Prefs.putString(ConstantsDirectory.GST,craftUser?.gstNo ?: " ")
        Prefs.putString(ConstantsDirectory.CIN,craftUser?.cin ?: " ")
        Prefs.putString(ConstantsDirectory.PAN,craftUser?.pancard ?: " ")
        Prefs.putString(ConstantsDirectory.POC_NAME,craftUser?.poc_firstName ?: " ")
        Prefs.putString(ConstantsDirectory.POC_EMAIL,craftUser?.poc_email ?: " ")
        Prefs.putString(ConstantsDirectory.POC_CONTACT,craftUser?.poc_contactNo ?: " ")


        return mBinding?.root
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

        mBinding?.gst?.addTextChangedListener {
            var boolean = Utility.isValidGST(mBinding?.gst?.text.toString())
            if(mBinding?.gst?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.GST, mBinding?.gst?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.gst?.error =activity?.getString(R.string.gst_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                Prefs.putString(ConstantsDirectory.GST, mBinding?.cin?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.cin?.addTextChangedListener {
            var boolean = Utility.isValidCIN(mBinding?.cin?.text.toString())
            if(mBinding?.cin?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.CIN, mBinding?.cin?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.cin?.error =activity?.getString(R.string.cin_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                Prefs.putString(ConstantsDirectory.CIN, mBinding?.cin?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.pan?.addTextChangedListener {
            var boolean = Utility.isValidPan(mBinding?.pan?.text.toString())
            if(mBinding?.pan?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.PAN, mBinding?.pan?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.pan?.error =activity?.getString(R.string.pan_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                mBinding?.pan?.nonEmpty{
                    mBinding?.pan?.error = it
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)}
            }
        }
        mBinding?.name?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.POC_NAME, mBinding?.name?.text.toString())
        }

        mBinding?.mobile?.addTextChangedListener {
            if(mBinding?.mobile?.text?.isNotEmpty()!!) {
                if(mBinding?.mobile?.minLength(10) == false){
                    mBinding?.mobile?.minLength(10) { mBinding?.mobile?.error = activity?.getString(R.string.mobile_no_invalid_text) }
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }else{
                    Prefs.putString(ConstantsDirectory.POC_CONTACT, mBinding?.mobile?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }
            }else{
                Prefs.putString(ConstantsDirectory.POC_CONTACT, mBinding?.mobile?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.email?.addTextChangedListener {
            if(mBinding?.email?.text?.isNotEmpty()!!) {
                if(mBinding?.email?.validEmail() == false){
                    mBinding?.email?.validEmail { mBinding?.email?.error = it }
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }else{
                    Prefs.putString(ConstantsDirectory.POC_EMAIL, mBinding?.email?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }
            }else{
                Prefs.putString(ConstantsDirectory.POC_EMAIL, mBinding?.email?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

    }

    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                        Prefs.putString(ConstantsDirectory.BRAND_LOGO,absolutePath)
                    }else{
                        Utility.messageDialog(requireContext(), requireActivity().getString(R.string.file_size_exceeded))
                    }

                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandEditFragment()
        const val TAG = "BrandEditFrag"
    }
}
