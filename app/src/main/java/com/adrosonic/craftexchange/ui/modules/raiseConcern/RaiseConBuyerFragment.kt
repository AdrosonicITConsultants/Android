package com.adrosonic.craftexchange.ui.modules.raiseConcern

import android.app.Dialog
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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OrderProgressDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentPaymentReceiptBinding
import com.adrosonic.craftexchange.databinding.FragmentRaiseConBuyerBinding
import com.adrosonic.craftexchange.enums.EnquiryStages
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.response.faultyOrders.FaultRefData
import com.adrosonic.craftexchange.repository.data.response.faultyOrders.FaultReviewRefResponse
import com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter.FillQcRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.raiseConcern.adapter.FaultReviewRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.rating.SendRatingActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.FaultyOrdersViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import io.realm.RealmResults
import kotlinx.android.synthetic.main.dialog_save_upload.view.*
import java.util.Arrays.stream
import java.util.stream.Collectors
import java.util.stream.StreamSupport.stream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RaiseConBuyerFragment : Fragment(),
    FaultReviewRecyclerAdapter.UpdateFaultListInterface,
    FaultyOrdersViewModel.PostReviewInterface,
    OrdersViewModel.GetOrderProgressInterface,
    FaultyOrdersViewModel.ResolveFaultInterface{

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
    var mArtisanList = ArrayList<FaultRefData>()

    var mBuyerFaultList = ArrayList<FaultRefData>()
    var mFaultRecyclerAdapter: FaultReviewRecyclerAdapter? = null

    var loadingDialog : Dialog ?= null

    private var orderDetails: Orders? = null
    private var orderProgressDetails: OrderProgressDetails? = null

    val mOrdVM: OrdersViewModel by viewModels()
    val mFOVM: FaultyOrdersViewModel by viewModels()

    private var mBinding: FragmentRaiseConBuyerBinding? = null

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_raise_con_buyer, container, false)
        if (param1 != null) {
            enqID = if (param1!!.isNotEmpty()) param1 else "0"
            Log.e("BuyerRating", "11111111 enquiryId: $enqID")
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFOVM?.postReviewListener = this
        mOrdVM?.getOrderProgressListener = this
        mFOVM?.resolveFaultListener = this

        setReviewListData()

        if(Utility?.checkIfInternetConnected(requireContext())){
            enqID?.toLong()?.let { mOrdVM?.getOrderProgressDetails(it) }
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
            orderProgressDetails?.let { setViews(it) }
        }

        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it, 0) }
        if (orderDetails != null) {
            setDetails()
        }

        loadingDialog = Utility.loadingDialog(requireContext())

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.txtGotoChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }

        mBinding?.btnSubmit?.setOnClickListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                showFaultSubmitDialog()
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

        mBinding?.btnMarkConcernResolved?.setOnClickListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                loadingDialog?.show()
                enqID?.let { it1 -> mFOVM?.markFaultResolved(it1) }
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }

        mBinding?.btnRateReview?.setOnClickListener {
            Log.e("BuyerRating", "enquiryId: ${enqID?.toLong()}")
            val myIntent = Intent(requireContext(), SendRatingActivity::class.java)
            myIntent.putExtra("enquiryId", enqID?.toLong())
            startActivity(myIntent)
        }

        mBinding?.btnViewGotoChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }
    }

    //sets static buyer artisan review data
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
        var itr2 = Utility?.getArtFaultReviewData()?.data?.iterator()
        if (itr2 != null) {
            while (itr2?.hasNext()) {
                var data2 = itr2?.next()
                mArtisanList?.add(data2)
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

    fun showFaultSubmitDialog(){
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_fault_confirm)
        dialog.show()
        val btnSubmit = dialog.findViewById(R.id.btn_submit_fault) as TextView
        val btnCancel = dialog.findViewById(R.id.btn_cancel_fault) as TextView

        btnCancel?.setOnClickListener {
            dialog?.cancel()
        }
        btnSubmit?.setOnClickListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                dialog?.cancel()
                var list = ArrayList<Long>()
                var resultString : String ?= ""
                list?.clear()
                mBuyerFaultList?.forEach {
                    if(it?.isSelected){
                        list?.add(it?.id)
                    }
                }
                if(mBinding?.commentBox?.nonEmpty() == true){
                    if(list?.isNotEmpty()){
                        //convert array list into comma separated string
                        if (list.isNotEmpty()) {
                            val sb = StringBuilder()
                            for (s in list) {
                                sb.append(s).append(",")
                            }
                            resultString = sb.deleteCharAt(sb.length - 1).toString()
                        }
                        resultString?.let { it1 ->
                            mFOVM?.postBuyerFaultReview(orderDetails?.enquiryId.toString(),mBinding?.commentBox?.text.toString(), it1)
                        }
                        loadingDialog?.show()
                    }else{
                        Utility?.displayMessage("Please select atleast one Issue",requireContext())
                    }
               }else{
                   mBinding?.commentBox?.nonEmpty{ mBinding?.commentBox?.error = it }
               }
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }
    }

    fun setFaultStatus(details: OrderProgressDetails) {

        if(details?.isResolved == 0L){
            when(details?.isFaulty){
                0L -> {
                    mBinding?.faultStatusText?.text = "Report Fault"
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
                }

                1L -> {
                    mBinding?.faultStatusText?.text = "Concern Unresolved"
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
                }
            }
        }else{
            mBinding?.faultStatusText?.text = "Fault Resolved"
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

    fun setFaultRecyclerView() {
        mBinding?.faultyReviewFormRecyceler?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mFaultRecyclerAdapter = FaultReviewRecyclerAdapter(requireContext(), mBuyerList)
        mBinding?.faultyReviewFormRecyceler?.adapter = mFaultRecyclerAdapter
        mFaultRecyclerAdapter?.fAdapterListener = this
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
            mBinding?.faultyReviewList?.adapter = adapter
        }
        mBinding?.buyerNote?.text = details?.buyerReviewComment

        //set artisan response
        if(details?.artisanReviewComment!=null){
            mBinding?.txtRemarks?.visibility = View.VISIBLE
            mBinding?.artisanCommentBox?.visibility = View.VISIBLE
            mBinding?.artisanReview?.visibility = View.VISIBLE

            var revArtisanData = details?.artisanReviewId
            mArtisanList?.forEach {
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

            if(details?.artisanReviewId == "1"){
                mBinding?.btnMarkConcernResolved?.visibility = View.VISIBLE
            }else{
                mBinding?.btnMarkConcernResolved?.visibility = View.GONE
            }
        }else{
            mBinding?.btnMarkConcernResolved?.visibility = View.GONE
            mBinding?.rateReviewLayout?.visibility = View.VISIBLE
        }
    }

    fun setViews(details: OrderProgressDetails){
        setFaultStatus(details)
        if(details?.isResolved == 0L){
            if(details?.isFaulty == 0L){
                mBinding?.fillFaultyFormLayout?.visibility = View.VISIBLE
                mBinding?.viewFaultyFormLayout?.visibility = View.GONE
                setFaultRecyclerView()
            }else{
                mBinding?.fillFaultyFormLayout?.visibility = View.GONE
                mBinding?.viewFaultyFormLayout?.visibility = View.VISIBLE
//                mBinding?.txtReportedProblem?.visibility = View.VISIBLE
                mBinding?.txtConcernResolved?.visibility = View.GONE
                setRemarkData(details)
                setActionButtonVisibilities(details)
            }
        }else{
            mBinding?.fillFaultyFormLayout?.visibility = View.GONE
            mBinding?.viewFaultyFormLayout?.visibility = View.VISIBLE
//            mBinding?.txtReportedProblem?.visibility = View.GONE
            mBinding?.txtConcernResolved?.visibility = View.VISIBLE
            setRemarkData(details)
            setActionButtonVisibilities(details)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("apirecall", "onActivityCreated: recall rating api")
        var userId : String?= null
        userId = Prefs.getString(ConstantsDirectory.USER_ID, "")
        mOrdVM.ratingData(enqID?.toLong()!!, userId!!.toLong())
    }
    override fun updateList(faultData: FaultRefData) {
        try {
            Handler(Looper.getMainLooper()).post {
                var newObj: FaultRefData? = null
                var oldObj: FaultRefData? = null

                if (mBuyerFaultList?.isNotEmpty()) {
                    var itr = mBuyerFaultList.iterator()
                    if (itr != null) {
                        while (itr.hasNext()) {
                            var data = itr.next()
                            if (data.id == faultData.id) {
                                newObj = faultData
                                oldObj = data
                            }
                        }
                    }

                    if (newObj == null) {
                        mBuyerFaultList.add(faultData)
                    } else {
                        mBuyerFaultList.remove(oldObj)
                        mBuyerFaultList.add(newObj)
                    }
                } else {
                    mBuyerFaultList?.add(faultData)
                }
                Log.e("FaultReview", "list updated : $mBuyerFaultList....size : ${mBuyerFaultList.size}")
            }
        } catch (e: Exception) {
            Log.e("FaultReview", "Exception onFailure " + e.message)
        }

    }

    override fun onPostSuccess() {
        try {
            Handler(Looper.getMainLooper()).post {
                loadingDialog?.cancel()
                activity?.onBackPressed()
                Utility?.displayMessage("Success",requireContext())
            }
        } catch (e: Exception) {
            Log.e("FaultReviewPost", "Exception onFailure " + e.message)
        }
    }

    override fun onPostFailure() {
        try {
            Handler(Looper.getMainLooper()).post {
                loadingDialog?.cancel()
                Utility?.displayMessage("Please try again",requireContext())
            }
        } catch (e: Exception) {
            Log.e("FaultReviewPost", "Exception onFailure " + e.message)
        }
    }

    override fun onOPFailure() {
        try {
            Handler(Looper.getMainLooper()).post {
                loadingDialog?.cancel()
                Utility.displayMessage("Error fetching data",requireContext())
            }
        } catch (e: Exception) {
            Log.e("OrderProgressDetails", "Exception onFailure " + e.message)
        }
    }

    override fun onOPSuccess() {
        try {
            Handler(Looper.getMainLooper()).post {
                loadingDialog?.cancel()
                orderProgressDetails= enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
                orderProgressDetails?.let {
                    setViews(it)}
            }
        } catch (e: Exception) {
            Log.e("OrderProgressDetails", "Exception onFailure " + e.message)
        }
    }

    override fun onResolveFailure() {
        try {
            Handler(Looper.getMainLooper()).post {
                loadingDialog?.cancel()
                Utility.displayMessage("Error! Please try again.",requireContext())
            }
        } catch (e: Exception) {
            Log.e("FaultResolve", "Exception onFailure " + e.message)
        }
    }

    override fun onResolveSuccess() {
        try {
            Handler(Looper.getMainLooper()).post {
                if(Utility?.checkIfInternetConnected(requireContext())){
                    enqID?.toLong()?.let { mOrdVM?.getOrderProgressDetails(it) }
                }else{
                    Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
                    orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
                    orderProgressDetails?.let { setViews(it) }
                }

            }
        } catch (e: Exception) {
            Log.e("FaultResolve", "Exception onFailure " + e.message)
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            RaiseConBuyerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}