package com.adrosonic.craftexchange.ui.modules.order.cr

import android.app.Activity
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
import com.adrosonic.craftexchange.databinding.ActivityCrBinding
import com.adrosonic.craftexchange.repository.data.request.changeRequest.ItemList
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOption
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOptionsResponse
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
import kotlin.collections.ArrayList


fun Context.crContext(enquiryId:Long,changeRequestStatus:Long): Intent {
    val intent = Intent(this, CrActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("changeRequestStatus", changeRequestStatus)
    return intent
}

class CrActivity : LocaleBaseActivity(),
    OrdersViewModel.FetchCrInterface,
    OrdersViewModel.UpdateCrStatusInterface,
    CrAcceptRejectAdapter.selectionListener {
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
    var stageList =ArrayList<CrOption>()
    var profile=""
    private lateinit var crSelectionAdapter: CrAcceptRejectAdapter
    var crSelctionList = ArrayList<Pair<ChangeRequests,Boolean>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cr)
        val view = mBinding?.root
        setContentView(view)
        val gson = GsonBuilder().create()
        var crStages = gson.fromJson(UserConfig.shared.crStatusData.toString(), CrOptionsResponse::class.java)
        stageList.clear()
        stageList.addAll(crStages.data)
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra("enquiryId",0)
            changeRequestStatus = intent.getLongExtra("changeRequestStatus",4)
//            orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(enquiryId,0)
            setDetails()
        }
        Log.e("RaiseCr", "changeRequestStatus : ${changeRequestStatus}")
        mBinding?.btnBack?.setOnClickListener {
            finish()
        }
        mBinding?.txtCrSwipe?.setOnClickListener {
             weftYarn = mBinding?.etWeftYarn?.text.toString()
             color = mBinding?.etColor?.text.toString()
             qty = mBinding?.etQty?.text.toString()
             motifSize = mBinding?.etMotifSize?.text.toString()
             motif = mBinding?.etMotif?.text.toString()

            if (weftYarn.isEmpty() && color.isEmpty()&& qty.isEmpty() && motifSize.isEmpty()&&motif.isEmpty()) Utility.displayMessage(getString(R.string.plz_fill_in), applicationContext)
            else {
                showDialog()
            }
        }
        mBinding?.etWeftYarn?.addTextChangedListener(generalTextWatcher)
        mBinding?.etColor?.addTextChangedListener(generalTextWatcher)
        mBinding?.etQty?.addTextChangedListener(generalTextWatcher)
        mBinding?.etMotifSize?.addTextChangedListener(generalTextWatcher)
        mBinding?.etMotif?.addTextChangedListener(generalTextWatcher)
        mBinding?.txtSubmit?.setOnClickListener {
            var crFlagList = ArrayList<Boolean>()
            crSelctionList.forEach { if (it.second) crFlagList?.add(it.second) }
            if(!crFlagList.contains(true)){showAcceptRejectDialog(false)}
            else showAcceptRejectDialog(true)
        }
    }

    fun setDetails(){
            crSelctionList.clear()
            enquiryId?.let{ orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(it,0)}
            profile = Prefs.getString(ConstantsDirectory.PROFILE,"")
            mBinding?.enquiryCode?.text=getString(R.string.cr_for)+": ${orderDetails?.orderCode}"
            mBinding?.enquiryStartDate?.text = getString(R.string.date_accepted)+": ${orderDetails?.startedOn?.split("T")?.get(0)}"
            when(profile) {
                ConstantsDirectory.ARTISAN -> {
                    mBinding?.disclaimerText1?.text=getString(R.string.cr_pleas_note_artisan)
                    mBinding?.disclaimerText2?.text=getString(R.string.cr_note_artisan)
                }
                ConstantsDirectory.BUYER -> {
                    mBinding?.disclaimerText1?.text=getString(R.string.cr_pleas_note)
                    mBinding?.disclaimerText2?.text=getString(R.string.cr_note)
                }
            }
            when(changeRequestStatus) {
                0L -> {
                    when(profile){
                        ConstantsDirectory.ARTISAN -> {
                            mBinding?.txtSubmit?.visibility=View.VISIBLE
                            mBinding?.acceptRejectCr?.visibility=View.VISIBLE
                            mBinding?.editCrDetails?.visibility=View.GONE
                            var changeReq = CrPredicates.getCrs(enquiryId)
                            changeReq?.forEach {
                                if(it.requestStatus!!.equals(0L)||it.requestStatus!!.equals(2L)) crSelctionList.add(Pair(it,false))
                                else crSelctionList.add(Pair(it,true))
                            }
                            mBinding?.acceptRejectRecyclerList?.layoutManager = LinearLayoutManager(this)
                            crSelectionAdapter = CrAcceptRejectAdapter(this, crSelctionList,stageList,true)
                            crSelectionAdapter.listener = this
                            mBinding?.acceptRejectRecyclerList?.adapter = crSelectionAdapter
                            mBinding?.txtCountAcceptReject?.text=getString(R.string.accepted_txt1)+" ${crSelctionList.size} "+getString(R.string.accepted_txt2)
                        }
                        ConstantsDirectory.BUYER -> {
                            mBinding?.acceptRejectCr?.visibility=View.GONE
                            mBinding?.editCrDetails?.visibility=View.VISIBLE
                            mBinding?.txtCrSwipe?.visibility=View.GONE
                            mBinding?.etWeftYarn?.isEnabled = false
                            mBinding?.etColor?.isEnabled = false
                            mBinding?.etQty?.isEnabled = false
                            mBinding?.etMotifSize?.isEnabled = false
                            mBinding?.etMotif?.isEnabled = false
                            var changeReq = CrPredicates.getCrs(enquiryId)
                            if(changeReq!=null) {
                                val crIterator=changeReq.iterator()
                                while (crIterator.hasNext()) {
                                    val cr = crIterator.next()
                                    stageList.forEach {
                                        if (cr.requestItemsId!!.equals(it.id)) {
                                            when (it.item) {
                                                "Change in weft Yarn" -> {
                                                    mBinding?.etWeftYarn?.setText(cr?.requestText ?: "", TextView.BufferType.NORMAL)
                                                }
                                                "Change in color" -> {
                                                    mBinding?.etColor?.setText( cr?.requestText ?: "",TextView.BufferType.NORMAL)
                                                }
                                                "Change in Quantity" -> {
                                                    mBinding?.etQty?.setText(cr?.requestText ?: "", TextView.BufferType.NORMAL )
                                                }
                                                "Change in motif size" -> {
                                                    mBinding?.etMotifSize?.setText(cr?.requestText ?: "",TextView.BufferType.NORMAL)
                                                }
                                                "Change in motif placement" -> {
                                                    mBinding?.etMotif?.setText(cr?.requestText ?: "",TextView.BufferType.NORMAL)
                                                }
                                            }
                                        }
                                    }
                                    setStatusResource()
                                }
                            }
                        }
                    }
                }
                1L,3L,2L->{
                    mBinding?.acceptRejectCr?.visibility=View.VISIBLE
                    mBinding?.editCrDetails?.visibility=View.GONE
                    mBinding?.txtSubmit?.visibility=View.GONE
                    var changeReq = CrPredicates.getCrs(enquiryId)
                    var count=0
                    changeReq?.forEach {
                        if(it.requestStatus!!.equals(0L)||it.requestStatus!!.equals(2L)) crSelctionList.add(Pair(it,false))
                        else {
                            count++
                            crSelctionList.add(Pair(it,true))
                        }
                    }
                    mBinding?.acceptRejectRecyclerList?.layoutManager = LinearLayoutManager(this)
                    crSelectionAdapter = CrAcceptRejectAdapter(this, crSelctionList,stageList,false)
                    crSelectionAdapter.listener = this
                    mBinding?.acceptRejectRecyclerList?.adapter = crSelectionAdapter
                    when(profile) {
                        ConstantsDirectory.ARTISAN -> mBinding?.txtCountAcceptReject?.text = "You have accepted $count out of ${crSelctionList.size} requests"
                        ConstantsDirectory.BUYER -> mBinding?.txtCountAcceptReject?.text = "Artisan has accepted $count out of ${crSelctionList.size} requests"
                    }
                }
                4L->{
                    mBinding?.acceptRejectCr?.visibility=View.GONE
                    mBinding?.editCrDetails?.visibility=View.VISIBLE
                }
            }
    }

    fun viewLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing= true
    }
    fun hideLoader(){
        mBinding?.swipeEnquiryDetails?.isRefreshing= false
    }

    fun showDialog(){
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_raise_cr_confirm)
        dialog.create()
        dialog.show()
        val btn_cancel = dialog?.findViewById(R.id.btn_cancel) as TextView
        val btn_ok = dialog?.findViewById(R.id.btn_ok) as TextView
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

        btn_cancel?.setOnClickListener {
            dialog.cancel()
        }
        Log.e("RaiseCr","itemList 11111: ${itemList.size}")
        btn_ok?.setOnClickListener {
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

    fun showAcceptRejectDialog(isAccept:Boolean){
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cr_accept_reject)
        dialog.create()
        dialog.show()

        val btn_cancel = dialog?.findViewById(R.id.btn_cancel) as TextView
        val btn_ok = dialog?.findViewById(R.id.btn_ok) as TextView
        val txt_goto_chat = dialog?.findViewById(R.id.txt_goto_chat) as TextView
        val txt_changes_dscrp = dialog?.findViewById(R.id.txt_changes_dscrp) as TextView
        val itemList= ArrayList<ItemList>()
        var acceptedChanges=""
        crSelctionList.forEach {
            if(it.second){
                itemList.add(ItemList(it.first.changeRequestId?:0,it.first.crId?:0,it.first.requestItemsId?:0,1,it.first.requestText?:""))
                stageList.forEach {it1->
                    if (it.first.requestItemsId!!.equals(it1.id)) {
                       when(it1.item){
                           "Change in weft Yarn"->acceptedChanges=acceptedChanges+" Yarn, "
                           "Change in color"-> acceptedChanges=acceptedChanges+" Color, "
                           "Change in Quantity"-> acceptedChanges=acceptedChanges+" Quantity, "
                           "Change in motif size"->acceptedChanges=acceptedChanges+" Size, "
                           "Change in motif placement"->acceptedChanges=acceptedChanges+" Placement, "
                       }
                    }
                }
            }
            else itemList.add(ItemList(it.first.changeRequestId?:0,it.first.crId?:0,it.first.requestItemsId?:0,2,it.first.requestText?:""))
        }
        if (acceptedChanges.endsWith(", ")) {
            acceptedChanges = acceptedChanges.substring(0, acceptedChanges.length - 2);
        }
        if(isAccept){
            @RequiresApi(Build.VERSION_CODES.N)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txt_changes_dscrp?.text = Html.fromHtml(getString(R.string.you_abt)+" <b>$acceptedChanges</b>.", Html.FROM_HTML_MODE_COMPACT)
            } else {
                txt_changes_dscrp?.text = Html.fromHtml(getString(R.string.you_abt)+" <b>$acceptedChanges</b>.")
            }
        }
        else txt_changes_dscrp.text=getString(R.string.you_abt_reject)
        Log.e("RaiseCr","itemList ${itemList.count()} ")

        btn_ok.setOnClickListener {
            var req= RaiseCrInput(enquiryId,itemList)
            Log.e("RaiseCr","itemList ${itemList.size} ")
                var longList=ArrayList<Long>()
                itemList.forEach {
                    longList.add(it.requestStatus)
                }
                var changeRequestStatus=1L
                if(longList.contains(2) && longList.contains(1)) changeRequestStatus=3L
                else if(!longList.contains(1))changeRequestStatus=2L
                else if(!longList.contains(2))  changeRequestStatus=1L
            Log.e("RaiseCr","changeRequestStatus ${changeRequestStatus} ")
            if (Utility.checkIfInternetConnected(applicationContext)) {
                viewLoader()
                mOrderVM?.updateCrListener=this
                mOrderVM?.acceptRejectChangeRequest(req,changeRequestStatus)
            } else {
                Log.e("RaiseCr","Req Json ${Gson().toJson(req)} ")
                CrPredicates.updatePostCrStatus(req,changeRequestStatus)
                this.changeRequestStatus=changeRequestStatus
                OrdersPredicates.updateChangeRequestStatusOffline(enquiryId, Gson().toJson(req),1L,changeRequestStatus)
                Utility.displayMessage(getString(R.string.no_internet_connection),application)
                setDetails()
            }
            dialog.cancel()
        }
        btn_cancel.setOnClickListener {  dialog.cancel() }
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
                    Utility.displayMessage(getString(R.string.unable_to_raise_cr),this)
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

    override fun onCrItemSelected(pairList: ArrayList<Pair<ChangeRequests, Boolean>>) {
        this.crSelctionList = pairList
        var selectedArr=ArrayList<Long>()
        selectedArr.clear()
        var count=0
        pairList.forEach {
            if(it.second)count++
        }
        mBinding?.txtCountAcceptReject?.text="You have accepted ${count} out of ${crSelctionList.size} requests"
    }

    override fun onCrStatusSuccess(crStatus:Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("RaiseCr","onFetchCrSuccess Success")
                hideLoader()
                changeRequestStatus=crStatus
                setDetails()
                showPiDialog()
                Utility.displayMessage(getString(R.string.cr_status_updated),this)
            })
        } catch (e: Exception) {
            Log.e("RaiseCr", "Exception onFetchCrSuccess " + e.message)
        }
    }

    override fun onCrStatusFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("RaiseCr","onFetchCrSuccess Success")
                hideLoader()
                setDetails()
                Utility.displayMessage(getString(R.string.unable_to_update_cr_stus),this)
            })
        } catch (e: Exception) {
            Log.e("RaiseCr", "Exception onFetchCrSuccess " + e.message)
        }
    }

    fun showPiDialog(){
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_post_cr_process)
        dialog.create()
        dialog.show()

        val btn_skip = dialog?.findViewById(R.id.btn_skip) as Button
        val btn_raise_pi = dialog?.findViewById(R.id.btn_raise_pi) as Button
        btn_raise_pi.setOnClickListener {
        enquiryId?.let {
            dialog.cancel()
            startActivityForResult(this.revisePiContext(it),ConstantsDirectory.RESULT_PI)}
        }
        btn_skip.setOnClickListener {  dialog.cancel() }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("CrActivity", "onActivityResult RESULT_OK ${Activity.RESULT_OK}")
        if (requestCode == ConstantsDirectory.RESULT_PI) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}