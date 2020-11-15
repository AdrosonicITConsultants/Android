package com.adrosonic.craftexchange.ui.modules.raiseConcern

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OrderProgressDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentRaiseConArtisanBinding
import com.adrosonic.craftexchange.repository.data.response.faultyOrders.FaultRefData
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.rating.SendRatingActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.FaultyOrdersViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RaiseConArtisanFragment : Fragment(),
    OrdersViewModel.GetOrderProgressInterface,
    FaultyOrdersViewModel.PostReviewInterface,
    OrdersViewModel.RecreationDispatchInterface{

    private var param1: String? = null
    private var param2: String? = null

    var enqID: String? = ""
    private var url: String? = ""
    var weft: String? = ""
    var warp: String? = ""
    var extraweft: String? = ""
    var prodCategory: String? = ""
    var status: String? = ""

    var mBuyerList = ArrayList<FaultRefData>()
    var mArtisanViewList = ArrayList<FaultRefData>()
    var mArtisanList = ArrayList<String>()
    var dialog : Dialog ?= null

    var artisanReviewId : Int?=null

    private var mBinding: FragmentRaiseConArtisanBinding? = null

    private var orderDetails: Orders? = null
    private var orderProgressDetails: OrderProgressDetails? = null

    val mOrdVM: OrdersViewModel by viewModels()
    val mFOVM: FaultyOrdersViewModel by viewModels()

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
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_raise_con_artisan, container, false)
        if (param1 != null) {
            enqID = if (param1!!.isNotEmpty()) param1 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrdVM?.getOrderProgressListener = this
        mFOVM?.postReviewListener = this
        mOrdVM?.recreationDispatchListener = this

        setReviewListData()

        if(Utility?.checkIfInternetConnected(requireContext())){
            enqID?.toLong()?.let { mOrdVM?.getOrderProgressDetails(it) }
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
            if (orderProgressDetails != null){
                orderProgressDetails?.let { setViews(it) }
            }
        }

        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it, 0) }
        if (orderDetails != null) {
            setDetails()
        }

        dialog = Utility?.loadingDialog(requireContext())

        mBinding?.txtGotoChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnSend?.setOnClickListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                if(mBinding?.commentBox?.nonEmpty() == true){
                    if(artisanReviewId != null){
                        dialog?.show()
                        enqID?.let { it1 -> mFOVM?.postArtisanFaultReview(it1,mBinding?.commentBox?.text.toString(),artisanReviewId.toString()) }
                    }else{
                        Utility?.displayMessage("Select atleast one Remark",requireContext())
                    }
                }else{
                    mBinding?.commentBox?.nonEmpty{ mBinding?.commentBox?.error = it }
                }
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }

        mBinding?.btnViewResppnse?.setOnClickListener {
            if(mBinding?.viewFaultDetailsLayout?.visibility == View.GONE){
                mBinding?.viewFaultDetailsLayout?.visibility = View.VISIBLE
            }else{
                mBinding?.viewFaultDetailsLayout?.visibility = View.GONE
            }
        }

        mBinding?.btnRateReview?.setOnClickListener {
            val myIntent = Intent(requireContext(), SendRatingActivity::class.java)
            myIntent.putExtra("enquiryId", enqID)
            startActivity(myIntent)
        }

        mBinding?.btnViewGotoChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }
    }

    fun setReviewListData(){
        mBuyerList?.clear()
        var itr1 = Utility?.getBuyFaultReviewData()?.data?.iterator()
        if (itr1 != null) {
            while (itr1?.hasNext()) {
                var data1 = itr1?.next()
                mBuyerList?.add(data1)
            }
        }
        mArtisanList?.clear()
        mArtisanViewList?.clear()
        var itr2 = Utility?.getArtFaultReviewData()?.data?.iterator()
        if (itr2 != null) {
            while (itr2?.hasNext()) {
                var data2 = itr2?.next()
                mArtisanList?.add(data2?.comment)
                mArtisanViewList?.add(data2)
            }
        }
    }

    fun setDetails() {
        mBinding?.orderCode?.text = orderDetails?.orderCode ?: "N.A"
        mBinding?.orderDate?.text = "Order Created on : ${orderDetails?.orderCreatedOn?.split("T")?.get(0)}"
        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount}"

        setProductImage()
        setProductName()
    }

    fun setArtisanReviewList(context: Context, array: ArrayList<String>, spinner: Spinner?) {
        var adapter= ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                artisanReviewId = position?.plus(1)
            }
        })
    }

    fun setBuyerReviewList(details : OrderProgressDetails) {
        var revData = details?.buyerReviewId
        var stringList = arrayListOf<String>()
        stringList?.clear()
        val array = revData?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (array != null) {
            for(iD in array){
                mBuyerList?.forEach {
                    if(iD == it.id.toString() ){
                        stringList?.add(it.comment)
                    }
                }
            }
        }
        if (stringList != null) {
            var adapter= ArrayAdapter(requireContext(), R.layout.item_view_fault_review,stringList)
            mBinding?.faultyReviewList?.adapter = adapter
        }
        mBinding?.note?.text = details?.buyerReviewComment
    }

    fun setProductImage() {
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

    fun setProductName() {
        //Product name or Product cloth details
        if (orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
        } else {
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

    fun showConfirmDialog() {
        var cDialog = Dialog(requireContext())
        cDialog.setContentView(R.layout.dialog_confirm_skip_recreate)
        cDialog.show()

        val confirmRecreate = cDialog.findViewById(R.id.btn_confirm_recreate) as Button
        val skipRecreate = cDialog.findViewById(R.id.btn_skip_recreate) as Button
        skipRecreate?.setOnClickListener {
            cDialog?.cancel()
        }
        confirmRecreate.setOnClickListener {
            cDialog.cancel()
            if(Utility?.checkIfInternetConnected(requireContext())){
                dialog?.show()
                enqID?.toLong()?.let { it1 -> mOrdVM?.recreateOrder(it1) }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }
    }

    fun setFaultStatus(details: OrderProgressDetails) {
        if(details?.isResolved == 0L){
            mBinding?.faultStatusText?.text = "Fault Raised"
            context?.let {
                ContextCompat.getColor(
                    it, R.color.red_logo
                )
            }?.let { mBinding?.faultStatusText?.setTextColor(it) }

            context?.let {
                ContextCompat.getColor(
                    it, R.color.red_logo
                )
            }?.let { mBinding?.faultStatusDot?.setColorFilter(it) }
        }else{
            mBinding?.faultStatusText?.text = "Concern Resolved"
            context?.let {
                ContextCompat.getColor(
                    it, R.color.light_green
                )
            }?.let { mBinding?.faultStatusText?.setTextColor(it) }

            context?.let {
                ContextCompat.getColor(
                    it, R.color.light_green
                )
            }?.let { mBinding?.faultStatusDot?.setColorFilter(it) }
        }
    }

    fun setRemarkData(details: OrderProgressDetails){
        //set buyer response
        var revBuyerData = details?.buyerReviewId
        var stringList = arrayListOf<String>()
        stringList?.clear()
        val array = revBuyerData?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (array != null) {
            for(iD in array){
                mBuyerList?.forEach {
                    if(iD == it.id.toString() ){
                        stringList?.add(it.comment)
                    }
                }
            }
        }
        if (stringList != null) {
            var adapter= ArrayAdapter(requireContext(), R.layout.item_view_fault_review,stringList)
            mBinding?.buyerFaultReviewList?.adapter = adapter
        }
        mBinding?.buyerNote?.text = details?.buyerReviewComment

        //set artisan response
        if(details?.artisanReviewComment!=null){
            mBinding?.txtRemarks?.visibility = View.VISIBLE
            mBinding?.artisanCommentBox?.visibility = View.VISIBLE
            mBinding?.artisanReview?.visibility = View.VISIBLE

            var revArtisanData = details?.artisanReviewId
            mArtisanViewList?.forEach {
                if(revArtisanData == it?.id.toString()){
                    mBinding?.artisanReview?.text = it?.comment
                }
            }
            mBinding?.artisanCommentBox?.text = details?.artisanReviewComment
        }else{
            mBinding?.txtRemarks?.visibility = View.GONE
            mBinding?.artisanCommentBox?.visibility = View.GONE
            mBinding?.artisanReview?.visibility = View.GONE
        }
    }

    fun setActionButtonVisibilities(details: OrderProgressDetails){
        if(details?.isResolved == 0L){
            mBinding?.rateReviewLayout?.visibility = View.GONE
        }else{
            mBinding?.rateReviewLayout?.visibility = View.VISIBLE
        }
    }


    fun setViews(details: OrderProgressDetails){
        setFaultStatus(details)
        if(details?.isResolved == 0L){
            if(details?.artisanReviewComment == null){
                mBinding?.fillArtisanFaultyFormLayout?.visibility = View.VISIBLE
                mBinding?.viewFaultyFormLayout?.visibility = View.GONE
                setArtisanReviewList(requireContext(),mArtisanList,mBinding?.listArtisanReview)
                setBuyerReviewList(orderProgressDetails!!)
            }else{
                mBinding?.fillArtisanFaultyFormLayout?.visibility = View.GONE
                mBinding?.viewFaultyFormLayout?.visibility = View.VISIBLE
                setRemarkData(details)
            }
        }else{
            mBinding?.fillArtisanFaultyFormLayout?.visibility = View.GONE
            mBinding?.viewFaultyFormLayout?.visibility = View.VISIBLE
            setRemarkData(details)
            setActionButtonVisibilities(details)
        }
    }


    override fun onOPFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OPD","onFailure")
                dialog?.cancel()
            })
        } catch (e: Exception) {
            Log.e("OPD", "Exception onFailure " + e.message)
        }
    }

    override fun onOPSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OPD","onSuccess")
                dialog?.cancel()
                orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
                if (orderProgressDetails != null){
                    orderProgressDetails?.let { setViews(it) }
                }
            })
        } catch (e: Exception) {
            Log.e("OPD", "Exception onFailure " + e.message)
        }
    }

    override fun onPostSuccess() {
        try {
            Handler(Looper.getMainLooper()).post {

                if(artisanReviewId == 2){
                    if(Utility?.checkIfInternetConnected(requireContext())){
                        enqID?.toLong()?.let { mOrdVM?.getOrderProgressDetails(it) }
                    }else{
                        Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
                        dialog?.cancel()
                        activity?.onBackPressed()
                    }
                    showConfirmDialog()
                }else{
                    dialog?.cancel()
                    activity?.onBackPressed()
                }

            }
        } catch (e: Exception) {
            Log.e("FaultReviewPost", "Exception onFailure " + e.message)
        }
    }

    override fun onPostFailure() {
        try {
            Handler(Looper.getMainLooper()).post {
                dialog?.cancel()
                Utility?.displayMessage("Please try again",requireContext())
            }
        } catch (e: Exception) {
            Log.e("FaultReviewPost", "Exception onFailure " + e.message)
        }
    }

    override fun onRDFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Utility.displayMessage("Failed to update Recreation status",requireContext())
                dialog?.cancel()
            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    override fun onRDSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                dialog?.cancel()
                Utility.displayMessage("Order Recreation Success",requireContext())
                activity?.onBackPressed()
            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            RaiseConArtisanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}