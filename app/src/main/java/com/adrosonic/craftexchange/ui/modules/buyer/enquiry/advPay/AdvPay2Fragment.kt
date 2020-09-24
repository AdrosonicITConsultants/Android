package com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.EnquiryPaymentDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.databinding.FragmentAdvPay1Binding
import com.adrosonic.craftexchange.databinding.FragmentAdvPay2Binding
import com.adrosonic.craftexchange.enums.PaymentStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.ui.modules.authentication.reset.ResetPasswordFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.bumptech.glide.Glide
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"


class AdvPay2Fragment : Fragment(),
EnquiryViewModel.UploadPaymentInterface{

    private var param1: String? = null
    private var param2: Float?= 0F
    private var param3: String?= null
    private var param4: String?= null

    var piID : Long ?= 0
    var enqID : Long?= 0
    var calculatedAmount : Long?= 0
    var percentSelected : Long?= 0

    private var mBinding: FragmentAdvPay2Binding?= null

    val mEnqVM : EnquiryViewModel by viewModels()

    private var enquiryDetails : OngoingEnquiries?= null
    private var url : String?=""
    private lateinit var slideDown: Animation
    private lateinit var slideUp: Animation
    var userID : Long ?= 0

    var filename : String?= ""
    var absolutePath : String?= ""

    private var dialog : Dialog?= null

    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getFloat(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
            param4 = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_adv_pay2, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty()) param1!!.toLong() else 0
        }
        if(param2!=null){
            calculatedAmount = if(param2 != 0F)param2!!.toLong() else 0
        }
        if(param3!=null){
            percentSelected = if(param3!!.isNotEmpty())param3!!.toLong() else 30
        }
        if(param4!=null){
            piID = if(param4!!.isNotEmpty())param4!!.toLong() else 0
        }
        dialog = Utility.loadingDialog(requireActivity())

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM?.uploadPaymentListener = this

        enquiryDetails = enqID?.toLong()?.let { mEnqVM?.getSingleOnEnqData(it) }?.value
//        var piDetails = PiPredicates?.getSinglePi(enqID?.toLong())

        slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        if(enquiryDetails != null){
            setDetails()
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if (mBinding?.paymentDetailsLayout?.visibility == View.GONE) {
                mBinding?.paymentDetailsLayout?.animation = slideDown
                mBinding?.paymentDetailsLayout?.visibility = View.VISIBLE

            } else {
                mBinding?.paymentDetailsLayout?.animation = slideUp
                mBinding?.paymentDetailsLayout?.visibility = View.GONE
            }
        }

        mBinding?.uploadPhoto?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                if(Utility.checkPermission(requireActivity())){
                    selectFromGallery()
                }else{
                    requestPermission()
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }

        mBinding?.btnUploadSend?.setOnClickListener {

            var amount = enquiryDetails?.totalAmount?.toFloat()?.toLong()

            if(Utility.checkIfInternetConnected(requireActivity())){
                if(absolutePath != ""){
                    var paymentObj = enqID?.let { it1 ->
                        calculatedAmount?.let { it2 ->
                            percentSelected?.let { it3 ->
                                piID?.let { it4 ->
                                    amount?.let { it5 ->
                                        BuyerPayment(
                                            it1,
                                            it2,
                                            it3,
                                            it4,
                                            it5,
                                            PaymentStatus.ADVANCE.getId())
                                    }
                                }
                            }
                        }
                    }

                    absolutePath?.let { it1 -> paymentObj?.let { it2 -> mEnqVM?.uploadPaymentReceipt(it2, it1) } }
                    dialog?.show()
                }else{
                    Utility?.displayMessage("Upload Transaction Receipt",requireActivity())
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }

        }

    }

    fun setDetails(){
        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode ?: "N.A"
        mBinding?.calculatedAmount?.text = "₹ $calculatedAmount"
        setProductImage()
        setAccountDetails()
    }

    fun setProductImage(){
        val image = enquiryDetails?.productImages?.split((",").toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        if (enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
            url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
        } else {
            url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
        }
        mBinding?.productImage?.let {
            ImageSetter.setImage(
                requireActivity(),
                url!!,
                it,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder
            )
        }
    }

    fun setAccountDetails(){

        mBinding?.artisanBrand?.text = enquiryDetails?.ProductBrandName ?: " - "
        mBinding?.artisanName?.text = "${enquiryDetails?.firstName} ${enquiryDetails?.lastName ?: ""}"

        userID = enquiryDetails?.userId
        var bank =  EnquiryPredicates.getEnqPaymentDetails(userID.toString(),1)

        mBinding?.branchDetails?.text = "${bank?.bankName}, ${bank?.branch}"
        mBinding?.accountNumber?.text = bank?.accountDesc ?: "N.A"
        mBinding?.ifscCode?.text = bank?.ifsc ?: "N.A"

        var gpay = EnquiryPredicates.getEnqPaymentDetails(userID.toString(),2)
        var phonepe = EnquiryPredicates.getEnqPaymentDetails(userID.toString(),3)
        var paytm = EnquiryPredicates.getEnqPaymentDetails(userID.toString(),4)

        mBinding?.gpay?.text = gpay?.accNoUPIMobile ?: "N.A"
        mBinding?.phonepe?.text = phonepe?.accNoUPIMobile ?: "N.A"
        mBinding?.paytm?.text = paytm?.accNoUPIMobile ?: "N.A"
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
                    val uri = data?.data
                    absolutePath = uri?.let { Utility.getRealPathFromFileURI(requireContext(), it) }

                    if(Utility.validFileSize(absolutePath!!)){
                        uri?.let {
                            mBinding?.uploadPhoto?.let { it1 ->
                                ImageSetter.setCircleImage(requireActivity(),
                                    it, it1
                                )
                            }
                        }

                        filename = absolutePath?.lastIndexOf("/")?.plus(1)?.let {
                            absolutePath?.substring(
                                it
                            )
                        }
                        mBinding?.imgName?.text = filename

                    }else{
                        Utility.messageDialog(requireActivity(), requireActivity().getString(R.string.file_size_exceeded))
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



    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("AdvancePAyUpload", "OnSuccess")
                dialog?.cancel()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_payment_container,
                        AdvPay3Fragment.newInstance(enqID.toString(),calculatedAmount.toString()))
                    ?.commit()
            }
            )
        } catch (e: Exception) {
            Log.e("AdvancePAyUpload", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("AdvancePAyUpload", "OnFailure")
                dialog?.cancel()
                Utility.messageDialog(requireActivity(),"Failed to Upload receipt! Please try again.")
            }
            )
        } catch (e: Exception) {
            Log.e("AdvancePAyUpload", "Exception onFailure " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String,param2: Float,param3:String,param4:String) =
            AdvPay2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putFloat(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putString(ARG_PARAM4, param4)

                }
            }
    }
}