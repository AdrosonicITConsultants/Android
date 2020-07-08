package com.adrosonic.craftexchange.ui.modules.buyer.auth.register

import android.Manifest.permission.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterCompanyBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.utils.Utility.Companion.checkPermission
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import java.io.File

private const val ARG_PARAM1 = "param1"

class BuyerRegisterCompanyFragment : Fragment() {

    companion object {
        fun newInstance() = BuyerRegisterCompanyFragment()
        const val TAG = "BuyerRegisterComp"

    }


    private var mBinding: FragmentBuyerRegisterCompanyBinding ?= null
    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_company, container, false)

        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textCompname?.append(asterik)
        mBinding?.textPan?.append(asterik)

        mBinding?.textBoxCompname?.setText(Prefs.getString(ConstantsDirectory.COMP_NAME,""))
        mBinding?.textBoxCin?.setText(Prefs.getString(ConstantsDirectory.CIN,""))
        mBinding?.textBoxGst?.setText(Prefs.getString(ConstantsDirectory.GST,""))
        mBinding?.textBoxPan?.setText(Prefs.getString(ConstantsDirectory.PAN,""))
        mBinding?.textBoxUpload?.text = Prefs.getString(ConstantsDirectory.BRAND_IMG_NAME,"")
        mBinding?.textBoxPocName?.setText(Prefs.getString(ConstantsDirectory.POC_NAME,""))
        mBinding?.textBoxPocContact?.setText(Prefs.getString(ConstantsDirectory.POC_CONTACT,""))
        mBinding?.textBoxPocEmail?.setText(Prefs.getString(ConstantsDirectory.POC_EMAIL,""))

        return mBinding?.root
    }


    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf<String>("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.textBoxPocEmail?.addTextChangedListener {
            if(mBinding?.textBoxPocEmail?.text?.isNotEmpty()!!) {
                if(mBinding?.textBoxPocEmail?.validEmail() == false){
                    mBinding?.textBoxPocEmail?.validEmail { mBinding?.textBoxPocEmail?.error = it }
                    mBinding?.buttonNext?.isClickable = false
                }else{
                    mBinding?.buttonNext?.isClickable = true
                }
            }
        }

        mBinding?.textBoxPocContact?.addTextChangedListener {
            if(mBinding?.textBoxPocContact?.text?.isNotEmpty()!!) {
                if(mBinding?.textBoxPocContact?.minLength(10) == false){
                    mBinding?.textBoxPocContact?.minLength(10) { mBinding?.textBoxPocContact?.error = activity?.getString(R.string.mobile_no_invalid_text) }
                    mBinding?.buttonNext?.isClickable = false
                }else{
                    mBinding?.buttonNext?.isClickable = true
                }
            }
        }

        mBinding?.textBoxPan?.addTextChangedListener {
            var boolean = Utility.isValidPan(mBinding?.textBoxPan?.text.toString())
            if(mBinding?.textBoxPan?.text?.isNotEmpty()!! ) {
                if(boolean){
                    mBinding?.buttonNext?.isClickable = true
                }else{
                    mBinding?.textBoxPan?.error =activity?.getString(R.string.pan_invalid_text)
                    mBinding?.buttonNext?.isClickable = false
                }
            }
        }

        mBinding?.textBoxGst?.addTextChangedListener {
            var boolean = Utility.isValidGST(mBinding?.textBoxGst?.text.toString())
            if(mBinding?.textBoxGst?.text?.isNotEmpty()!! ) {
                if(boolean){
                    mBinding?.buttonNext?.isClickable = true
                }else{
                    mBinding?.textBoxGst?.error =activity?.getString(R.string.gst_invalid_text)
                    mBinding?.buttonNext?.isClickable = false
                }
            }
        }

        mBinding?.textBoxCin?.addTextChangedListener {
            var boolean = Utility.isValidCIN(mBinding?.textBoxCin?.text.toString())
            if(mBinding?.textBoxCin?.text?.isNotEmpty()!! ) {
                if(boolean){
                    mBinding?.buttonNext?.isClickable = true
                }else{
                    mBinding?.textBoxCin?.error =activity?.getString(R.string.cin_invalid_text)
                    mBinding?.buttonNext?.isClickable = false
                }
            }
        }

        mBinding?.textBoxUpload?.setOnClickListener {
            if(checkPermission(requireContext())){
                selectFromGallery()
            }else{
                requestPermission()
            }
        }

        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxCompname?.nonEmpty() == true &&
                mBinding?.textBoxPan?.nonEmpty() == true) {

                Prefs.putString(ConstantsDirectory.COMP_NAME,mBinding?.textBoxCompname?.text.toString())
                Prefs.putString(ConstantsDirectory.CIN,mBinding?.textBoxCin?.text.toString())
                Prefs.putString(ConstantsDirectory.GST,mBinding?.textBoxGst?.text.toString())
                Prefs.putString(ConstantsDirectory.PAN,mBinding?.textBoxPan?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_NAME,mBinding?.textBoxPocName?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_CONTACT,mBinding?.textBoxPocContact?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_EMAIL,mBinding?.textBoxPocEmail?.text.toString())

                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.register_container,
                            BuyerRegisterAddressFragment.newInstance(),"Register Buyer Address Details")
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                mBinding?.textBoxCompname?.nonEmpty{ mBinding?.textBoxCompname?.error = it }
                mBinding?.textBoxPan?.nonEmpty{ mBinding?.textBoxPan?.error = it }
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
                        val filename = absolutePath.substring(absolutePath.lastIndexOf("/") + 1)
                        Prefs.putString(ConstantsDirectory.BRAND_LOGO,absolutePath)
                        Prefs.putString(ConstantsDirectory.BRAND_IMG_NAME,filename)
                        mBinding?.textBoxUpload?.text = filename
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
                    if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))
                    {
                        showMessageOKCancel("You need to allow access to both the permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                                selectFromGallery()
                            })
                        return
                    }
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

}
