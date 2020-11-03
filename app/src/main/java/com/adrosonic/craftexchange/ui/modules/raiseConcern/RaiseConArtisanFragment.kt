package com.adrosonic.craftexchange.ui.modules.raiseConcern

import android.app.Dialog
import android.content.Context
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
    FaultyOrdersViewModel.PostReviewInterface{

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
        setReviewListData()
        setArtisanReviewList(requireContext(),mArtisanList,mBinding?.listArtisanReview)

        if(Utility?.checkIfInternetConnected(requireContext())){
            enqID?.toLong()?.let { mOrdVM?.getOrderProgressDetails(it) }
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
            if (orderProgressDetails != null){
                setBuyerReviewList(orderProgressDetails!!)
            }
        }

        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it, 0) }
        if (orderDetails != null) {
            setDetails()
        }

        dialog = Utility?.loadingDialog(requireContext())
        mBinding?.txtGotoChat?.setOnClickListener {
            Utility?.displayMessage("Coming Soon",requireContext())
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
        var itr2 = Utility?.getArtFaultReviewData()?.data?.iterator()
        if (itr2 != null) {
            while (itr2?.hasNext()) {
                var data2 = itr2?.next()
                mArtisanList?.add(data2?.comment)
            }
        }
    }

    fun setDetails() {
        mBinding?.orderCode?.text = orderDetails?.orderCode ?: "N.A"
        mBinding?.orderDate?.text = "Order Created on : ${orderDetails?.orderCreatedOn}"
        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount}"

        setProductImage()
        setProductName()
        setFaultStatus()
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

    fun setFaultStatus() {
        mBinding?.faultStatusText?.text = "Fault Raised"
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

    override fun onOPFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OPD","onFailure")
            })
        } catch (e: Exception) {
            Log.e("OPD", "Exception onFailure " + e.message)
        }
    }

    override fun onOPSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OPD","onSuccess")
                orderProgressDetails = enqID?.toLong()?.let { mOrdVM?.loadOrderProgressDetails(it) }
                if (orderProgressDetails != null){
                    setBuyerReviewList(orderProgressDetails!!)
                }
            })
        } catch (e: Exception) {
            Log.e("OPD", "Exception onFailure " + e.message)
        }
    }

    override fun onPostSuccess() {
        try {
            Handler(Looper.getMainLooper()).post {
                dialog?.cancel()
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
                dialog?.cancel()
                Utility?.displayMessage("Please try again",requireContext())
            }
        } catch (e: Exception) {
            Log.e("FaultReviewPost", "Exception onFailure " + e.message)
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