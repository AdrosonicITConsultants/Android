package com.adrosonic.craftexchange.ui.modules.order.confirmDelivery

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChangeRequests
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.CrPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.databinding.ActivityConfirmDeliveryBinding
import com.adrosonic.craftexchange.databinding.ActivityCrBinding
import com.adrosonic.craftexchange.repository.data.request.changeRequest.ItemList
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOption
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOptionsResponse
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.order.cr.adapter.CrAcceptRejectAdapter
import com.adrosonic.craftexchange.ui.modules.order.revisePi.revisePiContext
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import kotlinx.android.synthetic.main.dialog_cr_accept_reject.*
import kotlinx.android.synthetic.main.dialog_raise_cr_confirm.*
import java.util.*
import kotlin.collections.ArrayList


fun Context.confirmDeliveryContext(enquiryId:Long): Intent {
    val intent = Intent(this, ConfirmDeliveryActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}

class ConfirmDeliveryActivity : LocaleBaseActivity(),
    OrdersViewModel.OrderCinfirmedInterface{
    var enquiryId=0L
    val mOrderVM : OrdersViewModel by viewModels()
    var orderDetails: Orders? = null
    private var mBinding: ActivityConfirmDeliveryBinding? = null

    var stageList =ArrayList<CrOption>()
    var profile=""
    private lateinit var crSelectionAdapter: CrAcceptRejectAdapter
    var crSelctionList = ArrayList<Pair<ChangeRequests,Boolean>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_delivery)
        val view = mBinding?.root
        setContentView(view)
        mOrderVM.orderConfirmListener=this
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            setDetails()
        }
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        mBinding?.btnConfirmReceived?.setOnClickListener {
            if(mBinding?.txtDateReceived?.text.toString().isNotEmpty()){
                if(Utility.checkIfInternetConnected(applicationContext)){
                    viewLoader()
                    mOrderVM.markOrderAsReceived(orderDetails?.enquiryId?:0,mBinding?.txtDateReceived?.text.toString())
                }else Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
            }else Utility.displayMessage(getString(R.string.select_revise_date),applicationContext)
        }
       mBinding?.selectDate?.setOnClickListener {
           val c: Calendar = Calendar.getInstance()
           val mYear = c.get(Calendar.YEAR)
           val mMonth = c.get(Calendar.MONTH)
           val mDay = c.get(Calendar.DAY_OF_MONTH)
           val datePickerDialog = DatePickerDialog(
               this,
               DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                   mBinding?.txtDateReceived?.setText(
                       year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString(),
                       TextView.BufferType.EDITABLE
                   )
               }, mYear, mMonth, mDay)
//           datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
           datePickerDialog.getDatePicker().maxDate=(System.currentTimeMillis() - 1000)
           datePickerDialog.show()
       }
       mBinding?.btnChat?.setOnClickListener {
           enquiryId?.let {  startActivity(Intent(this?.chatLogDetailsIntent(it)))}
       }

    }

    fun setDetails(){
            crSelctionList.clear()
            enquiryId?.let{ orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(it,0)}
            profile = Prefs.getString(ConstantsDirectory.PROFILE,"")
            mBinding?.enquiryCode?.text=getString(R.string.confirming_delivery)
            mBinding?.enquiryStartDate?.text = getString(R.string.date)+" : ${orderDetails?.startedOn?.split("T")?.get(0)}"
    }

    fun viewLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing= true
    }
    fun hideLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing= false
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoader()
                setDetails()
                showRatingDialog()
                Utility.displayMessage(getString(R.string.order_confirmed),this)
            })
        } catch (e: Exception) {
            Log.e("ConfirmDeliveryActivity", "Exception onFetchCrSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("ConfirmDeliveryActivity","onFetchCrSuccess Success")
                hideLoader()
                setDetails()
                Utility.displayMessage(getString(R.string.unable_order_confirmed),this)
            })
        } catch (e: Exception) {
            Log.e("ConfirmDeliveryActivity", "Exception onFetchCrSuccess " + e.message)
        }
    }

    fun showRatingDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_post_cr_process)
        dialog.create()
        dialog.show()

        val btn_skip = dialog?.findViewById(R.id.btn_skip) as Button
        val btn_raise_pi = dialog?.findViewById(R.id.btn_raise_pi) as Button
        val txt_dscrp = dialog?.findViewById(R.id.txt_dscrp) as TextView
        val txt_changes_dscrp = dialog?.findViewById(R.id.txt_changes_dscrp) as TextView
        val txt_header_dscro = dialog?.findViewById(R.id.txt_header_dscro) as TextView
        txt_dscrp.text = getString(R.string.completed)+"!"
        txt_changes_dscrp.text = "Would you like to review and rate this order?"
        txt_header_dscro.text = "You can find this order under completed tab"
        btn_raise_pi.text = "Review and rating"
        btn_raise_pi.setOnClickListener {
            enquiryId?.let {
                dialog.cancel()
                Utility.displayMessage("Coming soon",applicationContext)
                setResult(Activity.RESULT_OK)
                finish()
//            startActivityForResult(this.revisePiContext(it),ConstantsDirectory.RESULT_PI)}
            }
        }
        btn_skip.setOnClickListener {
            dialog.cancel()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ConfirmDeliveryActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}