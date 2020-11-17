package com.adrosonic.craftexchange.ui.modules.order.revisePi

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.databinding.ActivityPiBinding
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import java.util.*
import kotlin.collections.ArrayList



fun Context.revisePiContext(enquiryId:Long): Intent {
    val intent = Intent(this, RevisePiActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}
class RevisePiActivity : LocaleBaseActivity(),
    EnquiryViewModel.RevisePiInterface,
    EnquiryViewModel.singlePiInterface {
    var enquiryId=0L
    val mEnqVM : EnquiryViewModel by viewModels()
    var enquiryDetails: OngoingEnquiries? = null
    var moqDeliveryTimeList=ArrayList<Datum>()
    private var url : String?=""
    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    private var isCustom : Boolean ?= false
    private var status : String?= ""
    private var currency : String?= ""
    var currencyList=ArrayList<String>()
    var pi=SendPiRequest()
    private var mBinding: ActivityPiBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pi)
        val view = mBinding?.root
        setContentView(view)
        mEnqVM?.singlePiListener=this
        mEnqVM?.revisePiInterface=this
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            enquiryDetails = mEnqVM?.loadSingleEnqDetails(enquiryId)
            Utility.getDeliveryTimeList()?.let {moqDeliveryTimeList.addAll(it)  }
            mEnqVM?.getSinglePi(enquiryId)
            setDetails()
        }

        Log.e("RaisePi", "pi enquiryId : ${enquiryId}")
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        mBinding?.etDeliveryDate?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.etDeliveryDate?.setText( year.toString() + "-" + (monthOfYear + 1) + "-" +dayOfMonth.toString() , TextView.BufferType.EDITABLE )
                }, mYear, mMonth, mDay)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            datePickerDialog.show()
        }
        mBinding?.imgDate?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.etDeliveryDate?.setText( year.toString() + "-" + (monthOfYear + 1) + "-" +dayOfMonth.toString() , TextView.BufferType.EDITABLE )
                }, mYear, mMonth, mDay)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            datePickerDialog.show()
        }
        mBinding?.txtPiSwipe?.setOnClickListener {
            val qty = mBinding?.etQty?.text.toString()
            val date = mBinding?.etDeliveryDate?.text.toString()
            val ppu = mBinding?.etPpu?.text.toString()
            val hsn = mBinding?.etHsnCode?.text.toString()
            val sgst = mBinding?.etSgst?.text.toString()
            val cgst = mBinding?.etCgst?.text.toString()
            currency = mBinding?.spCurrency?.selectedItem.toString()
            if (qty.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_qty), applicationContext)
            else if (date.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_del_date), applicationContext )
            else if (ppu.isEmpty()) Utility.displayMessage(getString(R.string.add_ppu), applicationContext)
            else if (hsn.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_hsn),applicationContext)
            else if (sgst.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_sgst), applicationContext)
            else if (cgst.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_cgst), applicationContext)
            else if (currency!!.isEmpty()) Utility.displayMessage(getString(R.string.plz_add_currency), applicationContext)
            else if(!mBinding?.chbTnc!!.isChecked)Utility.displayMessage(getString(R.string.plz_accept_tnc), applicationContext)
            else {
                pi.cgst=cgst.toLong()
                pi.expectedDateOfDelivery=date
                pi.hsn=hsn.toLong()
                pi.ppu=ppu.toLong()
                pi.quantity=qty.toLong()
                pi.sgst=sgst.toLong()
                if (Utility.checkIfInternetConnected(applicationContext)) {
//                    mBinding?.txtPiSwipe?.setText("Pi preview being genrated")
                    mBinding?.txtPiSwipe?.isEnabled=false
                    viewLoader()
                    mEnqVM?.revisePi(enquiryId,  pi)
                } else {
                    PiPredicates.insertPiForOfflineForRevise(enquiryId,pi)
                    OrdersPredicates.updatIsPiSend(enquiryId,1L)
                    Utility.displayMessage(getString(R.string.raise_pi_offline),applicationContext)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
        mBinding?.chbTnc?.setOnClickListener {
        }
        mBinding?.txtTnc?.setOnClickListener {
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "Terms_conditions")
            startActivity(intent)
        }
        mBinding?.btnChat?.setOnClickListener {
            enquiryId?.let {  startActivity(Intent(this?.chatLogDetailsIntent(it)))}
        }
    }

    fun setDetails(){
        mBinding?.enquiryCode?.text=getString(R.string.proforma_invoice)+": ${enquiryDetails?.enquiryCode}"
        mBinding?.enquiryStartDate?.text = getString(R.string.date_accepted)+": ${enquiryDetails?.startedOn?.split("T")?.get(0)}"
        val image = enquiryDetails?.productImages?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)
        if(enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT){
            url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
            isCustom = true
        }else{
            url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
            isCustom = false
        }
        mBinding?.moqDetailsLayer?.visibility=View.GONE
        mBinding?.productImage?.let { ImageSetter.setImage(applicationContext, url!!, it, R.drawable.artisan_logo_placeholder, R.drawable.artisan_logo_placeholder, R.drawable.artisan_logo_placeholder) }
        mBinding?.buyerCompany?.text = enquiryDetails?.ProductBrandName
        mBinding?.productAmount?.text = "₹ ${enquiryDetails?.totalAmount ?: 0}"
        when(enquiryDetails?.productStatusID){
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
        if(enquiryDetails?.productName != "") {
            mBinding?.productName?.text = enquiryDetails?.productName
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
            fp.setSpan(applicationContext?.let { ContextCompat.getColor(it,R.color.black_text) }?.let {
                ForegroundColorSpan(
                    it
                )
            }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)
        }

        currencyList.clear()
        currencyList.add(" ₹")
//        currencyList.add("$")
//        currencyList.add(" £")
//        currencyList.add(" €")
        val spEstDaysAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item,currencyList)
        spEstDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spCurrency?.adapter = spEstDaysAdapter

        val piObj=PiPredicates.getSinglePi(enquiryId)
           piObj?.let{
               mBinding?.etQty?.setText(it?.quantity.toString(), TextView.BufferType.EDITABLE)
               mBinding?.etDeliveryDate?.setText(it?.date, TextView.BufferType.EDITABLE)
               mBinding?.etPpu?.setText(it?.ppu.toString(), TextView.BufferType.EDITABLE)
               mBinding?.etHsnCode?.setText(it?.hsn.toString(), TextView.BufferType.EDITABLE)
               mBinding?.etSgst?.setText(it.sgst.toString(), TextView.BufferType.EDITABLE)
               mBinding?.etCgst?.setText(it.cgst.toString(), TextView.BufferType.EDITABLE)
           }

    }

    fun viewLoader(){
        mBinding?.swipeEnquiryDetails?.visibility= View.VISIBLE
    }

    fun hideLoader(){
        mBinding?.swipeEnquiryDetails?.visibility= View.VISIBLE
    }

    override fun onPiFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage(getString(R.string.unable_tofetch_pi),applicationContext)
            })
        } catch (e: Exception) {
            Log.e("Enquiry Details", "Exception onFailure " + e.message)
        }
    }

    override fun getPiSuccess(id: Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("PiPostCr", "getPiSuccess")
                 setDetails()
            })
        } catch (e: Exception) {
            Log.e("RevisePiActivity", "Exception onFailure " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("RevisePiActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()

            }
        }
    }

    override fun onRevisePiFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage(getString(R.string.unable_raise_pi),applicationContext)
            })
        } catch (e: Exception) {
            Log.e("revisePi", "Exception onFailure " + e.message)
        }
    }

    override fun onRevisePiSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                Utility.displayMessage(getString(R.string.pi_raised_succesfully),applicationContext)
                setResult(Activity.RESULT_OK)
                finish()
                //todo setresult
            })
        } catch (e: Exception) {
            Log.e("revisePi", "Exception onFailure " + e.message)
        }
    }
}