package com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter

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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.databinding.FragmentBuyerOnGoEnqDetailsBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.ui.modules.artisan.auth.register.ArtisanRegisterPasswordFragment
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.enquiryPayment
import com.adrosonic.craftexchange.ui.modules.buyer.ownDesign.ownDesignIntent
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.enquiry.ArtEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.dialog_are_you_sure.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerOnGoEnqDetailsFragment : Fragment(),
EnquiryViewModel.FetchEnquiryInterface,
    EnquiryViewModel.BuyersMoqInterface,
    MoqAdapter.MoqListener,
EnquiryViewModel.singlePiInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var piID : Long?= 0
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
    private var status : String ?= ""

    private var isCustom : Boolean ?= false

    var mBinding : FragmentBuyerOnGoEnqDetailsBinding?= null

    val mEnqVM : EnquiryViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""

    var moqDeliveryJson=""
    var moqDeliveryTimeList=ArrayList<Datum>()
    private lateinit var moqAdapter: MoqAdapter
    private lateinit var confirmDialog: Dialog
    var dialogMoq : String ?= ""
    var dialogPpu : String ?= ""
    var dialogBrand : String ?= ""
    var dialogEta =0L

    private lateinit var slideDown:Animation
    private lateinit var slideUp:Animation
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_on_go_enq_details, container, false)
        enqID = param1?.toLong()
//        enqCode = param2
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moqDeliveryJson = UserConfig.shared.moqDeliveryDates
        val gson = GsonBuilder().create()
        val moqDeliveryTime = gson.fromJson(moqDeliveryJson, MoqDeliveryTimesResponse::class.java)
        moqDeliveryTimeList.addAll(moqDeliveryTime.data)
