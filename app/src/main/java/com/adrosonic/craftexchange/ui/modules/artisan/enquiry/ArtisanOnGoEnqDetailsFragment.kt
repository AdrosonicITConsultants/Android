package com.adrosonic.craftexchange.ui.modules.artisan.enquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanOnGoEnqDetailsBinding
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.piContext
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchange.ui.modules.enquiry.BuyEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.agik.swipe_button.Controller.OnSwipeCompleteListener
import com.agik.swipe_button.View.Swipe_Button_View
import com.google.gson.GsonBuilder


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanOnGoEnqDetailsFragment : Fragment(),
    EnquiryViewModel.FetchEnquiryInterface,
    EnquiryViewModel.MoqInterface{
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
    private var isCustom : Boolean ?= false


//    var moqDeliveryJson=""
    var moqDeliveryTimeList=ArrayList<Datum>()
    var arrayDeliveryDscrp=ArrayList<String>()
    var estId=0L
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
//        moqDeliveryJson = UserConfig.shared.moqDeliveryDates
//        val gson = GsonBuilder().create()
//        val moqDeliveryTime = gson.fromJson(moqDeliveryJson, MoqDeliveryTimesResponse::class.java)
        Utility.getDeliveryTimeList()?.let {moqDeliveryTimeList.addAll(it)  }
        mEnqVM?.moqListener=this
        mEnqVM?.fetchEnqListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            viewLoader()
            enqID?.let { mEnqVM.getSingleOngoingEnquiry(it) }
            val moqId=MoqsPredicates.getSingleMoq(enqID)?.moqId?:0
            Log.e("getSingleMoq","moqId: $moqId")
            if(moqId<=0){
                viewLoader()
                mEnqVM.getSingleMoq(enqID!!)
            }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
//            setDetails()
        }

        enqID?.let {
            mEnqVM.getSingleOnEnqData(it)
                .observe(viewLifecycleOwner, Observer<OngoingEnquiries> {
                    enquiryDetails = it
                })
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_details_container,
                        BuyEnqDetailsFragment.newInstance(enquiryDetails?.enquiryID.toString(),enquiryDetails?.enquiryStatusID.toString()))
                    ?.addToBackStack(null)
                    ?.commit()
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

        mBinding?.moqDetailsLayer?.setOnClickListener {
        if(mBinding?.moqDetails?.visibility==View.VISIBLE)mBinding?.moqDetails?.visibility=View.GONE
        else mBinding?.moqDetails?.visibility=View.VISIBLE
        }

        mBinding?.spEstDays?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position>0){
                    var selection=arrayDeliveryDscrp.get(position)
                    moqDeliveryTimeList.forEach{
                        if(it.deliveryDesc.equals(selection, true)){
                            estId=it.id

                        }
                    }
                } else estId=0
            }
        }

        mBinding?.txtBidMoq?.setOnSwipeCompleteListener_forward_reverse(object : OnSwipeCompleteListener {
            override fun onSwipe_Forward(swipeView: Swipe_Button_View) {
                val additionalInfo=mBinding?.etAddNote?.text.toString()
                val moq=mBinding?.etMoq?.text.toString()
                val ppu=mBinding?.etPrice?.text.toString()
                if(moq.isEmpty()) Utility.displayMessage("Please add MOQ",requireContext())
                else if(ppu.isEmpty()) Utility.displayMessage("Please add price per unit",requireContext())
                else if(estId<=0) Utility.displayMessage("Please select estimated days",requireContext())
                else {
                    enqID?.let {
                        if (Utility.checkIfInternetConnected(requireContext())) {
                            mBinding?.txtBidMoq?.setText("Sending MOQ")
                            viewLoader()
                            mEnqVM?.sendMoq(it, additionalInfo, estId, moq.toLong(), ppu)
                        }else {
                            MoqsPredicates.insertMoqForOffline(it, additionalInfo, estId, moq.toLong(), ppu)
                            setDetails()
                        }
                    }
                }
            }
            override fun onSwipe_Reverse(swipeView: Swipe_Button_View) {
                //inactive function
            }
        })

        mBinding?.btnUploadDocReceipt?.setOnClickListener {
            Log.e("RaisePi", "upload : $enqID")
            Log.e("RaisePi", "upload : ${enquiryDetails?.enquiryID}")
            enqID?.let {startActivityForResult(requireContext().piContext(it),ConstantsDirectory.RESULT_PI)}
        }

        mBinding?.viewPiLayout?.setOnClickListener {
            enqID?.let {  startActivity(requireContext().raisePiContext(it,true, SendPiRequest()))
            }
        }
    }

    fun ArtisanProduct(){
        context?.startActivity(context?.addProductIntent(enquiryDetails?.productID?:0))
    }

