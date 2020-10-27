package com.adrosonic.craftexchange.ui.modules.artisan.order

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentFinalPayReceiptBinding
import com.adrosonic.craftexchange.databinding.FragmentPaymentReceiptBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.EnquiryStages
import com.adrosonic.craftexchange.enums.PaymentActionStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FinalPayReceiptFragment : Fragment(),
    TransactionViewModel.ValidatePaymentInterface,
    TransactionViewModel.PaymentReceiptInterface {
    private var param1: String? = null
    private var param2: String? = null

    var enqID : String?= ""

    private var orderDetails : Orders?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""

    var isAccepted : Boolean ?= false
    var isCompleted : String ?="0"

    private var loadingDialog : Dialog?= null

    private var mBinding: FragmentFinalPayReceiptBinding?= null

    val mOrdVM : OrdersViewModel by viewModels()
    val mTransVM : TransactionViewModel by viewModels()

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_final_pay_receipt, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTransVM.validatePaymentListener = this
        mTransVM.paymentReceiptListener = this

        mBinding?.swipeReceipt?.isEnabled = false

        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it,0) }

        if(orderDetails != null){
            setDetails()
        }

        if(Utility.checkIfInternetConnected(requireActivity())){
            viewLoader()
            orderDetails?.enquiryId?.let { mTransVM.getFinalPaymentReceipt(it) }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
        }

        loadingDialog = Utility.loadingDialog(requireActivity())

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnAccept?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadingDialog?.show()
                orderDetails?.enquiryId?.let { it1 -> mTransVM.validateFinalPayment(it1,
                    PaymentActionStatus.ACCEPT.getId().toString()) }
                isAccepted = true
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }

        mBinding?.btnReject?.setOnClickListener {
            showConfirmDialog()
        }

        mBinding?.paymentImg?.setOnTouchListener(ImageMatrixTouchHandler(requireActivity()))
    }

    fun showConfirmDialog(){
        var confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.dialog_are_you_sure)
        confirmDialog.show()

        val yes = confirmDialog.findViewById(R.id.btn_yes) as Button
        val no = confirmDialog.findViewById(R.id.btn_no) as Button

        no.setOnClickListener{
            confirmDialog.cancel()
        }

        yes.setOnClickListener {
            confirmDialog.cancel()
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadingDialog?.show()
                orderDetails?.enquiryId?.let { it1 -> mTransVM.validateFinalPayment(it1,PaymentActionStatus.REJECT.getId().toString()) }
                isAccepted = false
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }
    }

    fun setDetails(){
        mBinding?.date?.text = "Date Accepted : ${orderDetails?.startedOn?.split("T")?.get(0)}"
        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount ?: 0}"
        mBinding?.buyerCompany?.text = orderDetails?.brandName
        mBinding?.enquiryUpdateDate?.text = "Last updated : ${orderDetails?.lastUpdated?.split("T")?.get(0)}"

        setProductImage()
        setProductName()
        setProductAvailability()
        setEnquiryStage()
        setButtonVisibility()
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

    fun setEnquiryStage(){
        //enquiry stage with color
        var enquiryStage : String ?= ""
        var stagList = Utility?.getEnquiryStagesData()
        Log.e("enqDataStages", "List : $stagList")
        stagList?.forEach {
            if(it.first == orderDetails?.enquiryStageId){
                enquiryStage = it.second
            }
        }
        when(orderDetails?.enquiryStageId){
            1L -> {
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.black_text)
                }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.black_text)
                }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }
            }

            2L,3L,4L,5L -> {
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.tab_details_selected_text)
                }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.tab_details_selected_text)
                }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }
            }

            6L,7L,8L,9L,10L -> {
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.dark_green)
                }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }

            }
        }
        mBinding?.enquiryStatusText?.text = enquiryStage
    }

    fun viewLoader(){
        mBinding?.swipeReceipt?.isRefreshing = true
        mBinding?.middleReceiptLayer?.visibility = View.GONE
        mBinding?.bottomValidationPart?.visibility = View.GONE
    }

    fun hideLoader(){
        mBinding?.swipeReceipt?.isRefreshing = false
        mBinding?.middleReceiptLayer?.visibility = View.VISIBLE
        setButtonVisibility()
    }

    fun setButtonVisibility(){
        if (orderDetails?.enquiryStageId!! >=EnquiryStages.FINAL_PAYMENT_RECEIVED.getId()){
            mBinding?.bottomValidationPart?.visibility = View.GONE
        }else{
            mBinding?.bottomValidationPart?.visibility = View.VISIBLE
        }
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            FinalPayReceiptFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onPaymentFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentValidation", "OnFailure")
                loadingDialog?.cancel()
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentValidation", "Exception onFailure " + e.message)
        }
    }

    override fun onPaymentSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentValidation", "OnSuccess")
                loadingDialog?.cancel()
                if(isAccepted == true){
                    Utility.displayMessage("Payment Accepted",requireActivity())
                }else{
                    Utility.displayMessage("Payment Rejected",requireActivity())
                }
                activity?.onBackPressed()
            })
        } catch (e: Exception) {
            Log.e("PaymentValidation", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentReceipt", "OnFailure")
                hideLoader()
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentReceipt", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess(imgName: String, receiptId : Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PaymentReceipt", "OnSuccess")
                hideLoader()
                var url = Utility.getFinalPaymentImageUrl(receiptId,imgName)
                mBinding?.paymentImg?.let { mBinding?.loader?.let { it1 ->
                    ImageSetter.setFullImage(requireActivity(),url, it, it1)
                } }
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentReceipt", "Exception OnSuccess " + e.message)
        }
    }
}