//        mEnqVM.fetchEnqListener = this
        mEnqVM.buyerMoqListener = this
        mEnqVM.singlePiListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            enqID?.let { mEnqVM.getSingleOngoingEnquiry(it) }
            viewLoader()
            mEnqVM.getMoqs(enqID!!)
            mEnqVM?.getSinglePi(enqID!!)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
        }

        enqID?.let {
            mEnqVM.getSingleOnEnqData(it)
                .observe(viewLifecycleOwner, Observer<OngoingEnquiries> {
                    enquiryDetails = it
                })
        }

        slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        mBinding?.moqDetailsLayer?.setOnClickListener {
            handleMoqVisiblities()
        }

        mBinding?.btnMenu?.setOnClickListener {
            if(mBinding?.menuList?.visibility == View.GONE){
                mBinding?.menuList?.visibility = View.VISIBLE
            }else{
                mBinding?.menuList?.visibility = View.GONE
            }
        }

        mBinding?.btnChat?.setOnClickListener {
            Utility?.displayMessage("Coming soon",requireActivity())
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.closeEnquiry?.setOnClickListener {
            enquiryDetails?.enquiryID?.let { it1 -> showDialog(it1) }
        }

        mBinding?.btnUploadTransacReceipt?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                if(piID != 0L){
                    startActivity(context?.enquiryPayment()
                        ?.putExtra(ConstantsDirectory.ENQUIRY_ID,enqID)
                        ?.putExtra("PIID",piID))
                }else{
                    Utility.messageDialog(requireActivity(),"PI not generated")
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if(enquiryDetails?.ProductBrandName != ""){
                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.enquiry_details_container,ArtEnqDetailsFragment.newInstance(enquiryDetails?.enquiryID.toString(),enquiryDetails?.enquiryStatusID.toString(),0))
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                Utility.messageDialog(requireActivity(),"No Artisan Assigned to this enquiry")
            }
        }

        mBinding?.productDetailsLayer?.setOnClickListener {
//           if(enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
//               CustomProduct()
//           }else{
//               CatalogueProduct()
//           }
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
    }

    fun showDialog(enquiryId : Long){
        var dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_are_you_sure)
        dialog.create()
        dialog.show()

        dialog.btn_no?.setOnClickListener {
            dialog.cancel()
        }

        dialog.btn_yes?.setOnClickListener {
            mEnqVM.markEnquiryCompleted(enquiryId)
            dialog.cancel()
            activity?.onBackPressed()
        }
    }


    fun setDetails(){
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                setTabVisibilities()
                setChatIConVisibility()
//                handleMoqVisiblities()
                mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode
                mBinding?.enquiryStartDate?.text =
                    "Date started : ${enquiryDetails?.startedOn?.split("T")?.get(0)}"

                val image = enquiryDetails?.productImages?.split((",").toRegex())
                    ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

                //brand name of product & product Image
                if (enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
                    url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
                    isCustom = true
                } else {
                    url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
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

                //Product name or Product cloth details
                if (enquiryDetails?.productName != "") {
                    mBinding?.productName?.text = enquiryDetails?.productName
                    mBinding?.productNameDetails?.text = enquiryDetails?.productName
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

                    mBinding?.productNameDetails?.text = "Custom Design Product"
                }

                mBinding?.productAmount?.text = "₹ ${enquiryDetails?.totalAmount ?: 0}"

                //enquiry stage with color
                var enquiryStage: String? = ""
                var stagList = Utility?.getEnquiryStagesData()
                Log.e("enqDataStages", "List : $stagList")
                stagList?.forEach {
                    if (it.first == enquiryDetails?.enquiryStageID) {
                        enquiryStage = it.second
                    }
                }
                when (enquiryDetails?.enquiryStageID) {
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

                mBinding?.enquiryUpdateDate?.text =
                    "Last updated : ${enquiryDetails?.lastUpdated?.split("T")?.get(0)}"

                mBinding?.artisanBrand?.text = enquiryDetails?.ProductBrandName

                setProgressTimeline()

                when (enquiryDetails?.enquiryStageID) {
                    3L -> {
                        mBinding?.transactionLayout?.visibility = View.VISIBLE
                    }
                    8L -> {
                        mBinding?.transactionLayout?.visibility = View.VISIBLE
                    }
                    else -> {
                        mBinding?.transactionLayout?.visibility = View.GONE
                    }
                }

                when (enquiryDetails?.productType) {
                    "Product" -> {
                        val moq = MoqsPredicates.getMoqs(enqID)
                        if (moq == null || moq!!.size == 0) {
                            //todo show simple empty view
                            mBinding?.moqDetails?.visibility = View.GONE
                            mBinding?.moqListLayout?.visibility = View.GONE
                            mBinding?.orderTime?.visibility = View.VISIBLE
                            mBinding?.orderTime?.text = "No MOQs Received"
                        }
                        else {
                                if (moq.size == 1 && moq?.get(0)?.accepted == true) {
                                    //todo show product vala view
                                    var moq1 = moq?.get(0)
                                    mBinding?.moqListLayout?.visibility = View.GONE
                                    mBinding?.moqOrderQty?.text = "" + moq1?.moq
                                    mBinding?.orderQuantity?.text = "" + moq1?.moq
                                    mBinding?.moqOrderAmount?.text = "₹ ${moq1?.ppu}"
                                    mBinding?.orderAmount?.text = "₹ ${moq1?.ppu}"
                                    moqDeliveryTimeList?.forEach {
                                        if (it.id.equals(moq1?.deliveryTimeId)) {
                                            mBinding?.moqOrderEta?.text = if (it?.days.equals(0L)) {
                                                "Immediate"
                                            } else "${it?.days} Days"// "${it?.days} Days"
                                            mBinding?.orderTime?.text = if (it?.days.equals(0L)) {
                                                "Immediate"
                                            } else "${it?.days} Days"//"${it?.days} Days"
                                        }
                                    }
                                }
                                else {
                                    mBinding?.orderTime?.text = ""
                                    mBinding?.moqDetails?.visibility = View.GONE
                                    mBinding?.moqListLayout?.visibility = View.VISIBLE
                                    mBinding?.moqList?.layoutManager = LinearLayoutManager(
                                        requireContext(),
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                    moqAdapter =  MoqAdapter(requireContext(), moq, moqDeliveryTimeList)
                                    mBinding?.moqList?.adapter = moqAdapter
                                    moqAdapter.listener = this
                                }

                        }
                    }
                    "Custom Product" -> {
                        val moq = MoqsPredicates.getMoqs(enqID)
                        if (moq == null || moq!!.size == 0) {
                            mBinding?.moqDetails?.visibility = View.GONE
                            mBinding?.moqListLayout?.visibility = View.GONE
                            mBinding?.orderTime?.text = "Awaiting MOQs"
                        }
                        else {
                            if (moq.size == 1 && moq?.get(0)?.accepted == true) {
                                //todo show product vala view
                                var moq1 = moq?.get(0)
                                mBinding?.moqListLayout?.visibility = View.GONE
                                mBinding?.moqOrderQty?.text = "" + moq1?.moq
                                mBinding?.orderQuantity?.text = "" + moq1?.moq
                                mBinding?.moqOrderAmount?.text = "₹ ${moq1?.ppu}"
                                mBinding?.orderAmount?.text = "₹ ${moq1?.ppu}"
                                moqDeliveryTimeList?.forEach {
                                    if (it.id.equals(moq1?.deliveryTimeId)) {
                                        mBinding?.moqOrderEta?.text = if (it?.days.equals(0L)) {
                                            "Immediate"
                                        } else "${it?.days} Days"// "${it?.days} Days"
                                        mBinding?.orderTime?.text = if (it?.days.equals(0L)) {
                                            "Immediate"
                                        } else "${it?.days} Days"//"${it?.days} Days"
                                    }
                                }
                            }
                            else {
                                mBinding?.orderTime?.text = ""
                                mBinding?.moqDetails?.visibility = View.GONE
                                mBinding?.moqListLayout?.visibility = View.VISIBLE
                                mBinding?.moqList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false )
                                moqAdapter = MoqAdapter(requireContext(), moq, moqDeliveryTimeList)
                                mBinding?.moqList?.adapter = moqAdapter
                                moqAdapter.listener = this
                            }
                        }
                    }
                }
            })
        }catch (e:Exception){
            Log.e("ViewEnqProd","Details : "+e.printStackTrace())
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

        if(enquiryDetails?.isBlue == 1L){
            when(currEnqStageId){
                3L -> {
                    mBinding?.awaitingPaymentReceipt?.visibility = View.VISIBLE
                    mBinding?.transactionLayout?.visibility = View.VISIBLE
                }
                8L -> {
                    mBinding?.awaitingPaymentReceipt?.visibility = View.VISIBLE
                    mBinding?.transactionLayout?.visibility = View.VISIBLE
                }
                else ->{
                    mBinding?.awaitingPaymentReceipt?.visibility = View.GONE
                    mBinding?.transactionLayout?.visibility = View.GONE
                }
            }
        }

    }

    private fun setTabVisibilities(){
//        if(enquiryDetails?.isMoqSend == 1L){
//            mBinding?.moqDetailsLayer?.visibility = View.VISIBLE
//        }else{
//            mBinding?.moqDetailsLayer?.visibility = View.GONE
//        }

        if(enquiryDetails?.isPiSend == 1L){
            mBinding?.piDetailsLayer?.visibility = View.VISIBLE
        }else{
            mBinding?.piDetailsLayer?.visibility = View.GONE
        }
    }

    private fun handleMoqVisiblities(){
        when(enquiryDetails?.productType){
        "Product"-> {
            val moq= MoqsPredicates.getMoqs(enqID)
            if(moq==null|| moq?.size==0){
                mBinding?.moqDetails?.visibility = View.GONE
                mBinding?.moqListLayout?.visibility = View.GONE
                mBinding?.orderTime?.text = "No MOQs Received"
            }else {
                val acceptedList = ArrayList<Boolean>()
                moq?.forEach { acceptedList.add(it.accepted ?: false) }
                if (!acceptedList.contains(true)) {
                    mBinding?.moqDetails?.visibility = View.GONE
                    if (mBinding?.moqListLayout?.visibility == View.GONE) {
                        mBinding?.moqListLayout?.animation = slideDown
                        mBinding?.moqListLayout?.visibility = View.VISIBLE
                    } else {
                        mBinding?.moqListLayout?.animation = slideUp
                        mBinding?.moqListLayout?.visibility = View.GONE
                    }
                } else {
                    mBinding?.moqListLayout?.visibility = View.GONE
                    if (mBinding?.moqDetails?.visibility == View.GONE) {
                        mBinding?.moqDetails?.animation = slideDown
                        mBinding?.moqDetails?.visibility = View.VISIBLE

                    } else {
                        mBinding?.moqDetails?.animation = slideUp
                        mBinding?.moqDetails?.visibility = View.GONE
                    }
                }
            }
//            val moq= MoqsPredicates.getMoqs(enqID)
//            val moqId= moq?.moqId?:0
//            mBinding?.moqListLayout?.visibility = View.GONE
//            if(moqId>0) {
//                if (mBinding?.moqDetails?.visibility == View.GONE) {
//                    mBinding?.moqDetails?.animation = slideDown
//                    mBinding?.moqDetails?.visibility = View.VISIBLE
//
//                } else {
//                    mBinding?.moqDetails?.animation = slideUp
//                    mBinding?.moqDetails?.visibility = View.GONE
//                }
//            }
        }
        "Custom Product"-> {
            val moq= MoqsPredicates.getMoqs(enqID)
            if(moq==null|| moq?.size==0){
                mBinding?.moqDetails?.visibility = View.GONE
                mBinding?.moqListLayout?.visibility = View.GONE
                mBinding?.orderTime?.text = "Awaiting MOQs"
            }else {
                val acceptedList = ArrayList<Boolean>()
                moq?.forEach { acceptedList.add(it.accepted ?: false) }
                if (!acceptedList.contains(true)) {
                    mBinding?.moqDetails?.visibility = View.GONE
                    if (mBinding?.moqListLayout?.visibility == View.GONE) {
                        mBinding?.moqListLayout?.animation = slideDown
                        mBinding?.moqListLayout?.visibility = View.VISIBLE

                    } else {
                        mBinding?.moqListLayout?.animation = slideUp
                        mBinding?.moqListLayout?.visibility = View.GONE
                    }
                } else {
                    mBinding?.moqListLayout?.visibility = View.GONE
                    if (mBinding?.moqDetails?.visibility == View.GONE) {
                        mBinding?.moqDetails?.animation = slideDown
                        mBinding?.moqDetails?.visibility = View.VISIBLE

                    } else {
                        mBinding?.moqDetails?.animation = slideUp
                        mBinding?.moqDetails?.visibility = View.GONE
                    }
                }
            }
        }
    }
}

    private fun setChatIConVisibility(){
        if(enquiryDetails?.enquiryStageID!=null){
            if(enquiryDetails?.isBlue == null && enquiryDetails?.enquiryStageID!! >= 4L){
                mBinding?.btnChat?.visibility = View.VISIBLE
                mBinding?.btnMenu?.visibility = View.GONE
            }else{
                mBinding?.btnChat?.visibility = View.GONE
                mBinding?.btnMenu?.visibility = View.VISIBLE
            }
        }
    }

    fun viewLoader(){
        mBinding?.buyerOngoEnqDetails?.visibility = View.GONE
        mBinding?.swipeEnquiryDetails?.isRefreshing = true
    }

    fun hideLoader(){
        mBinding?.buyerOngoEnqDetails?.visibility = View.VISIBLE
        mBinding?.swipeEnquiryDetails?.isRefreshing = false
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

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        if(requestCode == 75){
            if(Utility.checkIfInternetConnected(requireActivity())){
                enqID?.let { mEnqVM.getSingleOngoingEnquiry(it) }
                viewLoader()
                mEnqVM.getMoqs(enqID!!)
                mEnqVM?.getSinglePi(enqID!!)
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
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

    override fun onSendCustomMoqSuccess(moqId:Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Enquiry Details", "onSendCustomMoqSuccess: $moqId")
                if(Utility.checkIfInternetConnected(requireActivity())){
                    enqID?.let { mEnqVM.getSingleOngoingEnquiry(it) }
                    viewLoader()
                    mEnqVM.getMoqs(enqID!!)
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
                    setDetails()
                }
                showSendMoqSuccesDialog(moqId)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onAddMoqSuccess " + e.message)
        }
    }

    override fun onSendCustomMoqFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                setDetails()
                Utility.displayMessage("Unable to send moq, please try again",requireContext())
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onAddMoqSuccess " + e.message)
        }
    }

    override fun onAccepted(artisanId: Long, moqId: Long) {
        confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.dialog_moq_confirmation)
        confirmDialog.show()
        val tvCancel = confirmDialog.findViewById(R.id.txt_cancel) as TextView
        val txt_accept = confirmDialog.findViewById(R.id.txt_accept) as TextView
        val brand_cluster = confirmDialog.findViewById(R.id.brand_cluster) as TextView
        val moq_order_qty = confirmDialog.findViewById(R.id.moq_order_qty) as TextView
        val moq_order_amount = confirmDialog.findViewById(R.id.moq_order_amount) as TextView
        val moq_order_eta = confirmDialog.findViewById(R.id.moq_order_eta) as TextView
        val moq= MoqsPredicates.getSingleMoqByMoqId(moqId)
        moq?.let {
            dialogEta =moq?.deliveryTimeId?:0
            brand_cluster.text="${moq.brand} from ${moq.clusterName}"
            dialogBrand="${moq.brand} from ${moq.clusterName}"
            moq_order_qty.text="${moq.moq} pcs"
            dialogMoq="${moq.moq} pcs"
            moq_order_amount.text="₹ ${moq.ppu}"
            dialogPpu="₹ ${moq.ppu}"
                moqDeliveryTimeList?.forEach {
                    if (it.id.equals(moq?.deliveryTimeId)) {
                        moq_order_eta.text= if(it?.days.equals(0L)){"Immediate"} else "${it?.days} Days"
                    }
                }
        }
        tvCancel.setOnClickListener {
            confirmDialog.dismiss()
        }
        txt_accept.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                viewLoader()
                confirmDialog.dismiss()
                mEnqVM.sendCustomMoqs(enqID?:0,moqId?:0,artisanId)
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }

        }
    }

    override fun viewArtisanProfile(id: Long) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.enquiry_details_container,ArtEnqDetailsFragment.newInstance("","",id))
            ?.addToBackStack(null)
            ?.commit()
    }

    fun showSendMoqSuccesDialog(moqId:Long){
        confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(R.layout.dialog_moq_accept)
        confirmDialog.show()
        val txt_ok = confirmDialog.findViewById(R.id.txt_ok) as TextView
        val txt_goto_chat = confirmDialog.findViewById(R.id.txt_goto_chat) as TextView
        val brand_cluster = confirmDialog.findViewById(R.id.brand_cluster) as TextView
        val moq_order_qty = confirmDialog.findViewById(R.id.moq_order_qty) as TextView
        val moq_order_amount = confirmDialog.findViewById(R.id.moq_order_amount) as TextView
        val moq_order_eta = confirmDialog.findViewById(R.id.moq_order_eta) as TextView
//        val moq= MoqsPredicates.getSingleMoqByMoqId(moqId)
//        moq?.let {
            brand_cluster.text=dialogBrand
            moq_order_qty.text=dialogMoq
            moq_order_amount.text=dialogPpu
            moqDeliveryTimeList?.forEach {
                if (it.id.equals(dialogEta)) {
                    moq_order_eta.text= if(it?.days.equals(0L)){"Immediate"} else "${it?.days} Days"//"${it?.days} Days"
                }
            }
//        }
        txt_ok.setOnClickListener {
            confirmDialog.dismiss()
        }
        txt_goto_chat.setOnClickListener {
        Utility.displayMessage("Coming Soon",requireContext())
        }
    }

    override fun onPiFailure() {
        Utility.displayMessage("PI Failure",requireActivity())
    }

    override fun getPiSuccess(id: Long) {
        if(id != 0L){
            piID = id
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            BuyerOnGoEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

}