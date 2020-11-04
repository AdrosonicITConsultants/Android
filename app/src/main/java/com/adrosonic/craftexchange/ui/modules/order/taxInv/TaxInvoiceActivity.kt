package com.adrosonic.craftexchange.ui.modules.order.taxInv

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.predicates.TaxInvPredicates
import com.adrosonic.craftexchange.databinding.ActivityTaxInvoiceBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.enquiry.DetailsData
import com.adrosonic.craftexchange.ui.modules.artisan.landing.PDFViewerActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel

fun Context.taxInvoiceIntent(enquiryId:Long): Intent {
    val intent = Intent(this, TaxInvoiceActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}


class TaxInvoiceActivity : AppCompatActivity(),
    EnquiryViewModel.PayEnqInvInterface,
    OrdersViewModel.GenTaxInvInterface {

    private var mBinding: ActivityTaxInvoiceBinding? = null

    var enquiryId=0L
    private var url : String?=""
    private var status : String?= ""
    private var isCustom : Boolean ?= false
    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var currency : String?= ""
    var currencyList=ArrayList<String>()

    private var orderDetails : Orders?= null
    private var piDetails : PiDetails?= null

    var taxInv = SendTiRequest()
    val mEnqVM : EnquiryViewModel by viewModels()
    val mOrdVM : OrdersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTaxInvoiceBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mEnqVM?.payDetailsListener = this
        mOrdVM?.taxInvGenListener = this

        mBinding?.swipeTaxInvoice?.isEnabled = false

        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            orderDetails = mOrdVM?.loadSingleOrderDetails(enquiryId,0)
            setDetails()

            if(Utility.checkIfInternetConnected(applicationContext)){
                mEnqVM?.fetchPayEnqInvDetails(enquiryId)
                viewFormLoader()
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),this)
//                piDetails = PiPredicates.getSinglePi(enquiryId)
//                if(piDetails!=null){
//                    setPiDetails()
//                }
            }
        }
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }

        mBinding?.btnSwipeTi?.setOnClickListener {
            val qty = mBinding?.orderQuantity?.text.toString()
            val prevTotAmt = mBinding?.etPrevTotalAmt?.text.toString()
            var advPay = mBinding?.etAdvPay?.text.toString()
            var finAmt = mBinding?.etFinalAmt?.text.toString()
            var amtToPay = mBinding?.etAmtToPay?.text.toString()
            var delCharge = mBinding?.etDeliveryCharge?.text.toString()
            val ppu = mBinding?.etRate?.text.toString()
            val sgst = mBinding?.etSgst?.text.toString()
            val cgst = mBinding?.etCgst?.text.toString()
            currency = mBinding?.spCurrency?.selectedItem.toString()

            if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
                if (qty.isEmpty()) Utility.displayMessage("Please add Quantity", applicationContext)
                else if (ppu.isEmpty()) Utility.displayMessage("Please add price per unit", applicationContext)
                else if (sgst.isEmpty()) Utility.displayMessage("Please add SGST", applicationContext)
                else if (cgst.isEmpty()) Utility.displayMessage("Please add CGST", applicationContext)
                else if (prevTotAmt.isEmpty()) Utility.displayMessage("Please add previous total amount", applicationContext)
                else if (advPay.isEmpty()) Utility.displayMessage("Please add paid advance amount", applicationContext)
                else if (finAmt.isEmpty()) Utility.displayMessage("Please add Final amount", applicationContext)
                else if (amtToPay.isEmpty()) Utility.displayMessage("Please add amount to be paid", applicationContext)
                else if (delCharge.isEmpty()) Utility.displayMessage("Please add Delivery Charges", applicationContext)
                else if (currency!!.isEmpty()) Utility.displayMessage("Please select Currency", applicationContext)
                else if (!mBinding?.chbTnc?.isChecked!!) Utility.displayMessage("Please read the terms & conditions", applicationContext)
                else {
                    taxInv.cgst=cgst
                    taxInv.ppu=ppu.toLong()
                    taxInv.quantity=qty.toLong()
                    taxInv.sgst=sgst
                    taxInv.enquiryId = enquiryId?.toString()
                    taxInv.advancePaidAmt = advPay
                    taxInv.deliveryCharges = delCharge
                    taxInv.finalTotalAmt = finAmt?.toLong()

                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        mBinding?.btnSwipeTi?.text = "Tax Invoice preview being generated"
                        mBinding?.btnSwipeTi?.isEnabled=false
                        viewLoader()

                        mOrdVM?.generateTaxInvoice(taxInv)
                    } else {
//                    todo add dat to pi table
                        TaxInvPredicates.insertTiForOffline(enquiryId,1,taxInv)
                        startActivityForResult(applicationContext.raiseTaxInvIntent(enquiryId,true),ConstantsDirectory.RESULT_TI)
                    }
                }
            }else{
                if (qty.isEmpty()) Utility.displayMessage("Please add Quantity", applicationContext)
                else if (ppu.isEmpty()) Utility.displayMessage("Please add price per unit", applicationContext)
                else if (sgst.isEmpty()) Utility.displayMessage("Please add SGST", applicationContext)
                else if (cgst.isEmpty()) Utility.displayMessage("Please add CGST", applicationContext)
                else if (prevTotAmt.isEmpty()) Utility.displayMessage("Please add previous total amount", applicationContext)
                else if (finAmt.isEmpty()) Utility.displayMessage("Please add Final amount", applicationContext)
                else if (delCharge.isEmpty()) Utility.displayMessage("Please add Delivery Charges", applicationContext)
                else if (currency!!.isEmpty()) Utility.displayMessage("Please select Currency", applicationContext)
                else if (!mBinding?.chbTnc?.isChecked!!) Utility.displayMessage("Please read the terms & conditions", applicationContext)
                else {
                    taxInv.cgst=cgst
                    taxInv.ppu=ppu.toLong()
                    taxInv.quantity=qty.toLong()
                    taxInv.sgst=sgst
                    taxInv.enquiryId = enquiryId?.toString()
//                    taxInv.advancePaidAmt = advPay
                    taxInv.deliveryCharges = delCharge
                    taxInv.finalTotalAmt = finAmt?.toLong()

                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        mBinding?.btnSwipeTi?.text = "Tax Invoice preview being generated"
                        mBinding?.btnSwipeTi?.isEnabled=false
                        viewLoader()

                        mOrdVM?.generateTaxInvoice(taxInv)
                    } else {
//                    todo add dat to pi table
                        TaxInvPredicates.insertTiForOffline(enquiryId,1,taxInv)
                        startActivityForResult(applicationContext.raiseTaxInvIntent(enquiryId,true),ConstantsDirectory.RESULT_TI)
                    }
                }
            }
        }

        mBinding?.txtTnc?.setOnClickListener {
            val intent = Intent(this, PDFViewerActivity::class.java)
            intent.putExtra("ViewType", "Terms_conditions")
            startActivity(intent)
        }

    }

    fun viewLoader(){
        mBinding?.taxInvoiceScreen?.visibility= View.GONE
        mBinding?.swipeTaxInvoice?.isRefreshing = true
    }
    fun hideLoader(){
        mBinding?.taxInvoiceScreen?.visibility= View.VISIBLE
        mBinding?.swipeTaxInvoice?.isRefreshing = false
    }

    fun viewFormLoader(){
        mBinding?.middlePart?.visibility= View.GONE
        mBinding?.swipeTaxInvoice?.isRefreshing = true
    }
    fun hideFormLoader(){
        mBinding?.middlePart?.visibility= View.VISIBLE
        mBinding?.swipeTaxInvoice?.isRefreshing = false
    }

    fun setDetails(){
        mBinding?.orderCode?.text="Tax Invoice for ${orderDetails?.orderCode}"
        mBinding?.orderStartDate?.text = orderDetails?.startedOn?.split("T")?.get(0)
        val image = orderDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)
        if(orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
            isCustom = true
        }else{
            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
            isCustom = false
        }
        mBinding?.productImage?.let { ImageSetter.setImage(applicationContext, url!!, it, R.drawable.artisan_logo_placeholder, R.drawable.artisan_logo_placeholder, R.drawable.artisan_logo_placeholder) }
        mBinding?.buyerCompany?.text = orderDetails?.brandName
        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount ?: 0}"
        when(orderDetails?.productStatusId){
            2L -> {
                status = applicationContext?.getString(R.string.in_stock)
                mBinding?.productAvailability?.text = status
                applicationContext?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            1L -> {
                status = applicationContext?.getString(R.string.made_to_order)
                mBinding?.productAvailability?.text = status
                applicationContext?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            else -> {
                status = applicationContext?.getString(R.string.requested_custom_design)
                mBinding?.productAvailability?.text = status
                applicationContext?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
        }
        if(orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
        }else{
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
            fp.setSpan(applicationContext?.let { ContextCompat.getColor(it,R.color.black_text) }?.let {
                ForegroundColorSpan(
                    it
                )
            }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)
        }
        setEnquiryStage()

        currencyList.clear()
        currencyList.add(" ₹")
        currencyList.add("$")
        currencyList.add(" £")
        currencyList.add(" €")
        val spEstDaysAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item,currencyList)
        spEstDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spCurrency?.adapter = spEstDaysAdapter
    }

    fun setEnquiryStage(){
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
                this?.let {
                    ContextCompat.getColor(
                        it, R.color.black_text)
                }?.let { mBinding?.orderStatusText?.setTextColor(it) }

            }

            2L,3L,4L,5L -> {
                this?.let {
                    ContextCompat.getColor(
                        it, R.color.tab_details_selected_text)
                }?.let { mBinding?.orderStatusText?.setTextColor(it) }
            }

            6L,7L,8L,9L,10L -> {
                this?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { mBinding?.orderStatusText?.setTextColor(it) }

            }
        }
        mBinding?.orderStatusText?.text = enquiryStage
    }

    fun setFormFieldsVisibility(){
        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            mBinding?.prevTotAmntLayout?.visibility = View.VISIBLE
            mBinding?.advPaidAmtLayout?.visibility = View.VISIBLE
            mBinding?.amtToPayLayout?.visibility = View.VISIBLE
        }else{
            mBinding?.prevTotAmntLayout?.visibility = View.GONE
            mBinding?.advPaidAmtLayout?.visibility = View.GONE
            mBinding?.amtToPayLayout?.visibility = View.GONE
        }
     }

    fun setPiDetails(details : DetailsData){

        setFormFieldsVisibility()

        var quantity = details?.pi?.quantity
        var prevTotAmt = details?.pi?.totalAmount
        var ppu = details?.pi?.ppu
        var cgst = details?.pi?.cgst
        var sgst = details?.pi?.sgst
        var finAmt = details?.pi?.totalAmount

        if(orderDetails?.productStatusId == AvailableStatus.MADE_TO_ORDER.getId() || orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            var advPayRec = details?.payment?.paidAmount
            mBinding?.etAdvPay?.setText(advPayRec.toString(), TextView.BufferType.EDITABLE)
            var amtToPay = finAmt.minus(advPayRec)
            mBinding?.etAmtToPay?.setText(amtToPay.toString(), TextView.BufferType.EDITABLE)
        }

        mBinding?.orderQuantity?.setText(quantity.toString(), TextView.BufferType.EDITABLE)
        mBinding?.etPrevTotalAmt?.setText(prevTotAmt.toString(), TextView.BufferType.EDITABLE)

        mBinding?.etRate?.setText(ppu.toString(), TextView.BufferType.EDITABLE)
        mBinding?.etCgst?.setText(cgst.toString(), TextView.BufferType.EDITABLE)
        mBinding?.etSgst?.setText(sgst.toString(), TextView.BufferType.EDITABLE)
        mBinding?.etFinalAmt?.setText(finAmt.toString(), TextView.BufferType.EDITABLE)

//        mBinding?.orderQuantity?.setText(piDetails?.quantity?.toString() ?: "", TextView.BufferType.EDITABLE)
//
//        mBinding?.etPrevTotalAmt?.setText(prevTotalAmt , TextView.BufferType.EDITABLE)
//        mBinding?.etFinalAmt?.setText(totalAmt, TextView.BufferType.EDITABLE)
//
//        mBinding?.etRate?.setText(piDetails?.ppu?.toString() ?: "", TextView.BufferType.EDITABLE)
//        mBinding?.etCgst?.setText(piDetails?.cgst?.toString() ?: "", TextView.BufferType.EDITABLE)
//        mBinding?.etSgst?.setText(piDetails?.sgst?.toString() ?: "", TextView.BufferType.EDITABLE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TaxInvoiceActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_TI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

//    override fun onPiFailure() {
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                hideLoader()
//                piDetails = PiPredicates.getSinglePi(enquiryId)
//                if(piDetails!=null){
//                    setPiDetails()
//                }
//                Utility.displayMessage("Sorry,Unable to fetch PI details",applicationContext)
//            })
//        } catch (e: Exception) {
//            Log.e("Enquiry Details", "Exception onFailure " + e.message)
//        }
//    }
//
//    override fun getPiSuccess(id: Long) {
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                hideLoader()
//                setPiDetails()
//            })
//        } catch (e: Exception) {
//            Log.e("Enquiry Details", "Exception onFailure " + e.message)
//        }
//    }

    override fun onGenTaxInvSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage("Tax Invoice sent!",applicationContext)
                startActivityForResult(applicationContext.raiseTaxInvIntent(enquiryId,true),ConstantsDirectory.RESULT_TI)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onGenTaxInvFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage("Error generating Tax Invoice",applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onFetchDetailsFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideFormLoader()
                Utility.displayMessage("Sorry,Unable to fetch PI details",applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onFetchDetailsSuccess(details: DetailsData) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideFormLoader()
                setPiDetails(details)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onSuccess " + e.message)
        }
    }
}