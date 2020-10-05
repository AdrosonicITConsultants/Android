package com.adrosonic.craftexchange.ui.modules.order

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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.databinding.FragmentCompEnqDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentCompOrderDetailsBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.EnquiryStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.enquiryPayment
import com.adrosonic.craftexchange.ui.modules.enquiry.ArtEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.BuyEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchange.ui.modules.transaction.adapter.BuyerOnGoTranRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CompletedOrderDetailsFragment : Fragment(),
    OrdersViewModel.FetchOrderInterface,
    TransactionViewModel.TransactionInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var enqID : Long ?= 0
    private var enqStatus : Long ?= 0
    private var orderDetails : Orders?= null

    private var url : String?=""
    private var status : String?= ""

    val mOrdersVm : OrdersViewModel by viewModels()
    val mTranVM : TransactionViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var isCustom : Boolean ?= false
    var profile : String ?= ""


    var mBinding : FragmentCompOrderDetailsBinding?= null
    var moqDeliveryTimeList=ArrayList<Datum>()
    var moqId=0L

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_comp_order_details, container, false)
        enqID = param1?.toLong()
        enqStatus = param2?.toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile = Prefs.getString(ConstantsDirectory.PROFILE,"")
        Utility.getDeliveryTimeList()?.let {moqDeliveryTimeList.addAll(it)  }
        mOrdersVm.fetchEnqListener = this
        if(Utility.checkIfInternetConnected(requireActivity())){
            enqID?.let {
                mOrdersVm.getSingleCompletedOrder(it)
                mTranVM.getSingleCompletedTransactions(it)
            }
            viewLoader()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            setDetails()
        }

        enqID?.let {
            mOrdersVm.getSingleOnOrderData(it,1)
                .observe(viewLifecycleOwner, Observer<Orders> {
                    orderDetails = it
                })
        }

        mBinding?.swipeOrderDetails?.setOnRefreshListener {
            enqID?.let { mOrdersVm.getSingleCompletedOrder(it) }
        }
        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            when(profile){
                ConstantsDirectory.ARTISAN -> {
                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.enquiry_details_container,
                                BuyEnqDetailsFragment.newInstance(orderDetails?.enquiryId.toString(),orderDetails?.enquiryStatusId.toString()))
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
                ConstantsDirectory.BUYER -> {
                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.enquiry_details_container,
                                ArtEnqDetailsFragment.newInstance(orderDetails?.enquiryId.toString(),orderDetails?.enquiryStatusId.toString(),0))
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
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
            enqID?.let {  startActivity(requireContext().raisePiContext(it,true, SendPiRequest())) }
        }

        mBinding?.viewPaymentLayer?.setOnClickListener {
//            startActivity(context?.enquiryPayment()
//                ?.putExtra(ConstantsDirectory.ENQUIRY_ID,enqID)
//                ?.putExtra(ConstantsDirectory.ENQUIRY_STATUS_FLAG,EnquiryStatus.COMPLETED.getId())
//                ?.putExtra(ConstantsDirectory.PI_ID,0))
            if(mBinding?.transactionList!!.visibility==View.VISIBLE) mBinding?.transactionList!!.visibility=View.GONE
            else mBinding?.transactionList!!.visibility=View.VISIBLE
        }

    }

    fun setDetails(){
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                setTabVisibilities()
                //client profile
                when (profile) {
                    ConstantsDirectory.ARTISAN -> {
                        mBinding?.profileName?.text = ConstantsDirectory.BUYER
                    }
                    ConstantsDirectory.BUYER -> {
                        mBinding?.profileName?.text = ConstantsDirectory.ARTISAN
                    }
                }

                mBinding?.enquiryCode?.text = orderDetails?.orderCode
                mBinding?.enquiryStartDate?.text =
                    "Date started : ${orderDetails?.startedOn?.split("T")?.get(0)}"

                val image = orderDetails?.productImages?.split((",").toRegex())
                    ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

                //brand name of product & product Image
                if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
                    url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
                    isCustom = true
                } else {
                    url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
                    isCustom = false
                }
                mBinding?.productImage?.let {
                  if(requireContext()!=null) ImageSetter.setImage(
                        requireContext(),
                        url!!,
                        mBinding?.productImage!!,
                        R.drawable.artisan_logo_placeholder,
                        R.drawable.artisan_logo_placeholder,
                        R.drawable.artisan_logo_placeholder
                    )
                }

                mBinding?.company?.text = orderDetails?.brandName

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
                    fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }
                        ?.let {
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

                if (orderDetails?.enquiryStageId == 10L) {
                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.black_text
                        )
                    }?.let { mBinding?.closedDot?.setColorFilter(it) }

                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.black_text
                        )
                    }?.let { mBinding?.closedText?.setTextColor(it) }
                    mBinding?.closedText?.text = "Order Completed"

                } else {
                 context?.let {
                        ContextCompat.getColor(
                            it, R.color.red_logo
                        )
                    }?.let { mBinding?.closedDot?.setColorFilter(it) }

                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.red_logo
                        )
                    }?.let { mBinding?.closedText?.setTextColor(it) }
                    mBinding?.closedText?.text = "Order Closed"
                }

                mBinding?.enquiryUpdateDate?.text = "Last updated : ${orderDetails?.lastUpdated?.split("T")?.get(0)}"
                mBinding?.brand?.text = orderDetails?.companyName

                var tranList = TransactionPredicates.getTransactionByEnquiryId(enqID?:0)
                if(tranList!!.size>0){
                    mBinding?.viewPaymentLayer?.visibility = View.VISIBLE
                    mBinding?.viewTransaction?.text="View"
                    mBinding?.transactionList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false )
                    val transactionAdapter =  BuyerOnGoTranRecyclerAdapter(requireContext(), tranList)
                    mBinding?.transactionList?.adapter = transactionAdapter
//                    transactionAdapter.listener = this
                } else {
                    mBinding?.viewTransaction?.text="No transaction present"
                }
            })
            } catch (e: Exception) {
                Log.e("setDetails", "Exception " + e.message)
            }
    }

    fun viewLoader(){
        mBinding?.swipeOrderDetails?.isRefreshing =true
    }
    fun hideLoader(){
        mBinding?.swipeOrderDetails?.isRefreshing =false
    }

    private fun setTabVisibilities(){
//        when(profile){
//            ConstantsDirectory.ARTISAN -> {
                if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
                    if(orderDetails?.enquiryStageId!! >= 4L){
                        mBinding?.viewPaymentLayer?.visibility = View.VISIBLE
                    }else{
                        mBinding?.viewPaymentLayer?.visibility = View.GONE
                    }
                }else{
                    mBinding?.viewPaymentLayer?.visibility = View.GONE
                }
//            }
//            else -> {
//                mBinding?.viewPaymentLayer?.visibility = View.GONE
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        enqID?.let { mOrdersVm?.getSingleCompletedOrder(it) }
//        setDetails()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Order Details", "onFailure")
                enqID?.let { mOrdersVm.getSingleCompletedOrder(it) }
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Order Details", "Exception onFailure " + e.message)
        }
    }


    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Order Details", "onSuccess")
//                enqID?.let { mOrdersVm.getSingleCompletedOrder(it) }
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Order Details", "Exception onFailure " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String,param2 : String) =
            CompletedOrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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

}