//    fun BuyerCustomProduct(){
//        context?.start
//    }

    fun setDetails(){
        setTabVisibilities()
        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode
        mBinding?.enquiryStartDate?.text = "Date accepted : ${enquiryDetails?.startedOn?.split("T")?.get(0)}"
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
        }
        else{
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

        mBinding?.enquiryUpdateDate?.text = "Last updated : ${enquiryDetails?.lastUpdated?.split("T")?.get(0)}"
        mBinding?.buyerBrand?.text = enquiryDetails?.ProductBrandName

        setProgressTimeline()

        //TODO implement to enq stage

        if(enquiryDetails?.isMoqRejected!!.equals(1L)){
            mBinding?.uploadDocLayout?.visibility = View.GONE
            mBinding?.viewPiLayout?.visibility = View.GONE
        }else{
            Log.e("PITag","enquiryStageID: ${enquiryDetails?.enquiryStageID}")
            Log.e("PITag","enquiryStageID: ${enquiryDetails?.enquiryStatusID}")
            if(enquiryDetails?.enquiryStageID!!.equals(3L)){
                mBinding?.uploadDocLayout?.visibility = View.GONE
                mBinding?.viewPiLayout?.visibility = View.VISIBLE
              }
            else if(enquiryDetails?.isPiSend!!.equals(1L)){
                mBinding?.uploadDocLayout?.visibility = View.GONE
                mBinding?.viewPiLayout?.visibility = View.VISIBLE
            }else if(enquiryDetails?.isPiSend!!.equals(0L)&&enquiryDetails?.isMoqSend!!.equals(1L)){
                mBinding?.uploadDocLayout?.visibility = View.VISIBLE
                mBinding?.viewPiLayout?.visibility = View.GONE
            }else{
                mBinding?.uploadDocLayout?.visibility = View.GONE
                mBinding?.viewPiLayout?.visibility = View.GONE
            }
        }
        arrayDeliveryDscrp.clear()
        arrayDeliveryDscrp.add("Select")
        moqDeliveryTimeList?.forEach { arrayDeliveryDscrp.add(it.deliveryDesc) }
        val spEstDaysAdapter =ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item,arrayDeliveryDscrp)
        spEstDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spEstDays?.adapter = spEstDaysAdapter

        val moq=MoqsPredicates.getSingleMoq(enqID)
        if(moq!=null){
            mBinding?.etMoq?.setText("${moq?.moq?:""}", TextView.BufferType.EDITABLE)
            mBinding?.etPrice?.setText("${moq?.ppu?:""}", TextView.BufferType.EDITABLE)
            mBinding?.etAddNote?.setText("${moq?.additionalInfo?:""}", TextView.BufferType.EDITABLE)
            mBinding?.spEstDays?.setSelection((moq?.deliveryTimeId?:0).toInt())
            mBinding?.txtFillDetails?.text="MOQ Details"
            mBinding?.txtBidMoq?.visibility=View.GONE
            mBinding?.etMoq?.isEnabled=false
            mBinding?.etPrice?.isEnabled=false
            mBinding?.etAddNote?.isEnabled=false
            mBinding?.spEstDays?.isEnabled=false

        } else{
            mBinding?.txtFillDetails?.text=requireContext().getString(R.string.fill_in_moq_to_bid)
            mBinding?.txtBidMoq?.visibility=View.VISIBLE
            mBinding?.etMoq?.isEnabled=true
            mBinding?.etPrice?.isEnabled=true
            mBinding?.etAddNote?.isEnabled=true
            mBinding?.spEstDays?.isEnabled=true
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

        }
        else{
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
//        mBinding?.artisanOngoEnqDetails?.visibility = View.GONE
        mBinding?.swipeEnquiryDetails?.isRefreshing = true
    }
    fun hideLoader(){
//        mBinding?.artisanOngoEnqDetails?.visibility = View.VISIBLE
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
        enqID?.let { mEnqVM?.getSingleOnEnqData(it) }
        setDetails()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onFailure")
                enqID?.let { mEnqVM.getSingleOnEnqData(it) }
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
                enqID?.let { mEnqVM.getSingleOnEnqData(it) }
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onAddMoqFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Utility.displayMessage("Unable to add MOQ, please try again after some time",requireContext())
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onAddMoqSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Utility.displayMessage("MOQ added succesfully",requireContext())
                hideLoader()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onAddMoqSuccess " + e.message)
        }
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

    companion object {

        fun newInstance(param1: String) =
            ArtisanOnGoEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("PiActivity", "onActivityResult $requestCode")
        Log.e("PiActivity", "onActivityResult $resultCode")
        Log.e("PiActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
//                viewLoader()
                Log.e("PiActivity", "onActivityResult enqID ${enqID}")
                enqID?.let {
//                    mEnqVM.getSingleOngoingEnquiry(it)
                    EnquiryPredicates.updatePiStatus(it)
                    setDetails()
                }
            }
        }
    }

}