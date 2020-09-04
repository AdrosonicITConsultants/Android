package com.adrosonic.craftexchange.ui.modules.artisan.enquiry

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
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentArtisanOnGoEnqDetailsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchange.ui.modules.enquiry.ArtEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.BuyEnqDetailsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanOnGoEnqDetailsFragment : Fragment(),
    EnquiryViewModel.FetchOngoingEnqInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var enqID : Long ?= 0
    private var enqCode : String ?= ""
    private var enquiryDetails : OngoingEnquiries ?= null
    private var stageList : ArrayList<Pair<Long,String>> ?= null
    private var stageAPList : ArrayList<Triple<Long,Long,String>> ?= null
    private var nextEnqStage : String?=""
    private var prevEnqStage : String?=""
    private var currEnqStage : String ?= ""
    private var currEnqStageId : Long ?= 0
    private var currEnqStageSerNo : Long ?= 0
    private var url : String?=""
    private var status : String?= ""

    val mEnqVM : EnquiryViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""

    var mBinding : FragmentArtisanOnGoEnqDetailsBinding?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_on_go_enq_details, container, false)
        enqID = param1?.toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.fetchEnqListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            enqID?.let { mEnqVM.getSingleEnquiry(it) }
            viewLoader()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            setDetails()
        }

        enqID?.let {
            mEnqVM.getSingleEnqMutableData(it)
                .observe(viewLifecycleOwner, Observer<OngoingEnquiries> {
                    enquiryDetails = it
                })
        }

        mBinding?.btnMenu?.setOnClickListener {
            if(mBinding?.menuList?.visibility == View.GONE){
                mBinding?.menuList?.visibility = View.VISIBLE
            }else{
                mBinding?.menuList?.visibility = View.GONE
            }
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_details_container,
                        BuyEnqDetailsFragment.newInstance(enquiryDetails?.enquiryID.toString()))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

        mBinding?.productDetailsLayer?.setOnClickListener {
            if(enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
//                CustomProd()
                Utility?.displayMessage("View Custom Product Screen by buyer not implemented",requireActivity())
            }else{
                ArtisanProduct()
            }
        }
    }

    fun ArtisanProduct(){
        context?.startActivity(context?.addProductIntent(enquiryDetails?.productID?:0))
    }

    fun setDetails(){

        setTabVisibilities()

        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode
        mBinding?.enquiryStartDate?.text = "Date accepted : ${enquiryDetails?.startedOn?.split("T")?.get(0)}"

        val image = enquiryDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        //brand name of product & product Image
        if(enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
        }else{
            url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
        }
        mBinding?.productImage?.let { ImageSetter.setImage(requireActivity(),
            url!!, it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder) }

        mBinding?.buyerCompany?.text = enquiryDetails?.ProductBrandName

        //ProductAvailability
        when(enquiryDetails?.productStatusID){
            2L -> {
                status = context?.getString(R.string.in_stock)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            1L -> {
                status = context?.getString(R.string.made_to_order)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            else -> {
                status = context?.getString(R.string.requested_custom_design)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
        }

        //Product name or Product cloth details
        if(enquiryDetails?.productName != "") {
            mBinding?.productName?.text = enquiryDetails?.productName
            mBinding?.productNameDetails?.text = enquiryDetails?.productName
        }else{
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if(it.first == enquiryDetails?.weftYarnID){
                    weft = it.second
                }
                if(it.first == enquiryDetails?.warpYarnID){
                    warp = it.second
                }
                if(it.first == enquiryDetails?.extraWeftYarnID){
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if(it.first == enquiryDetails?.productCategoryID){
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft}"
            fp.setSpan(context?.let { ContextCompat.getColor(it,R.color.black_text) }?.let {
                ForegroundColorSpan(
                    it
                )
            }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)

            mBinding?.productNameDetails?.text = "Custom Design Product"
        }

        mBinding?.productAmount?.text = enquiryDetails?.totalAmount ?: "0"


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
                }?.let { mBinding?.enquiryStatusDot?.setBackgroundColor(it) }
            }

            2L,3L,4L,5L -> {
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.tab_details_selected_text)
                }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.tab_details_selected_text)
                }?.let { mBinding?.enquiryStatusDot?.setBackgroundColor(it) }
            }

            6L,7L,8L,9L,10L -> {
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { mBinding?.enquiryStatusText?.setTextColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.dark_green)
                }?.let { mBinding?.enquiryStatusDot?.setBackgroundColor(it) }

            }
        }
        mBinding?.enquiryStatusText?.text = enquiryStage

        mBinding?.enquiryUpdateDate?.text = "Last updated : ${enquiryDetails?.lastUpdated?.split("T")?.get(0)}"
        mBinding?.buyerBrand?.text = enquiryDetails?.ProductBrandName

        setProgressTimeline()

        //TODO implement to enq stage
        when(enquiryDetails?.enquiryStageID){
            3L -> {
                mBinding?.uploadDocLayout?.visibility = View.VISIBLE
            }
            8L -> {
                mBinding?.uploadDocLayout?.visibility = View.VISIBLE
            }
            else ->{
                mBinding?.uploadDocLayout?.visibility = View.GONE
            }
        }

    }

    private fun setProgressTimeline(){
        stageList?.clear()
        stageAPList?.clear()
        if(enquiryDetails?.productType == "Custom Product" || enquiryDetails?.productStatusID == 1L){
            stageList = Utility.getEnquiryStagesData() // custom product or made to order
            Log.e("enqdata", "List All : $stageList")

            stageList?.forEach {
                if(it.first == enquiryDetails?.enquiryStageID){
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

        }else{
            stageAPList = Utility.getAvaiProdEnquiryStagesData() // available product
            Log.e("enqdata", "List AP : $stageAPList")

            stageAPList?.forEach {
                if(it.second == enquiryDetails?.enquiryStageID){
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
        if(enquiryDetails?.isBlue == 1L){
            when(currEnqStageId){
                4L,9L -> {
                    mBinding?.awaitingPaymentReceipt?.visibility = View.VISIBLE
                }
                else ->{
                    mBinding?.awaitingPaymentReceipt?.visibility = View.GONE
////                    mBinding?.transactionLayout?.visibility = View.GONE
                }
            }
        }

        when(currEnqStageId){
            2L,7L -> mBinding?.uploadDocLayout?.visibility = View.VISIBLE
            else -> mBinding?.uploadDocLayout?.visibility = View.GONE
        }

    }


    fun viewLoader(){
        mBinding?.artisanOngoEnqDetails?.visibility = View.GONE
        mBinding?.swipeEnquiryDetails?.isRefreshing = true
    }
    fun hideLoader(){
        mBinding?.artisanOngoEnqDetails?.visibility = View.VISIBLE
        mBinding?.swipeEnquiryDetails?.isRefreshing = false
    }

    private fun setTabVisibilities(){
        if(enquiryDetails?.isMoqSend == 1L){
            mBinding?.moqDetailsLayer?.visibility = View.VISIBLE
        }else{
            mBinding?.moqDetailsLayer?.visibility = View.GONE
        }

        if(enquiryDetails?.isPiSend == 1L){
            mBinding?.piDetailsLayer?.visibility = View.VISIBLE
        }else{
            mBinding?.piDetailsLayer?.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        enqID?.let { mEnqVM?.getSingleEnqMutableData(it) }
        setDetails()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onFailure")
                enqID?.let { mEnqVM.getSingleEnqMutableData(it) }
                hideLoader()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSuccess")
                enqID?.let { mEnqVM.getSingleEnqMutableData(it) }
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String) =
            ArtisanOnGoEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}