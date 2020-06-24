package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.Manifest.permission.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterCompanyBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.google.android.material.snackbar.Snackbar
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import java.util.jar.Manifest

private const val ARG_PARAM1 = "param1"

class BuyerRegisterCompanyFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            BuyerRegisterCompanyFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1,param1)
//                }
//            }
        fun newInstance() = BuyerRegisterCompanyFragment()
        const val TAG = "BuyerRegisterComp"
        private val PERMISSION_REQUEST_CODE = 200
    }


    private var mBinding: FragmentBuyerRegisterCompanyBinding ?= null
    private var PICK_IMAGE: Int = 1

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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf<String>("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.textBoxUpload?.setOnClickListener {
            if(checkPermission()){
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
                    val filename = absolutePath.substring(absolutePath.lastIndexOf("/") + 1)

                    Prefs.putString(ConstantsDirectory.BRAND_LOGO,absolutePath)
                    Prefs.putString(ConstantsDirectory.BRAND_IMG_NAME,filename)

                    mBinding?.textBoxUpload?.text = filename
//                    val filePathColumn = MediaStore.Images.Media.DATA
//                    val cursor = activity?.contentResolver?.query(selectedImage!!, arrayOf(filePathColumn),null,null,null)
//                    cursor?.moveToFirst()
//                    val columnIndex = cursor?.getColumnIndex(filePathColumn)
//                    val logoString = columnIndex?.let { cursor.getString(it) }
//                    cursor?.close()

                }
            }
    }

    // Function to check and request permission
    private fun checkPermission():Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
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
    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

}
