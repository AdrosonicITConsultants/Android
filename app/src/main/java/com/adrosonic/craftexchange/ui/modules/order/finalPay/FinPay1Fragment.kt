package com.adrosonic.craftexchange.ui.modules.order.finalPay

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.databinding.FragmentFinPay1Binding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.PaymentStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.response.taxInv.FinPayData
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FinPay1Fragment : Fragment(),TransactionViewModel.FinalPayDetailsInterface,
    TransactionViewModel.UploadPaymentInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentFinPay1Binding?= null

    var enqID : String?= ""
    val mOrdVM : OrdersViewModel by viewModels()
    val mTranVM : TransactionViewModel by viewModels()

    private var url : String?=""
    private var orderDetails : Orders?=null
    private var payDetails : FinPayData?= null

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""

    var userID : Long ?= 0
    var filename : String?= ""
    var absolutePath : String?= ""

    private var dialog : Dialog?= null

    private lateinit var slideDown: Animation
    private lateinit var slideUp: Animation

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fin_pay1, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
//        if(param2!=null){
//            piID = if(param2!!.isNotEmpty())param2 else "0"
//        }
        dialog = Utility.loadingDialog(requireActivity())
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTranVM.finalPayDetailsListener =this
        mTranVM.uploadPaymentListener = this

        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it,0) }

        if(orderDetails != null){
            setDetails()
        }

        if(Utility.checkIfInternetConnected(requireContext())){
            enqID?.toLong()?.let { mTranVM?.getFinalPaymentDetails(it) }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
        }

        slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

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

            if(payDetails!=null){
                var amount =

                if(Utility.checkIfInternetConnected(requireActivity())){
                    if(absolutePath != ""){
                        var paymentObj = enqID?.toLong()?.let { it1 ->
                            payDetails?.payableAmount?.let { it2 ->
                                payDetails?.pid?.let { it3 ->
                                    payDetails?.totalAmount?.let { it4 ->
                                        BuyerPayment(
                                            it1,
                                            payDetails?.invoiceId,
                                            it2,
                                            null,
                                            it3,
                                            it4,
                                            PaymentStatus.FINAL.getId())
                                    }
                                }
                            }
                        }

                        absolutePath?.let { it1 -> paymentObj?.let { it2 -> mTranVM?.uploadPaymentReceipt(it2, it1) } }
                        dialog?.show()
                    }else{
                        Utility?.displayMessage(getString(R.string.upld_trans_recipt),requireActivity())
                    }
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
                }
            }else{
                Utility.displayMessage(getString(R.string.unable_fetch_payment),requireContext())
            }
        }
    }

    fun setDetails(){
        mBinding?.orderCode?.text = orderDetails?.orderCode ?: "N.A"
        setProductImage()
        setProductName()
        setProductAvailability()
        setAccountDetails()
    }

    fun setProductImage(){
        val image = orderDetails?.productImages?.split((",").toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
        } else {
            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
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

    fun setProductName(){
        //Product name or Product cloth details
        if (orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
        } else {
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if (it.first == orderDetails?.weftYarnId) {
                    weft = it.second
                }
                if (it.first == orderDetails?.warpYarnId) {
                    warp = it.second
                }
                if (it.first == orderDetails?.extraWeftYarnId) {
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if (it.first == orderDetails?.productCategoryId) {
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft}"
            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }
                ?.let {
                    ForegroundColorSpan(
                        it
                    )
                }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)
        }
    }

    fun setProductAvailability(){
        //ProductAvailability
        when (orderDetails?.productStatusId) {
            AvailableStatus.IN_STOCK.getId() -> {
                status = context?.getString(R.string.in_stock)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            AvailableStatus.MADE_TO_ORDER.getId() -> {
                status = context?.getString(R.string.made_to_order)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            else -> {
                status = "Custom Design by you"
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
        }
    }

    fun setAccountDetails(){

        mBinding?.artisanBrand?.text = orderDetails?.brandName ?: " - "
        mBinding?.artisanName?.text = "${orderDetails?.firstName} ${orderDetails?.lastName ?: ""}"

        userID = orderDetails?.userId
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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

    override fun onFPDFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("FinalPayDetails", "OnFailure")
//                dialog?.cancel()
                if(Utility.checkIfInternetConnected(requireContext())){
                    enqID?.toLong()?.let { mTranVM?.getFinalPaymentDetails(it) }
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                }
            })

        } catch (e: Exception) {
            Log.e("FinalPayDetails", "Exception onFailure " + e.message)
        }
    }

    override fun onFPDSuccess(details: FinPayData) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("FinalPayDetails", "OnSuccess")
//                dialog?.cancel()
                payDetails = details
                mBinding?.finalAmount?.text = "₹ ${payDetails?.payableAmount ?: 0}"
            }
            )
        } catch (e: Exception) {
            Log.e("FinalPayDetails", "Exception OnSuccess " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("AdvancePAyUpload", "OnSuccess")
                dialog?.cancel()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.order_payment_container,
                        FinPay2Fragment.newInstance(enqID.toString(),payDetails?.payableAmount.toString()))
                    ?.commit()
            }
            )
        } catch (e: Exception) {
            Log.e("FinalPAyUpload", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("FinalPAyUpload", "OnFailure")
                dialog?.cancel()
                Utility.messageDialog(requireActivity(),"Failed to Upload receipt! Please try again.")
            }
            )
        } catch (e: Exception) {
            Log.e("FinalPAyUpload", "Exception onFailure " + e.message)
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            FinPay1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}