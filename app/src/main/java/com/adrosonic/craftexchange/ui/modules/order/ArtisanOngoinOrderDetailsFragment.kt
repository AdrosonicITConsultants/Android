package com.adrosonic.craftexchange.ui.modules.order

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
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
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OrderProgressDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanOngoingOrderDetailsBinding
import com.adrosonic.craftexchange.enums.*
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.modules.artisan.deliveryReceipt.uploadDeliveryReceiptIntent
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.piContext
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchange.ui.modules.artisan.qcForm.qcFormIntent
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.enquiry.BuyEnqDetailsFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.ui.modules.enquiry.viewEnqDetails
import com.adrosonic.craftexchange.ui.modules.order.cr.crContext
import com.adrosonic.craftexchange.ui.modules.order.finalPay.orderPaymentIntent
import com.adrosonic.craftexchange.ui.modules.order.revisePi.viewPiContextPostCr
import com.adrosonic.craftexchange.ui.modules.order.revisedAdvPayment.orderPayment
import com.adrosonic.craftexchange.ui.modules.order.taxInv.raiseTaxInvIntent
import com.adrosonic.craftexchange.ui.modules.order.taxInv.taxInvoiceIntent
import com.adrosonic.craftexchange.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchange.ui.modules.raiseConcern.raiseConcernIntent
import com.adrosonic.craftexchange.ui.modules.transaction.adapter.OnGoingTransactionRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.transaction.viewDocument
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.QCViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.dialog_cr_toggle_confirm.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanOngoinOrderDetailsFragment : Fragment(),
    OrdersViewModel.FetchOrderInterface,
    OrdersViewModel.changeStatusInterface,
    OrdersViewModel.ToggleChangeInterface,
    TransactionViewModel.TransactionInterface,
    OrdersViewModel.GetOrderProgressInterface,
    OrdersViewModel.RecreationDispatchInterface{

    private var param1: String? = null
    private var param2: String? = null

    private var enqID : Long ?= 0
    private var enqCode : String ?= ""
    private var orderDetails : Orders ?= null
    private var orderProgressDetails : OrderProgressDetails ?= null
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
    val mQcVM : QCViewModel by viewModels()

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var isCustom : Boolean ?= false
    private var isStageCompleted : Boolean ?= false

    private var loadDialog : Dialog?= null


    var moqDeliveryTimeList=ArrayList<Datum>()
    var mBinding : FragmentArtisanOngoingOrderDetailsBinding?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_ongoing_order_details, container, false)
        enqID = param1?.toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utility.getDeliveryTimeList()?.let {moqDeliveryTimeList.addAll(it)  }
        mOrderVm?.fetchEnqListener = this
        mOrderVm?.changeStatusListener = this
        mOrderVm?.toggleListener = this
        mOrderVm?.getOrderProgressListener = this
        mOrderVm?.recreationDispatchListener = this
