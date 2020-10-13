package com.adrosonic.craftexchange.ui.modules.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.TransitionListenerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.databinding.FragmentArtisanOnGoEnqDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentArtisanOngoingOrderDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentBuyerOngoingOrderDetailsBinding
import com.adrosonic.craftexchange.enums.*
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.ui.modules.enquiry.BuyEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.order.cr.crContext
import com.adrosonic.craftexchange.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchange.ui.modules.transaction.adapter.OnGoingTransactionRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.agik.swipe_button.Controller.OnSwipeCompleteListener
import com.agik.swipe_button.View.Swipe_Button_View
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerOngoinOrderDetailsFragment : Fragment(),
    OrdersViewModel.FetchOrderInterface,
    OrdersViewModel.changeStatusInterface,
    OrdersViewModel.FetchCrInterface,
    TransactionViewModel.TransactionInterface{

    private var param1: String? = null
    private var param2: String? = null

    private var enqID : Long ?= 0
    private var enqCode : String ?= ""
    private var orderDetails : Orders ?= null
    private var stageList : ArrayList<Pair<Long,String>> ?= null
    private var stageAPList : ArrayList<Triple<Long,Long,String>> ?= null
    private var innerStageList : ArrayList<Pair<Long,String>> ?= null
    private var nextEnqStage : String?=""
    private var prevEnqStage : String?=""
    private var currEnqStage : String ?= ""
    private var currEnqStageId : Long ?= 0
    private var currEnqStageSerNo : Long ?= 0
    private var url : String?=""
    private var status : String?= ""

    val mOrderVm : OrdersViewModel by viewModels()
    val mTranVM : TransactionViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var isCustom : Boolean ?= false

    var estId=0L
    var mBinding : FragmentBuyerOngoingOrderDetailsBinding?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_ongoing_order_details, container, false)
        enqID = param1?.toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderVm?.fetchEnqListener = this
        mOrderVm?.changeStatusListener = this
        mOrderVm?.fetcCrListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            enqID?.let {
                viewLoader()
                mOrderVm.getSingleOngoingOrder(it)
                mTranVM.getSingleOngoingTransactions(it)
                mOrderVm?.getChangeRequestDetails(it)
            }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
        }

        orderDetails?.let {
            if(Utility.checkIfInternetConnected(requireActivity())) {
                viewLoader()

            }
        }
        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_details_container,BuyEnqDetailsFragment.newInstance(orderDetails?.enquiryId.toString(),orderDetails?.enquiryStatusId.toString()))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
        mBinding?.productDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                isCustom?.let { it1 ->
                    ViewProductDetailsFragment.newInstance(orderDetails?.productId!!.toLong(),
                        it1
                    )
                }?.let { it2 ->
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.enquiry_details_container,
                            it2
                        )
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }

        }

        mBinding?.piDetailsLayer?.setOnClickListener {
            enqID?.let {  startActivity(requireContext().raisePiContext(it,true, SendPiRequest()))
            }
        }

        mBinding?.viewPaymentLayer?.setOnClickListener {
            if(mBinding?.transactionList!!.visibility==View.VISIBLE) mBinding?.transactionList!!.visibility=View.GONE
            else mBinding?.transactionList!!.visibility=View.VISIBLE
        }
        mBinding?.qualityCheckLayer?.setOnClickListener {
            Utility.displayMessage("Coming soon",requireContext())
        }
        mBinding?.changeRequestLayer?.setOnClickListener {
            if(orderDetails?.changeRequestOn==1L) {
                when (orderDetails?.changeRequestStatus) {
                    0L -> {
                        enqID?.let { startActivity(requireActivity().crContext(it, 0L)) }
                    }
                    1L -> {
                    }
                    2L -> {
                    }
                    3L -> {
                    }
                    else -> {
                        enqID?.let { startActivity(requireActivity().crContext(it, 4L)) }
                    }
                }
            } else Utility.displayMessage("Change request disabled by artisan.",requireContext())
        }
        mBinding?.taxInvoiceLayer?.setOnClickListener {
            Utility.displayMessage("Coming soon",requireContext())
        }

    }

    fun setDetails(){
     try {
         Handler(Looper.getMainLooper()).post(Runnable {
             setTabVisibilities()
             mBinding?.orderCode?.text = orderDetails?.orderCode
             mBinding?.enquiryStartDate?.text =
                 "Date accepted : ${orderDetails?.startedOn?.split("T")?.get(0)}"
             val image =
                 orderDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }
                     ?.toTypedArray()?.get(0)

             //brand name of product & product Image
             if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
                 url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
                 isCustom = true
             } else {
                 url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
                 isCustom = false
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

             mBinding?.buyerCompany?.text = orderDetails?.companyName

             //ProductAvailability
             when (orderDetails?.productStatusId) {
                 2L -> {
                     status = context?.getString(R.string.in_stock)
                     mBinding?.productAvailability?.text = status
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.dark_green
                         )
                     }?.let { mBinding?.productAvailability?.setTextColor(it) }
                 }
                 1L -> {
                     status = context?.getString(R.string.made_to_order)
                     mBinding?.productAvailability?.text = status
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.dark_magenta
                         )
                     }?.let { mBinding?.productAvailability?.setTextColor(it) }
                 }
                 else -> {
                     status = context?.getString(R.string.requested_custom_design)
                     mBinding?.productAvailability?.text = status
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.dark_magenta
                         )
                     }?.let { mBinding?.productAvailability?.setTextColor(it) }
                 }
             }
             //Product name or Product cloth details
             if (orderDetails?.productName != "") {
                 mBinding?.productName?.text = orderDetails?.productName
                 mBinding?.productNameDetails?.text = orderDetails?.productName
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
                 fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }?.let {
                     ForegroundColorSpan(
                         it
                     )
                 }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                 mBinding?.productName?.text = fp
                 mBinding?.productName?.append(sp)

                 mBinding?.productNameDetails?.text = "Custom Design Product"
             }

             mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount ?: 0}"

             //enquiry stage with color
             var enquiryStage: String? = ""
             var stagList = Utility?.getEnquiryStagesData()
             Log.e("enqDataStages", "List : $stagList")
             stagList?.forEach {
                 if (it.first == orderDetails?.enquiryStageId) {
                     enquiryStage = it.second
                 }
             }
             when (orderDetails?.enquiryStageId) {
                 1L -> {
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.black_text
                         )
                     }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.black_text
                         )
                     }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }
                 }

                 2L, 3L, 4L, 5L -> {
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.tab_details_selected_text
                         )
                     }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.tab_details_selected_text
                         )
                     }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }
                 }

                 6L, 7L, 8L, 9L, 10L -> {
                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.dark_green
                         )
                     }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                     context?.let {
                         ContextCompat.getColor(
                             it, R.color.dark_green
                         )
                     }?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }

                 }
             }
             mBinding?.enquiryStatusText?.text = enquiryStage

             mBinding?.enquiryUpdateDate?.text =  "Last updated : ${orderDetails?.lastUpdated?.split("T")?.get(0)}"
             mBinding?.artisanBrand?.text = orderDetails?.companyName
             setProgressTimeline()


             var tranList = TransactionPredicates.getTransactionByEnquiryId(enqID ?: 0)
             if (tranList!!.size > 0) {
                 mBinding?.viewTransaction?.text = "View"
                 mBinding?.transactionList?.layoutManager =
                     LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                 val transactionAdapter =
                     OnGoingTransactionRecyclerAdapter(requireContext(), tranList)
                 mBinding?.transactionList?.adapter = transactionAdapter
//                    transactionAdapter.listener = this
             } else {
                 mBinding?.viewTransaction?.text = "No transaction present"
             }

             if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType.equals(ConstantsDirectory.CUSTOM_PRODUCT)){
                 if(orderDetails?.changeRequestOn==1L)
                 {
                     when(orderDetails?.changeRequestStatus) {
                         0L -> {
                             mBinding?.txtCr?.visibility = View.GONE
                             mBinding?.txtCrDate?.text = "Waiting for acknwoledgement"
                         }
                         1L -> {
                             mBinding?.txtCr?.visibility = View.VISIBLE
                             mBinding?.txtCrDate?.text = Utility.returnDisplayDate(orderDetails?.changeRequestModifiedOn ?: "")
                         }
                         2L -> {
                             mBinding?.txtCr?.visibility = View.GONE
                             mBinding?.txtCrDate?.text = ""
                         }
                         3L -> {
                             mBinding?.txtCr?.visibility = View.VISIBLE
                             mBinding?.txtCrDate?.text = Utility.returnDisplayDate(orderDetails?.changeRequestModifiedOn ?: "")
                         }
                         else -> {
//                             val currentDateTime=System.currentTimeMillis()
//                             val orderCreatedOn=Utility.returnDisplayDate(orderDetails?.orderCreatedOn?:"")
//                             val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//                             val dateString =dateFormat.parse(orderCreatedOn)
//                             val orderDateMillis=dateString.time
//                             Log.e("RaiseCr","orderDateMillis $orderDateMillis")
//                             Log.e("RaiseCr","orderDateMillis ${orderDateMillis-orderDateMillis}")
////                             if()
                             mBinding?.txtCr?.visibility = View.GONE
                             mBinding?.txtCrDate?.text = ""
                         }
                     }
                 }
                 else  mBinding?.txtCrDate?.text="Change request disabled by artisan."
             }else {
                 mBinding?.txtCr?.visibility = View.GONE
                 mBinding?.txtCrDate?.text=getString(R.string.cr_not_applicable)
             }
         })
        } catch (e: Exception) {
            Log.e("setDetails", "Exception " + e.message)
        }
    }

    private fun setProgressTimeline(){
        stageAPList?.clear()
        innerStageList?.clear()
        stageList?.clear()
        if(orderDetails?.productType == "Custom Product" || orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId()){
            stageList = Utility.getEnquiryStagesData() // custom product or made to order
            Log.e("enqdata", "List All : $stageList")

            innerStageList = Utility.getInnerEnquiryStagesData()
            Log.e("enqdata", "List Inner : $innerStageList")


            if(orderDetails?.innerEnquiryStageId != null && orderDetails?.enquiryStageId == 5L){
                when(orderDetails?.innerEnquiryStageId){
                    1L -> {
                        innerStageList?.forEach {
                            if(it.first == orderDetails?.innerEnquiryStageId){
                                currEnqStageId = it.first
                                currEnqStage = it.second
                                Log.e("CurrentEnqStage","Id : $currEnqStageId")
                                Log.e("CurrentEnqStage","current : $currEnqStage")
                            }
                        }
                        innerStageList?.forEach {
                            if(it.first == currEnqStageId?.plus(1) ?: 5){
                                nextEnqStage = it.second
                                Log.e("CurrentEnqStage","next : $nextEnqStage")
                            }
                        }

                        stageList?.forEach {
                            if(it.first == 4L){
                                prevEnqStage = it.second
                                Log.e("CurrentEnqStage","previous : $prevEnqStage")
                            }
                        }
                    }
                    5L -> {
                        innerStageList?.forEach {
                            if(it.first == orderDetails?.innerEnquiryStageId){
                                currEnqStageId = it.first
                                currEnqStage = it.second
                                Log.e("CurrentEnqStage","Id : $currEnqStageId")
                                Log.e("CurrentEnqStage","current : $currEnqStage")
                            }
                        }
                        innerStageList?.forEach {
                            if(it.first == currEnqStageId?.minus(1) ?: 5){
                                prevEnqStage = it.second
                                Log.e("CurrentEnqStage","next : $prevEnqStage")
                            }
                        }

                        stageList?.forEach {
                            if(it.first == 6L){
                                nextEnqStage = it.second
                                Log.e("CurrentEnqStage","previous : $nextEnqStage")
                            }
                        }
                    }
                    else -> {
                        innerStageList?.forEach {
                            if(it.first == orderDetails?.innerEnquiryStageId){
                                currEnqStageId = it.first
                                currEnqStage = it.second
                                Log.e("CurrentEnqStage","Id : $currEnqStageId")
                                Log.e("CurrentEnqStage","current : $currEnqStage")
                            }
                        }
                        innerStageList?.forEach {
                            if(it.first == currEnqStageId?.plus(1) ?: 5){
                                nextEnqStage = it.second
                                Log.e("CurrentEnqStage","next : $nextEnqStage")
                            }
                        }

                        innerStageList?.forEach {
                            if(it.first == currEnqStageId?.minus(1) ?: 1){
                                prevEnqStage = it.second
                                Log.e("CurrentEnqStage","previous : $prevEnqStage")
                            }
                        }
                    }
                }
            }else{
                stageList?.forEach {
                    if(it.first == orderDetails?.enquiryStageId){
                        currEnqStageId = it.first
                        currEnqStage = it.second
                        Log.e("CurrentEnqStage","Id : $currEnqStageId")
                        Log.e("CurrentEnqStage","current : $currEnqStage")
                    }
                }

                stageList?.forEach {
                    if(it.first == currEnqStageId?.plus(1) ?: 10){
                        nextEnqStage = it.second
                        Log.e("CurrentEnqStage","next : $nextEnqStage")
                    }
                }

                stageList?.forEach {
                    if(it.first == currEnqStageId?.minus(1) ?: 0){
                        prevEnqStage = it.second
                        Log.e("CurrentEnqStage","previous : $prevEnqStage")
                    }
                }
            }
        }else{
            stageAPList = Utility.getAvaiProdEnquiryStagesData() // available product
            Log.e("enqdata", "List AP : $stageAPList")
            stageAPList?.forEach {
                if(it.second == orderDetails?.enquiryStageId){
                    currEnqStageSerNo = it.first
                    currEnqStageId = it.second
                    currEnqStage = it.third
                    Log.e("CurrentEnqStage","Id : $currEnqStageId")
                    Log.e("CurrentEnqStage","current : $currEnqStage")
                }
            }

            stageAPList?.forEach {
                if(it.first == currEnqStageSerNo?.plus(1) ?: 7){
                    nextEnqStage = it.third
                    Log.e("CurrentEnqStage","next : $nextEnqStage")
                }
            }

            stageAPList?.forEach {
                if(it.first == currEnqStageSerNo?.minus(1) ?: 0){
                    prevEnqStage = it.third
                    Log.e("CurrentEnqStage","previous : $prevEnqStage")
                }
            }
        }

        mBinding?.previousStep?.text = prevEnqStage
        mBinding?.currentStep?.text = currEnqStage
        mBinding?.nextStep?.text = nextEnqStage
        //TODO : To implement pi moq upload
        if(orderDetails?.isBlue == 1L){
            when(currEnqStageId){
                3L,8L -> {
                    mBinding?.awaitingPaymentReceipt?.visibility = View.VISIBLE
                }
                else ->{
                    mBinding?.awaitingPaymentReceipt?.visibility = View.GONE
////                    mBinding?.transactionLayout?.visibility = View.GONE
                }
            }
        }

//        when(currEnqStageId){
//            2L,7L -> mBinding?.uploadDocLayout?.visibility = View.VISIBLE
//            else -> mBinding?.uploadDocLayout?.visibility = View.GONE
//        }

    }

    fun viewLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing = true
    }
    fun hideLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing = false
    }

    private fun setTabVisibilities(){
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            if(orderDetails?.enquiryStageId!! >= 4L){
                mBinding?.viewPaymentLayer?.visibility = View.VISIBLE
            }else{
                mBinding?.viewPaymentLayer?.visibility = View.GONE
            }
        }else{
            mBinding?.viewPaymentLayer?.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        enqID?.let { mOrderVm?.getSingleOnOrderData(it,0) }
        setDetails()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OrderDetails", "onFailure")
                enqID?.let { mOrderVm.getSingleOnOrderData(it,0) }
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OrderDetails", "onSuccess enqID: $enqID")
                enqID?.let { orderDetails=mOrderVm.getSingleOnOrderData(it,0).value }
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onFailure " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String) =
            BuyerOngoinOrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Log.e("PiActivity", "onActivityResult $requestCode")
//        Log.e("PiActivity", "onActivityResult $resultCode")
//        Log.e("PiActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
//        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
//            if (resultCode == Activity.RESULT_OK) {
////                viewLoader()
//                Log.e("PiActivity", "onActivityResult enqID ${enqID}")
//                enqID?.let {
////                    mOrderVm.getSingleOngoingEnquiry(it)
//                    EnquiryPredicates.updatePiStatus(it)
//                    setDetails()
//                }
//            }
//        }
    }

    override fun onStatusChangeSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                enqID?.let { mOrderVm.getSingleOngoingOrder(it) }
                Utility.displayMessage("Order Stage Updated!",requireActivity())
//                activity?.onBackPressed()
//                val intent = Intent(context?.orderDetails())
//                var bundle = Bundle()
//                bundle.putString(ConstantsDirectory.ENQUIRY_ID, enqID?.toString())
//                bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, "2")
//                intent.putExtras(bundle)
//                context?.startActivity(intent)

            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onStatusChangeSuccess " + e.message)
        }
    }

    override fun onStatusChangeFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                setDetails()
                hideLoader()
                Utility.displayMessage("Please Try Again",requireActivity())
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onStatusChangeFailure " + e.message)
        }
    }

    override fun onGetTransactionsSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Transaction","getSingleTransactions Success")
                setDetails()
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Transaction", "Exception onStatusChangeFailure " + e.message)
        }
    }

    override fun onGetTransactionsFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Transaction","onGetTransactionsFailure")
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Transaction", "Exception onStatusChangeFailure " + e.message)
        }
    }

    override fun onFetchCrSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Transaction","getSingleTransactions Success")
                setDetails()
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Transaction", "Exception onStatusChangeFailure " + e.message)
        }
    }

    override fun onFetchCrFailure() {
    }

}