package com.adrosonic.craftexchange.ui.modules.artisan.enquiry.advPay

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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentCompPaymentReceiptBinding
import com.adrosonic.craftexchange.databinding.FragmentPaymentReceiptBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CompPaymentReceiptFragment : Fragment(),
    TransactionViewModel.PaymentReceiptInterface{
    private var param1: String? = null

    var enqID : String?= ""

    private var enquiryDetails : CompletedEnquiries?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""


    private var mBinding: FragmentCompPaymentReceiptBinding?= null

    val mEnqVM : EnquiryViewModel by viewModels()
    val mTransVM : TransactionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_comp_payment_receipt, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTransVM.paymentReceiptListener = this
        mBinding?.swipeReceipt?.isEnabled = false

        enquiryDetails = enqID?.toLong()?.let { mEnqVM?.getSingleCompEnqData(it) }?.value

        if(enquiryDetails != null){
            setDetails()
        }

        if(Utility.checkIfInternetConnected(requireActivity())){
            viewLoader()
            enquiryDetails?.enquiryID?.let { mTransVM.getAdvancePaymentReceipt(it) }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.paymentImg?.setOnTouchListener(ImageMatrixTouchHandler(requireActivity()))

    }

    fun setDetails(){
        mBinding?.date?.text = "Date Accepted : ${enquiryDetails?.startedOn?.split("T")?.get(0)}"
        mBinding?.productAmount?.text = "₹ ${enquiryDetails?.totalAmount ?: 0}"
        mBinding?.buyerCompany?.text = enquiryDetails?.ProductBrandName
        mBinding?.enquiryUpdateDate?.text = "Last updated : ${enquiryDetails?.lastUpdated?.split("T")?.get(0)}"

        setProductImage()
        setProductName()
        setProductAvailability()
        setEnquiryStage()
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

    fun setProductName(){
        //Product name or Product cloth details
        if (enquiryDetails?.productName != "") {
            mBinding?.productName?.text = enquiryDetails?.productName
        } else {
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if (it.first == enquiryDetails?.weftYarnID) {
                    weft = it.second
                }
                if (it.first == enquiryDetails?.warpYarnID) {
                    warp = it.second
                }
                if (it.first == enquiryDetails?.extraWeftYarnID) {
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if (it.first == enquiryDetails?.productCategoryID) {
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
        when (enquiryDetails?.productStatusID) {
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
            if(it.first == enquiryDetails?.enquiryStageID){
                enquiryStage = it.second
            }
        }
        when(enquiryDetails?.enquiryStageID){
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
    }

    fun hideLoader(){
        mBinding?.swipeReceipt?.isRefreshing = false
        mBinding?.middleReceiptLayer?.visibility = View.VISIBLE
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
                var url = Utility.getAdvancePaymentImageUrl(receiptId,imgName)
//                var url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/AdvancedPayment/159/IMG_860552.png"
                mBinding?.paymentImg?.let { mBinding?.loader?.let { it1 ->
                    ImageSetter.setFullImage(requireActivity(),url, it,
                        it1
                    )
                } }
            }
            )
        } catch (e: Exception) {
            Log.e("PaymentReceipt", "Exception OnSuccess " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String) =
            CompPaymentReceiptFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}