//        mTranVM?.transactionListener = this
        mBinding?.swipeEnquiryDetails?.isEnabled = false
        if(Utility.checkIfInternetConnected(requireActivity())){
            viewLoader()
           reloadContent()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            //to set raise concern layer visibilty (different table created)
            orderProgressDetails = enqID?.let { mOrderVm?.loadOrderProgressDetails(it) }
            if(orderProgressDetails!=null){
                orderProgressDetails?.let { setRaiseConcernLayerVisibilty(it) }
            }
        }

        loadDialog = Utility.loadingDialog(requireActivity())

        enqID?.let {
            mOrderVm.getSingleOnOrderData(it,0)
                .observe(viewLifecycleOwner, Observer<Orders> {
                    orderDetails = it
                })
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnOrderDispatchedRecreation?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadDialog?.show()
                enqID?.let { it1 -> mOrderVm?.markOrderDispatchedAfterRecreation(it1) }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }

        mBinding?.btnOrderRecreation?.setOnClickListener {
            showConfirmDialog()
        }

        //Raise Concern
        mBinding?.raiseConcernLayer?.setOnClickListener {
            startActivity(enqID?.let { it1 -> requireContext().raiseConcernIntent(it1,false) })
        }

        mBinding?.btnUploadDeliveryReceipt?.setOnClickListener{
            startActivity(enqID?.let { it1 ->
                requireContext().uploadDeliveryReceiptIntent(it1,OrderStatus.ONGOING.getId())
            })
        }

        mBinding?.btnUploadTaxInv?.setOnClickListener {
            enqID?.let {startActivityForResult(requireContext().taxInvoiceIntent(it),ConstantsDirectory.RESULT_TI)}
        }

        mBinding?.brandDetailsLayer?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_details_container,
                        BuyEnqDetailsFragment.newInstance(orderDetails?.enquiryId.toString(),orderDetails?.enquiryStatusId.toString()))
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
            enqID?.let {startActivityForResult(requireContext().viewPiContextPostCr(it),ConstantsDirectory.RESULT_PI)}
        }

        mBinding?.viewPaymentLayer?.setOnClickListener {
            if(mBinding?.transactionList!!.visibility==View.VISIBLE) mBinding?.transactionList!!.visibility=View.GONE
            else mBinding?.transactionList!!.visibility=View.VISIBLE
        }
        mBinding?.btnMenu?.setOnClickListener {
            if(mBinding?.menuList!!.visibility==View.VISIBLE) mBinding?.menuList!!.visibility=View.GONE
            else mBinding?.menuList!!.visibility=View.VISIBLE
        }
        mBinding?.toogleCr?.setOnClickListener {
        if(orderDetails?.changeRequestStatus==null){
                var dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.dialog_cr_toggle_confirm)
                dialog.create()
                dialog.show()
                dialog.btn_cancel?.setOnClickListener {
                    dialog.cancel()
                    mBinding?.toogleCr?.isChecked=true
                    mBinding?.menuList!!.visibility=View.GONE
                }
                dialog.btn_ok?.setOnClickListener {
                    enqID?.let {
                        if(Utility.checkIfInternetConnected(requireContext())) {
                            viewLoader()
                            mOrderVm.setCrToggle(enqID ?: 0)
                        }else{
                            OrdersPredicates.updateCrStatusForOffline(enqID)
                            mBinding?.toogleCr?.isEnabled=false
                            mBinding?.toogleCr?.isChecked=false
                            mBinding?.toogleCr?.text=getString(R.string.cr_disabled)
                            mBinding?.menuList?.visibility=View.GONE
                        }
                        dialog.cancel()
                    }
                }
            }
        else{
            mBinding?.toogleCr?.isChecked=true
        }
        }
        mBinding?.qualityCheckLayer?.setOnClickListener {
            startActivity(context?.qcFormIntent()
                ?.putExtra(ConstantsDirectory.ENQUIRY_ID,enqID)
                ?.putExtra(ConstantsDirectory.ORDER_STATUS_FLAG, 0L))
        }

        //delivery receipt
        mBinding?.deliveryReceiptLayer?.setOnClickListener {
            startActivity(enqID?.let { it1 -> requireContext()?.viewDocument(it1,DocumentType.DELIVERY_CHALLAN.getId()) })
        }

        mBinding?.changeRequestLayer?.setOnClickListener {
            if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType.equals(ConstantsDirectory.CUSTOM_PRODUCT)) {
                if (orderDetails?.changeRequestOn == 1L) {
                    when (orderDetails?.changeRequestStatus) {
                        0L ->  enqID?.let {startActivityForResult(requireContext().crContext(it,0L),ConstantsDirectory.RESULT_PI)  }
                        1L ->  enqID?.let { startActivityForResult(requireContext().crContext(it,1L),ConstantsDirectory.RESULT_PI) }
                        2L ->  enqID?.let { startActivityForResult(requireContext().crContext(it,2L),ConstantsDirectory.RESULT_PI) }
                        3L ->  enqID?.let { startActivityForResult(requireContext().crContext(it,3L),ConstantsDirectory.RESULT_PI) }
                        else -> {
//                            else enqID?.let { startActivity(requireActivity().crContext(it, 4L)) }
                        }
                    }
                }
                else Utility.displayMessage(getString(R.string.cr_disabled), requireContext())
            } else Utility.displayMessage(getString(R.string.cr_not_applicable), requireContext())

        }
        mBinding?.taxInvoiceLayer?.setOnClickListener {
            enqID?.let {  startActivity(requireContext().raiseTaxInvIntent(it,true, SendTiRequest())) }
        }

        //mark order dispatch button
        mBinding?.btnMarkOrderDispatched?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadDialog?.show()
                orderDetails?.enquiryId?.let { it1 -> mOrderVm?.setCompleteOrderStage(it1,EnquiryStages.ORDER_DISPATCHED.getId()) }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }
        //ChangeEnquiryStageButtons
        mBinding?.btnStartEnqStage?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadDialog?.show()
                orderDetails?.enquiryId?.let { it1 ->
                    mOrderVm?.setEnquiryStage(it1, EnquiryStages.PRODUCTION_COMPLETED.getId(), InnerEnquiryStages.YARN_PROCURED.getId())
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }
        mBinding?.btnMarkInprogress?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadDialog?.show()
                orderDetails?.enquiryId?.let { it1 ->
                    orderDetails?.innerEnquiryStageId?.let { it2 ->
                        mOrderVm?.setEnquiryStage(it1, EnquiryStages.PRODUCTION_COMPLETED.getId(),it2 )
                    }
                }
                isStageCompleted = false
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }
        mBinding?.btnMarkMoveNextStage?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                loadDialog?.show()
                when(orderDetails?.innerEnquiryStageId){
                    InnerEnquiryStages.POST_LOOM_PROCESS.getId() -> {
                        orderDetails?.enquiryId?.let { it1 -> mOrderVm?.setCompleteOrderStage(it1,
                            EnquiryStages.COMPLETION_OF_ORDER.getId()) }
                    }
                    else -> {
                        orderDetails?.enquiryId?.let { it1 -> mOrderVm?.setEnquiryStage(it1,
                            EnquiryStages.PRODUCTION_COMPLETED.getId(),orderDetails?.innerEnquiryStageId?.plus(1)) }
                    }
                }
                isStageCompleted = true
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }
        mBinding?.btnChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it)))}
        }
        mBinding?.chat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it)))}
        }
        mBinding?.viewEnqLayer?.setOnClickListener {
            enqID?.let {
                startActivity(Intent(requireContext()?.viewEnqDetails( requireContext(),it.toString(),"2")))
            }
        }
        mBinding?.btnViewApproveRevAdvPayment?.setOnClickListener {
            enqID?.let {
                startActivity(context?.orderPayment()
                        ?.putExtra(ConstantsDirectory.ENQUIRY_ID, it)
                        ?.putExtra(ConstantsDirectory.ORDER_STATUS_FLAG, 0L)
                        ?.putExtra(ConstantsDirectory.PI_ID, 0))
            }
        }
        mBinding?.btnViewApprovePayment?.setOnClickListener {
            startActivity(requireContext().orderPaymentIntent()
                ?.putExtra(ConstantsDirectory.ENQUIRY_ID,enqID)
                ?.putExtra(ConstantsDirectory.ENQUIRY_STATUS_FLAG,OrderStatus.ONGOING.getId()))
        }
    }

    fun showConfirmDialog() {
        var cDialog = Dialog(requireContext())
        cDialog.setContentView(R.layout.dialog_confirm_recreate)
        cDialog.show()
        cDialog.show()
        val confirmRecreate = cDialog.findViewById(R.id.btn_confirm_recreate) as Button
        confirmRecreate.setOnClickListener {
            cDialog.cancel()
            if(Utility?.checkIfInternetConnected(requireContext())){
                loadDialog?.show()
                enqID?.let { it1 -> mOrderVm?.recreateOrder(it1) }
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }
    }


    fun reloadContent(){
        enqID?.let {
            mOrderVm.ratingData(enqID!!,  UserConfig.shared.userId?.toLong()?:0)
            mOrderVm.getSingleOngoingOrder(it)
            mTranVM.getSingleOngoingTransactions(it)
            mOrderVm?.getChangeRequestDetails(it)
            mQcVM.getArtisanQCResponse(it)
            mOrderVm.getOrderProgressDetails(it)
        }
    }

    fun setDetails(){
    try {
        Handler(Looper.getMainLooper()).post(Runnable {
        setTabVisibilities()
        setActionButtonVisibilites()
        setViewEnquiryStageChangeButton()
        viewChangeStatusLayer()
        setToggleVisiblity()
        mBinding?.orderCode?.text = orderDetails?.orderCode
        mBinding?.enquiryStartDate?.text = "Date accepted : ${orderDetails?.startedOn?.split("T")?.get(0)}"
        val image = orderDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        //brand name of product & product Image
        if(orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
            isCustom = true
        }else{
            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
            isCustom = false
        }
        mBinding?.productImage?.let { ImageSetter.setImage(requireActivity(),
            url!!, it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder) }

        mBinding?.buyerCompany?.text = orderDetails?.companyName

        //ProductAvailability
        when(orderDetails?.productStatusId){
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
        if(orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
            mBinding?.productNameDetails?.text = orderDetails?.productName
        }
        else{
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if(it.first == orderDetails?.weftYarnId){
                    weft = it.second
                }
                if(it.first == orderDetails?.warpYarnId){
                    warp = it.second
                }
                if(it.first == orderDetails?.extraWeftYarnId){
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if(it.first == orderDetails?.productCategoryId){
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

        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount ?: 0}"

        //enquiry stage with color
        var enquiryStage : String ?= ""
        var stagList = Utility?.getEnquiryStagesData()
        Log.e("enqDataStages", "List : $stagList")
        stagList?.forEach {
            if(it.first == orderDetails?.enquiryStageId){
                enquiryStage = it.second
            }
        }
        when(orderDetails?.enquiryStageId){
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

        mBinding?.enquiryUpdateDate?.text = "Last updated : ${orderDetails?.lastUpdated?.split("T")?.get(0)}"
        mBinding?.buyerBrand?.text = orderDetails?.companyName

        setProgressTimeline()
        var tranList = TransactionPredicates.getTransactionByEnquiryId(enqID?:0,false)
                if(tranList!!.size>0){
                    mBinding?.viewTransaction?.text=getString(R.string.view)
                    mBinding?.transactionList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false )
                    val transactionAdapter =  OnGoingTransactionRecyclerAdapter(requireContext(), tranList)
                    mBinding?.transactionList?.adapter = transactionAdapter
//                    transactionAdapter.listener = this
                } else {
                    mBinding?.viewTransaction?.text=getString(R.string.no_transaction)
                }
        })
        //cr
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType.equals(ConstantsDirectory.CUSTOM_PRODUCT)){
            if(orderDetails?.changeRequestOn==1L)
            {
                when(orderDetails?.changeRequestStatus) {
                    0L -> {
                        mBinding?.toogleCr?.isEnabled=false
                        mBinding?.txtCrLayerStatus?.text = "Buyer Waiting for acknwoledgement"
                    }
                    1L,2L,3L -> {
                        mBinding?.toogleCr?.isEnabled=false
                        mBinding?.txtCrLayerStatus?.text = Utility.getCountStatement(enqID?:0)//Utility.returnDisplayDate(orderDetails?.changeRequestModifiedOn ?: "")
                    }
                    null->{
                        mBinding?.txtCrLayerStatus?.text = "Change request not available"
                    }
                    else -> {
                         mBinding?.txtCrLayerStatus?.text = "Change request not available"
                    }
                }
            }
            else  mBinding?.txtCrLayerStatus?.text=getString(R.string.cr_disabled)
        }
        else {
            mBinding?.txtCrLayerStatus?.text=getString(R.string.cr_not_applicable)
        }
        if(orderDetails?.enquiryStageId!!>5L) {
            mBinding?.toogleCr?.isClickable=false
            mBinding?.toogleCr?.isEnabled=false
            mBinding?.toogleCr?.isChecked=false
            mBinding?.toogleCr?.text="Change request"
         }
        else{
            mBinding?.toogleCr?.isEnabled=true
        }

        if(orderDetails?.revisedAdvancePaymentId!!.equals(3L))mBinding?.btnViewApproveRevAdvPayment?.visibility = View.VISIBLE
        else mBinding?.btnViewApproveRevAdvPayment?.visibility = View.GONE
        } catch (e: Exception) {
            Log.e("setDetails", "Exception " + e.message)
        }
    }


    private fun setProgressTimeline(){
        stageAPList?.clear()
        innerStageList?.clear()
        stageList?.clear()

        enqID?.let { orderDetails=mOrderVm.getSingleOnOrderData(it,0).value }

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

        if(nextEnqStage != ""){
            mBinding?.nextEnqStageLayout?.visibility = View.VISIBLE
            mBinding?.nextStep?.text = nextEnqStage
            mBinding?.nextStepArrowText?.text=nextEnqStage
        }else {
            mBinding?.nextEnqStageLayout?.visibility = View.GONE
        }

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

    private  fun setToggleVisiblity(){
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            Log.e("ToggleCr","111111111111111111")
            mBinding?.toogleCr?.visibility=View.VISIBLE
            if(orderDetails?.changeRequestOn!!>0 && orderDetails?.actionMarkCr!!<1L){
                Log.e("ToggleCr","22222222222")
                mBinding?.toogleCr?.isEnabled=true
                mBinding?.toogleCr?.isChecked=true
                mBinding?.toogleCr?.text=getString(R.string.cr_enabled)
                if(orderDetails?.enquiryStageId!!>5L) {
                    Log.e("ToggleCr","3333333")
                    mBinding?.toogleCr?.isClickable=false
                    mBinding?.toogleCr?.isEnabled=false
                    mBinding?.toogleCr?.isChecked=false
                    mBinding?.toogleCr?.text="Change request"
                    mBinding?.toogleCr?.setTextColor(requireContext().getColor(R.color.darker_gray))
                }
            }
            else{
                Log.e("ToggleCr","3333333333333")
                mBinding?.toogleCr?.isEnabled=false
                mBinding?.toogleCr?.isChecked=false
                mBinding?.toogleCr?.text=getString(R.string.cr_disabled)
            }
        }
        else {
            Log.e("ToggleCr","4444444444444444")
            mBinding?.toogleCr?.visibility=View.GONE
        }

    }

    private fun setTabVisibilities(){
        //advance payment
        orderDetails?.enquiryStageId?.let {
            if (orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
                if (orderDetails?.enquiryStageId!! >= 4L) {
                    mBinding?.viewPaymentLayer?.visibility = View.VISIBLE
                } else {
                    mBinding?.viewPaymentLayer?.visibility = View.GONE
                }
            } else {
                mBinding?.viewPaymentLayer?.visibility = View.GONE
            }

            //QcForm
            if (orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
                if (orderDetails?.enquiryStageId!! >= EnquiryStages.ADVANCE_PAYMENT_RECEIVED.getId()) {
                    mBinding?.qualityCheckLayer?.visibility = View.VISIBLE
                } else {
                    mBinding?.qualityCheckLayer?.visibility = View.GONE
                }
            } else {
                if (orderDetails?.enquiryStageId!! >= EnquiryStages.PI_FINALIZED.getId()) {
                    mBinding?.qualityCheckLayer?.visibility = View.VISIBLE
                } else {
                    mBinding?.qualityCheckLayer?.visibility = View.GONE
                }
            }
            //TaxInvoice
            if (orderDetails?.enquiryStageId!! >= EnquiryStages.FINAL_INVOICE_RAISED.getId()) {
                mBinding?.taxInvoiceLayer?.visibility = View.VISIBLE
            } else {
                mBinding?.taxInvoiceLayer?.visibility = View.GONE
            }

            //DeliveryReceipt
            if (orderDetails?.enquiryStageId!! >= EnquiryStages.FINAL_PAYMENT_RECEIVED.getId() && orderDetails?.deliveryChallanUploaded == 1L) {
                mBinding?.deliveryReceiptLayer?.visibility = View.VISIBLE
            } else {
                mBinding?.deliveryReceiptLayer?.visibility = View.GONE
            }
        }
    }

    fun setActionButtonVisibilites(){
        //upload tax invoice
        Log.e("Revpay","Stage: ${orderDetails?.enquiryStageId}")
        Log.e("Revpay","revId: ${orderDetails?.revisedAdvancePaymentId}")
        when(orderDetails?.enquiryStageId){
            EnquiryStages.PI_FINALIZED.getId(),
            EnquiryStages.ADVANCE_PAYMENT_RECEIVED.getId(),
            EnquiryStages.PRODUCTION_COMPLETED.getId(),
            EnquiryStages.QUALITY_CHECK_BEFORE_DELIVERY.getId(),
            EnquiryStages.COMPLETION_OF_ORDER.getId() -> {
                if( orderDetails?.revisedAdvancePaymentId!!.equals(0L)||
                   orderDetails?.revisedAdvancePaymentId!!.equals(1L)||
                   orderDetails?.revisedAdvancePaymentId!!.equals(4L)){
                    mBinding?.uploadTaxInvLayout?.visibility = View.VISIBLE
                    Log.e("Revpay","tax inv visible")
                }
                else  {
                    mBinding?.uploadTaxInvLayout?.visibility = View.GONE
                    Log.e("Revpay","tax inv gone")
                }
            }
            else -> {
                mBinding?.uploadTaxInvLayout?.visibility = View.GONE
                Log.e("Revpay","tax inv gone")
            }
        }

        //Approve Final Payment
        if(orderDetails?.enquiryStageId == EnquiryStages.FINAL_INVOICE_RAISED.getId() && orderDetails?.isBlue == 1L){
            mBinding?.btnViewApprovePayment?.visibility = View.VISIBLE
        }else{
            mBinding?.btnViewApprovePayment?.visibility = View.GONE
        }

        //Upload Delivery Receipt
        if(orderDetails?.enquiryStageId!! >= EnquiryStages.FINAL_PAYMENT_RECEIVED.getId() && orderDetails?.deliveryChallanUploaded == 0L){
            mBinding?.uploadDeliveryReceiptLayout?.visibility = View.VISIBLE
        }else{
            mBinding?.uploadDeliveryReceiptLayout?.visibility = View.GONE
        }

        //Mark Order Dispatch Button
        if(orderDetails?.enquiryStageId == EnquiryStages.FINAL_PAYMENT_RECEIVED.getId() /*&& orderDetails?.deliveryChallanUploaded == 1L*/){
            mBinding?.btnMarkOrderDispatched?.visibility = View.VISIBLE
        }else{
            mBinding?.btnMarkOrderDispatched?.visibility = View.GONE
        }

        //Mark Order Dispatch
        if(orderDetails?.enquiryStageId == EnquiryStages.FINAL_PAYMENT_RECEIVED.getId()){
            mBinding?.btnMarkOrderDispatched?.visibility = View.VISIBLE
        }else{
            mBinding?.btnMarkOrderDispatched?.visibility = View.GONE
        }

        //Order Recreation
        if(orderDetails?.artisanReviewId == 2L && orderDetails?.isReprocess == 0L){
            mBinding?.btnOrderRecreation?.visibility = View.VISIBLE
        }else{
            mBinding?.btnOrderRecreation?.visibility = View.GONE
        }

        //mark order after recreation
        if(orderDetails?.isReprocess == 1L){
            mBinding?.orderDispatchRecreationLayer?.visibility = View.VISIBLE
        }else{
            mBinding?.orderDispatchRecreationLayer?.visibility = View.GONE
        }
    }

    fun setRaiseConcernLayerVisibilty(details: OrderProgressDetails){
        //Raise Concern
        if(orderDetails?.enquiryStageId!! >= EnquiryStages.ORDER_DISPATCHED.getId() && details?.isFaulty == 1L){
            mBinding?.raiseConcernLayer?.visibility = View.VISIBLE
        }else{
            mBinding?.raiseConcernLayer?.visibility = View.GONE
        }
    }

    fun setViewEnquiryStageChangeButton(){
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            if(orderDetails?.enquiryStageId == 4L && orderDetails?.innerEnquiryStageId == null){
                mBinding?.startEnqStageLayout?.visibility = View.VISIBLE
            }else{
                mBinding?.startEnqStageLayout?.visibility = View.GONE
            }
        }else{
            mBinding?.startEnqStageLayout?.visibility = View.GONE
        }
    }

    fun viewChangeStatusLayer(){
        Log.e("OrderDetails","enquiryStageId 222: ${orderDetails?.enquiryStageId}")
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            if(orderDetails?.enquiryStageId == 5L){
                mBinding?.changeEnquiryStatusLayout?.visibility = View.VISIBLE
            }else{
                mBinding?.changeEnquiryStatusLayout?.visibility = View.GONE
            }
        }else{
            mBinding?.changeEnquiryStatusLayout?.visibility = View.GONE
        }
    }

    fun qcDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_qc_form)
        dialog.show()
        val btn = dialog.findViewById(R.id.btn_fill_qc) as Button
        var btn_skip = dialog.findViewById(R.id.text_skip) as TextView
        btn.setOnClickListener {
            dialog.cancel()
            startActivity(context?.qcFormIntent()?.putExtra(ConstantsDirectory.ENQUIRY_ID,enqID))
        }
        btn_skip.setOnClickListener {
            dialog.cancel()
         }
    }

    override fun onResume() {
        super.onResume()
        if(Utility.checkIfInternetConnected(requireActivity())){
            viewLoader()
           reloadContent()

        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            setDetails()
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OrderDetails", "onFailure")
                loadDialog?.cancel()
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
                loadDialog?.cancel()
                setDetails()
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onFailure " + e.message)
        }
    }



    override fun onStatusChangeSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                enqID?.let { mOrderVm.getSingleOngoingOrder(it) }
                if(isStageCompleted == true){
                    qcDialog()
                    Utility.displayMessage("Order Stage Updated!",requireActivity())
                }else{
                    Utility.displayMessage("Order Stage In Progress!",requireActivity())
                }
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onStatusChangeSuccess " + e.message)
        }
    }

    override fun onStatusChangeFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                setDetails()
                loadDialog?.cancel()
                Utility.displayMessage(getString(R.string.plz_try_again),requireActivity())
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

    override fun onToggleSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Toggle","onToggleSuccess")
                Utility.displayMessage(getString(R.string.cr_disabled),requireContext())
                hideLoader()
                mBinding?.toogleCr?.isEnabled=false
                mBinding?.toogleCr?.isChecked=false
                mBinding?.toogleCr?.text=getString(R.string.cr_disabled)
                mBinding?.menuList?.visibility=View.GONE
            })
        } catch (e: Exception) {
            Log.e("Toggle", "Exception onStatusChangeFailure " + e.message)
        }
    }

    override fun onToggleFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Toggle","onToggleFailure")
                mBinding?.menuList?.visibility=View.GONE
                hideLoader()
                Utility.displayMessage(getString(R.string.err_cr_status),requireContext())

            })
        } catch (e: Exception) {
            Log.e("Toggle", "Exception onToggleFailure " + e.message)
        }
    }

    override fun onOPFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {

            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    override fun onOPSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
               orderProgressDetails = enqID?.let { mOrderVm?.loadOrderProgressDetails(it) }
                if(orderProgressDetails!=null){
                    orderProgressDetails?.let { setRaiseConcernLayerVisibilty(it) }
                }
            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    override fun onRDFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Utility.displayMessage(getString(R.string.err_recreation_status),requireContext())
                loadDialog?.cancel()
            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    override fun onRDSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                if(Utility?.checkIfInternetConnected(requireContext())){
                    reloadContent()
                }else{
                    Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
                }
            })
        } catch (e: Exception) {
            Log.e("OrderProgress", "Exception onFailure " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ArtOngonDetails", "RESULT_OK ${Activity.RESULT_OK}")
        Log.e("ArtOngonDetails", "resultCode ${resultCode}")
        Log.e("ArtOngonDetails", "requestCode ${requestCode}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            Log.e("ArtOngonDetails", "if ${enqID}")
            if (resultCode == Activity.RESULT_OK) {
                enqID?.let {
                    viewLoader()
                    mOrderVm?.getSingleOngoingOrder(it)
                }
            }
        }
        orderDetails= mOrderVm?.getSingleOnOrderData(enqID?:0,0).value
        orderDetails?.let{setDetails()}
    }

    companion object {

        fun newInstance(param1: String) =
            ArtisanOngoinOrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }



}