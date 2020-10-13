package com.adrosonic.craftexchange.ui.modules.order.cr

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.CrPredicates
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.databinding.ActivityCrBinding
import com.adrosonic.craftexchange.databinding.ActivityPiBinding
import com.adrosonic.craftexchange.repository.data.request.changeRequest.ItemList
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOptionsResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.moq.Datum
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.GsonBuilder
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import kotlinx.android.synthetic.main.dialog_are_you_sure.*
import kotlinx.android.synthetic.main.dialog_raise_cr_confirm.*
import java.util.*
import kotlin.collections.ArrayList


fun Context.crContext(enquiryId:Long,changeRequestStatus:Long): Intent {
    val intent = Intent(this, CrActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("changeRequestStatus", changeRequestStatus)
    return intent
}

class CrActivity : AppCompatActivity(),
    OrdersViewModel.FetchCrInterface
    {
    var enquiryId=0L
    var changeRequestStatus=0L
    val mOrderVM : OrdersViewModel by viewModels()
    var orderDetails: Orders? = null
    private var mBinding: ActivityCrBinding? = null

    var weftYarn =""
    var color = ""
    var qty = ""
    var motifSize = ""
    var motif = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cr)
        val view = mBinding?.root
        setContentView(view)
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            changeRequestStatus = intent.getLongExtra("changeRequestStatus",4)
            orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(enquiryId,0)
            setDetails()
        }
        Log.e("RaisePi", "pi enquiryId : ${enquiryId}")
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }

        mBinding?.txtCrSwipe?.setOnClickListener {
             weftYarn = mBinding?.etWeftYarn?.text.toString()
             color = mBinding?.etColor?.text.toString()
             qty = mBinding?.etQty?.text.toString()
             motifSize = mBinding?.etMotifSize?.text.toString()
             motif = mBinding?.etMotif?.text.toString()

            if (weftYarn.isEmpty() && color.isEmpty()&& qty.isEmpty() && motifSize.isEmpty()&&motif.isEmpty()) Utility.displayMessage("Please fill in the required input fields", applicationContext)
            else {
                showDialog()
            }
        }
        mBinding?.etWeftYarn?.addTextChangedListener(generalTextWatcher)
        mBinding?.etColor?.addTextChangedListener(generalTextWatcher)
        mBinding?.etQty?.addTextChangedListener(generalTextWatcher)
        mBinding?.etMotifSize?.addTextChangedListener(generalTextWatcher)
        mBinding?.etMotif?.addTextChangedListener(generalTextWatcher)
    }

    fun setDetails(){
        mBinding?.enquiryCode?.text="CR for ${orderDetails?.orderCode}"
        mBinding?.enquiryStartDate?.text = "Date accepted : ${orderDetails?.startedOn?.split("T")?.get(0)}"
        when(changeRequestStatus){
            0L->{
                mBinding?.etWeftYarn?.isEnabled=false
                mBinding?.etColor?.isEnabled=false
                mBinding?.etQty?.isEnabled=false
                mBinding?.etMotifSize?.isEnabled=false
                mBinding?.etMotif?.isEnabled=false
                var changeReq = CrPredicates.getCrs(enquiryId)
//                when
                    //todo UI
            }
        }
    }

    fun viewLoader(){
        mBinding?.swipeEnquiryDetails?.visibility= View.VISIBLE
    }
    fun hideLoader(){
        mBinding?.swipeEnquiryDetails?.visibility= View.GONE
    }

    fun showDialog(){
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_raise_cr_confirm)
        dialog.create()
        dialog.show()

        val gson = GsonBuilder().create()
        var crStages = gson.fromJson(UserConfig.shared.crStatusData.toString(), CrOptionsResponse::class.java)
        var stageList = crStages.data

        val itemList= ArrayList<ItemList>()
        if(weftYarn.isNotEmpty()){
            dialog.ll1.visibility=View.VISIBLE
            dialog.txtWeft.text=weftYarn
            var requestItemId=0L
            stageList.forEach { if(it.item.contains("Yarn")) requestItemId=it.id}
            itemList.add(ItemList(0,0,requestItemId,0,weftYarn))
        } else   dialog.ll1.visibility=View.GONE
        if(color.isNotEmpty()) {
            dialog.ll2.visibility=View.VISIBLE
            dialog.txtColor.text=color
            var requestItemId=0L
            stageList.forEach { if(it.item.contains("color")) requestItemId=it.id}
            itemList.add(ItemList(0,0,requestItemId,0,color))
        } else  dialog.ll2.visibility=View.GONE
        if(qty.isNotEmpty()){
            dialog.ll3.visibility=View.VISIBLE
            dialog.txtQty.text=qty
            var requestItemId=0L
            stageList.forEach { if(it.item.contains("Quantity")) requestItemId=it.id}
            itemList.add(ItemList(0,0,requestItemId,0,qty))
        } else   dialog.ll3.visibility=View.GONE
        if(motifSize.isNotEmpty()){
            dialog.ll4.visibility=View.VISIBLE
            dialog.txtMotifSize.text=motifSize
            var requestItemId=0L
            stageList.forEach { if(it.item.contains("motif size")) requestItemId=it.id}
            itemList.add(ItemList(0,0,requestItemId,0,motifSize))
        } else   dialog.ll4.visibility=View.GONE
        if(motif.isNotEmpty()) {
            dialog.ll5.visibility=View.VISIBLE
            dialog.txtMotifPlacement.text=motif
            var requestItemId=0L
            stageList.forEach { if(it.item.contains("motif placement")) requestItemId=it.id}
            itemList.add(ItemList(0,0,requestItemId,0,motif))
        } else   dialog.ll5.visibility=View.GONE

        dialog.btn_cancel?.setOnClickListener {
            dialog.cancel()
        }
        Log.e("RaiseCr","itemList 11111: ${itemList.size}")
        dialog.btn_ok?.setOnClickListener {
            if (Utility.checkIfInternetConnected(applicationContext)) {
                viewLoader()
                Log.e("RaiseCr","itemList 2222: ${itemList.size}")
                var req= RaiseCrInput(enquiryId,itemList)
                mOrderVM?.fetcCrListener=this
                mOrderVM?.raiseChangeRequest(enquiryId,req)
            } else {
                Utility.displayMessage(getString(R.string.no_internet_connection),application)
            }
            dialog.cancel()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Log.e("RaiseCr", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
//        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
//            if (resultCode == Activity.RESULT_OK) {
//                setResult(Activity.RESULT_OK)
//                finish()
//
//            }
//        }
    }

        override fun onFetchCrSuccess() {
            try {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Log.e("RaiseCr","onFetchCrSuccess Success")
                    finish()
                })
            } catch (e: Exception) {
                Log.e("RaiseCr", "Exception onFetchCrSuccess " + e.message)
            }
        }

        override fun onFetchCrFailure() {
            try {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Log.e("RaiseCr","onFetchCrFailure ")
                    Utility.displayMessage("Unable to raise CR, please try again later",this)
                })
            } catch (e: Exception) {
                Log.e("RaiseCr", "Exception onFetchCrFailure " + e.message)
            }
        }

        private val generalTextWatcher: TextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun beforeTextChanged( s: CharSequence, start: Int, count: Int, after: Int ) { }
            override fun afterTextChanged(s: Editable) {
                setStatusResource()
            }
        }

        fun setStatusResource() {

            if(mBinding?.etWeftYarn?.text!!.isNotBlank() )Utility.setImageResource(applicationContext, mBinding?.img1, R.drawable.ic_cr_checked)
            else Utility.setImageResource(applicationContext, mBinding?.img1, R.drawable.ic_cr_unchecked)

            if(mBinding?.etColor?.text!!.isNotBlank())Utility.setImageResource(applicationContext,  mBinding?.img2, R.drawable.ic_cr_checked)
            else Utility.setImageResource(applicationContext,  mBinding?.img2, R.drawable.ic_cr_unchecked)

            if(mBinding?.etQty?.text!!.isNotBlank())Utility.setImageResource(applicationContext,  mBinding?.img3, R.drawable.ic_cr_checked)
            else Utility.setImageResource(applicationContext,  mBinding?.img3, R.drawable.ic_cr_unchecked)

            if(mBinding?.etMotifSize?.text!!.isNotBlank())Utility.setImageResource(applicationContext,  mBinding?.img4, R.drawable.ic_cr_checked)
            else Utility.setImageResource(applicationContext,  mBinding?.img4, R.drawable.ic_cr_unchecked)

            if(mBinding?.etMotif?.text!!.isNotBlank())Utility.setImageResource(applicationContext,  mBinding?.img5, R.drawable.ic_cr_checked)
            else Utility.setImageResource(applicationContext,  mBinding?.img5, R.drawable.ic_cr_unchecked)
        }
    }