package com.adrosonic.craftexchangemarketing.ui.modules.enquiry

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
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentCompEnqDetailsBinding
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchangemarketing.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "isAritsan"


class CompEnqDetailsFragment : Fragment(),
    EnquiryViewModel.FetchEnquiryInterface,
    EnquiryViewModel.MoqInterface,
    EnquiryViewModel.BuyersMoqInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: Boolean? = false

    private var enqID : Long ?= 0
    private var enqStatus : Long ?= 0
    private var enquiryDetails : CompletedEnquiries?= null
//    private var stageList : ArrayList<Pair<Long,String>> ?= null
//    private var stageAPList : ArrayList<Triple<Long,Long,String>> ?= null
//    private var nextEnqStage : String?=""
//    private var prevEnqStage : String?=""
//    private var currEnqStage : String ?= ""
//    private var currEnqStageId : Long ?= 0
//    private var currEnqStageSerNo : Long ?= 0
    private var url : String?=""
    private var status : String?= ""

    val mEnqVM : EnquiryViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var isCustom : Boolean ?= false


    var mBinding : FragmentCompEnqDetailsBinding?= null
    var moqDeliveryJson=""
    var moqDeliveryTimeList=ArrayList<Datum>()
    var moqId=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getBoolean(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_comp_enq_details, container, false)
        enqID = param1?.toLong()
        enqStatus = param2?.toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moqDeliveryJson = UserConfig.shared.moqDeliveryDates
        val gson = GsonBuilder().create()
        val moqDeliveryTime = gson.fromJson(moqDeliveryJson, MoqDeliveryTimesResponse::class.java)
        moqDeliveryTimeList.addAll(moqDeliveryTime.data)
        mEnqVM.fetchEnqListener = this
        mEnqVM.moqListener = this
        mEnqVM.buyerMoqListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            enqID?.let { mEnqVM.getSingleCompletedEnquiry(it) }
            viewLoader()
            moqId= MoqsPredicates.getSingleMoq(enqID)?.moqId?:0
            Log.e("getSingleMoq","moqId: $moqId")
            if(moqId<=0){
                viewLoader()
                if(param3!!) mEnqVM.getSingleMoq(enqID!!)
                else mEnqVM.getMoqs(enqID!!)
            }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            setDetails()
        }

        enqID?.let {
            mEnqVM.getSingleCompEnqData(it)
                .observe(viewLifecycleOwner, Observer<CompletedEnquiries> {
                    enquiryDetails = it
                })
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
        mBinding?.brandDetailsLayer?.setOnClickListener {
            when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
                ConstantsDirectory.ARTISAN -> {
                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.enquiry_details_container,
                                BuyEnqDetailsFragment.newInstance(enquiryDetails?.enquiryID.toString(),enquiryDetails?.enquiryStatusID.toString()))
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
                ConstantsDirectory.BUYER -> {
                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.enquiry_details_container,
                                ArtEnqDetailsFragment.newInstance(enquiryDetails?.enquiryID.toString(),enquiryDetails?.enquiryStatusID.toString(),0))
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }
            }
        }

        mBinding?.moqDetailsLayer?.setOnClickListener {
            if(moqId>0) {
                if (mBinding?.moqDetails?.visibility == View.VISIBLE) mBinding?.moqDetails?.visibility = View.GONE
                else mBinding?.moqDetails?.visibility = View.VISIBLE
            }
        }

        mBinding?.productDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                isCustom?.let { it1 ->
                    ViewProductDetailsFragment.newInstance(enquiryDetails?.productID!!.toLong(),
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
        mBinding?.viewPiLayout?.setOnClickListener {
            enqID?.let {  startActivity(requireContext().raisePiContext(it,true, null))
            }
        }

    }

    fun setDetails(){
        setTabVisibilities()
        when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
            ConstantsDirectory.ARTISAN -> {
                mBinding?.profileName?.text = ConstantsDirectory.BUYER
            }
            ConstantsDirectory.BUYER -> {
                mBinding?.profileName?.text = ConstantsDirectory.ARTISAN
            }
        }

        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode
        mBinding?.enquiryStartDate?.text = "Date started : ${enquiryDetails?.startedOn?.split("T")?.get(0)}"

        val image = enquiryDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        //brand name of product & product Image
        if(enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
            isCustom = true
        }else{
            url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
            isCustom = false
        }
        mBinding?.productImage?.let { ImageSetter.setImage(requireActivity(),
            url!!, it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder) }

        mBinding?.company?.text = enquiryDetails?.ProductBrandName

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

        mBinding?.productAmount?.text = "₹ ${enquiryDetails?.totalAmount ?: 0}"


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

        if(enquiryDetails?.enquiryStageID == 10L){
            context?.let {
                ContextCompat.getColor(
                    it, R.color.black_text)
            }?.let { mBinding?.closedDot?.setColorFilter(it) }

            context?.let {
                ContextCompat.getColor(
                    it, R.color.black_text)
            }?.let { mBinding?.closedText?.setTextColor(it) }
            mBinding?.closedText?.text = "Order Completed"

        }else{
            context?.let {
                ContextCompat.getColor(
                    it, R.color.red_logo)
            }?.let { mBinding?.closedDot?.setColorFilter(it) }

            context?.let {
                ContextCompat.getColor(
                    it, R.color.red_logo)
            }?.let { mBinding?.closedText?.setTextColor(it) }
            mBinding?.closedText?.text = "Enquiry Closed"
        }

        mBinding?.enquiryUpdateDate?.text = "Last updated : ${enquiryDetails?.lastUpdated?.split("T")?.get(0)}"
        mBinding?.brand?.text = enquiryDetails?.ProductBrandName

        val moq=MoqsPredicates.getSingleMoq(enqID)
        if(moq!=null){
            moqId=moq?.moqId?:0
//            mBinding?.moqDetails?.visibility=View.VISIBLE
            mBinding?.imgDownArr?.visibility=View.VISIBLE
            mBinding?.moqOrderQty?.text=moq?.moq.toString()
            mBinding?.moqOrderAmount?.text=moq?.ppu
            mBinding?.orderQuantity?.text=moq?.moq.toString()
            mBinding?.orderAmount?.text=moq?.ppu
            moqDeliveryTimeList.forEach {
                if (it.id.equals(moq?.deliveryTimeId)) {
                mBinding?.moqOrderEta?.text=if(it?.days.equals(0L)){"Immediate"} else "${it?.days} Days"
                mBinding?.orderTime?.text=if(it?.days.equals(0L)){"Immediate"} else "${it?.days} Days"
                }
            }
        }else{
            mBinding?.moqDetails?.visibility=View.GONE
            mBinding?.imgDownArr?.visibility=View.GONE
            mBinding?.orderTime?.text="No MOQs present"

        }
        if(enquiryDetails?.isPiSend!!.equals(1)){
            mBinding?.viewPiLayout?.visibility=View.VISIBLE
        }else  mBinding?.viewPiLayout?.visibility=View.GONE

    }

    fun viewLoader(){
        mBinding?.compEnqDetails?.visibility = View.GONE
        mBinding?.swipeEnquiryDetails?.isRefreshing = true
    }
    fun hideLoader(){
        mBinding?.compEnqDetails?.visibility = View.VISIBLE
        mBinding?.swipeEnquiryDetails?.isRefreshing = false
    }

    private fun setTabVisibilities(){
//        if(enquiryDetails?.isMoqSend == 1L){
//            mBinding?.moqDetailsLayer?.visibility = View.VISIBLE
//        }else{
//            mBinding?.moqDetailsLayer?.visibility = View.GONE
//        }

//        if(enquiryDetails?.isPiSend == 1L){
//            mBinding?.piDetailsLayer?.visibility = View.VISIBLE
//        }else{
//            mBinding?.piDetailsLayer?.visibility = View.GONE
//        }
    }

    override fun onResume() {
        super.onResume()
        enqID?.let { mEnqVM?.getSingleCompEnqData(it) }
        setDetails()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onFailure")
                enqID?.let { mEnqVM.getSingleCompEnqData(it) }
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
                enqID?.let { mEnqVM.getSingleCompEnqData(it) }
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String,param2 : String,param3 : Boolean) =
            CompEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putBoolean(ARG_PARAM3, param3)
                }
            }
    }

    override fun onAddMoqFailure() {
    }

    override fun onAddMoqSuccess() {
    }

    override fun onGetMoqCall() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onAddMoqSuccess " + e.message)
        }
    }

    override fun onSendCustomMoqSuccess(moq: Long) {
        TODO("Not yet implemented")
    }

    override fun onSendCustomMoqFailure() {
        TODO("Not yet implemented")
    }
}