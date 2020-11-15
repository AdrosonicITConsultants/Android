package com.adrosonic.craftexchange.ui.modules.order.taxInv

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.icu.text.MessageFormat.format
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.databinding.ActivityTaxInvoiceBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.taxInv.DateeFormat
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiPreviewRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.enquiry.DetailsData
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.ui.modules.pdfViewer.pdfViewerIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.internal.bind.util.ISO8601Utils.format
import java.lang.String.format
import java.text.MessageFormat.format
import java.util.*
import kotlin.collections.ArrayList

fun Context.taxInvoiceIntent(enquiryId:Long): Intent {
    val intent = Intent(this, TaxInvoiceActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}


class TaxInvoiceActivity : LocaleBaseActivity(),
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


    var rrIsActive: Long?= 0
    var rrModifiedon: DateeFormat ?= null
    var rrCreatedon: DateeFormat ?= null
    var rrDate: DateeFormat ?= null

    var rrPiAmt: Long?= 0
    var rrInvoiceNo: String?= ""
    var rrId: Long?= 0
    var rrHSN: Long?= 0
    var rrArtisanId: Long?= 0

    var loadingDialog : Dialog?= null

    var taxInvPrev = SendTiPreviewRequest()
    var taxInv = SendTiRequest()
    var dteFrmt = DateeFormat()
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
        }

        orderDetails = mOrdVM?.loadSingleOrderDetails(enquiryId,0)
        setDetails()

        if(Utility.checkIfInternetConnected(applicationContext)){
            mEnqVM?.fetchPayEnqInvDetails(enquiryId)
            viewFormLoader()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }

        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        loadingDialog = Utility?.loadingDialog(this)

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
                if (qty.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_qty), applicationContext)
                else if (ppu.isEmpty()) Utility.displayMessage(getString(R.string.add_ppu), applicationContext)
                else if (sgst.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_sgst), applicationContext)
                else if (cgst.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_cgst), applicationContext)
                else if (prevTotAmt.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_prev_total), applicationContext)
                else if (advPay.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_paid_adv), applicationContext)
                else if (finAmt.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_final_amt), applicationContext)
                else if (amtToPay.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_amt_tobe_paid), applicationContext)
                else if (delCharge.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_del_charges), applicationContext)
                else if (currency!!.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_currency), applicationContext)
                else if (!mBinding?.chbTnc?.isChecked!!) Utility.displayMessage(getString(R.string.plz_accept_tnc), applicationContext)
                else {
                    //Tax Invoice Preview
                    taxInvPrev?.cgst=cgst?.toLong()
                    taxInvPrev?.ppu=ppu.toLong()
                    taxInvPrev?.quantity=qty.toLong()
                    taxInvPrev?.sgst=sgst.toLong()
                    taxInvPrev?.hsn = rrHSN
                    taxInvPrev?.enquiryId = enquiryId
                    taxInvPrev?.advancePaidAmt = advPay.toLong()
                    taxInvPrev?.deliveryCharges = delCharge.toLong()
                    taxInvPrev?.finalTotalAmt = finAmt.toLong()

                    //Generate Tax Invoice
                    taxInv?.cgst=cgst
                    taxInv?.ppu=ppu.toLong()
                    taxInv?.quantity=qty.toLong()
                    taxInv?.sgst=sgst
                    taxInv?.enquiryId = enquiryId?.toString()
                    taxInv?.advancePaidAmt = advPay
                    taxInv?.deliveryCharges = delCharge
                    taxInv?.finalTotalAmt = finAmt.toLong()

                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        mBinding?.btnSwipeTi?.text = getString(R.string.tax_invoic_gen)
                        mBinding?.btnSwipeTi?.isEnabled=false
//                        viewLoader()
                        loadingDialog?.show()
                        taxInvPrev?.let { it1 -> mOrdVM?.generateTaxInvoicePreview(it1) }
                    } else {
                        Utility.displayMessage(getString(R.string.no_internet_connection),this)
                    }
                }
            }else{
                if (qty.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_qty), applicationContext)
                else if (ppu.isEmpty()) Utility.displayMessage( getString(R.string.add_ppu), applicationContext)
                else if (sgst.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_sgst), applicationContext)
                else if (cgst.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_cgst), applicationContext)
                else if (prevTotAmt.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_prev_total), applicationContext)
                else if (finAmt.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_final_amt), applicationContext)
                else if (delCharge.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_del_charges), applicationContext)
                else if (currency!!.isEmpty()) Utility.displayMessage( getString(R.string.plz_add_currency), applicationContext)
                else if (!mBinding?.chbTnc?.isChecked!!) Utility.displayMessage( getString(R.string.plz_accept_tnc), applicationContext)
                else {

                    //tax invoice preview
                    taxInvPrev?.cgst=cgst.toLong()
                    taxInvPrev?.ppu=ppu.toLong()
                    taxInvPrev?.quantity=qty.toLong()
                    taxInvPrev?.sgst=sgst.toLong()
                    taxInvPrev?.hsn = rrHSN
                    taxInvPrev?.advancePaidAmt = 0
                    taxInvPrev?.enquiryId = enquiryId
                    taxInvPrev?.deliveryCharges = delCharge.toLong()
                    taxInvPrev?.finalTotalAmt = finAmt.toLong()

                    //Generate Tax Invoice
                    taxInv?.cgst=cgst
                    taxInv?.ppu=ppu.toLong()
                    taxInv?.quantity=qty.toLong()
                    taxInv?.sgst=sgst
                    taxInv?.advancePaidAmt = "0"
                    taxInv?.enquiryId = enquiryId?.toString()
                    taxInv?.deliveryCharges = delCharge
                    taxInv?.finalTotalAmt = finAmt.toLong()

                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        mBinding?.btnSwipeTi?.text = getString(R.string.tax_invoic_gen)
                        mBinding?.btnSwipeTi?.isEnabled=false
//                        viewLoader()
                        loadingDialog?.show()
                        taxInvPrev?.let { it1 -> mOrdVM?.generateTaxInvoicePreview(it1) }
                    } else {
                        Utility.displayMessage(getString(R.string.no_internet_connection),this)
                    }
                }
            }
        }

        mBinding?.txtTnc?.setOnClickListener {
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "Terms_conditions")
            startActivity(intent)
