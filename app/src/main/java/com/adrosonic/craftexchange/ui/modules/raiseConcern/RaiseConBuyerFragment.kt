package com.adrosonic.craftexchange.ui.modules.raiseConcern

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
import com.adrosonic.craftexchange.ui.modules.raiseConcern.adapter.FaultReviewRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.FaultyOrdersViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.Gson
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

    var mBuyerFaultList = ArrayList<FaultRefData>()
    var mFaultRecyclerAdapter: FaultReviewRecyclerAdapter? = null

    var dialog : Dialog ?= null

    private var orderDetails: Orders? = null

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
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFOVM?.postReviewListener = this

        setReviewListData()
        setFaultRecyclerView()
        setFaultStatus()
        orderDetails = enqID?.toLong()?.let { mOrdVM.loadSingleOrderDetails(it, 0) }
        if (orderDetails != null) {
            setDetails()
        }

        dialog = Utility.loadingDialog(requireContext())

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnSubmit?.setOnClickListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                showFaultSubmitDialog()
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
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
                        dialog?.show()
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

    fun setFaultStatus() {

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

    fun setFaultRecyclerView() {
        mBinding?.faultyReviewFormRecyceler?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mFaultRecyclerAdapter = FaultReviewRecyclerAdapter(requireContext(), mBuyerList)
        mBinding?.faultyReviewFormRecyceler?.adapter = mFaultRecyclerAdapter
        mFaultRecyclerAdapter?.fAdapterListener = this
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
            RaiseConBuyerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}