//            startActivity(this?.pdfViewerIntent()?.putExtra("ViewType", "Terms_conditions"))
        }
        mBinding?.btnChat?.setOnClickListener {
            enquiryId?.let { startActivity(Intent(this?.chatLogDetailsIntent(it)))}
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
        mBinding?.orderCode?.text=getString(R.string.tax_invoice)+": ${orderDetails?.orderCode}"
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

        rrIsActive = details?.invoice?.isactive
//        rrModifiedon = DateeFormat(date = getDate(details?.invoice?.modifiedon.toLong())?.toLong())
//        rrCreatedon = DateeFormat(date = getDate(details?.invoice?.createdon.toLong())?.toLong())
        rrPiAmt = details?.pi?.totalAmount
        rrInvoiceNo = details?.invoice?.invoiceNo
        rrId = details?.invoice?.id
        rrHSN = details?.pi?.hsn
        rrArtisanId = details?.pi?.artisanId

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


    override fun onGenTaxInvSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                loadingDialog?.cancel()
                Utility.displayMessage("Tax Invoice Preview Generated!",applicationContext)
                startActivityForResult(applicationContext.raiseTaxInvIntent(enquiryId,false,taxInv),ConstantsDirectory.RESULT_TI)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onGenTaxInvFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                loadingDialog?.cancel()
                Utility.displayMessage(getString(R.string.err_gen_ti),applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun onFetchDetailsFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideFormLoader()
                Utility.displayMessage( getString(R.string.unable_tofetch_pi),applicationContext)
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