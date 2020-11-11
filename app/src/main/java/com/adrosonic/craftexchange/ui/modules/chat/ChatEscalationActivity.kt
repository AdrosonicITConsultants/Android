package com.adrosonic.craftexchange.ui.modules.chat

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.entities.realmEntities.Escalations
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.databinding.ActivityChatEscalationBinding
import com.adrosonic.craftexchange.repository.data.request.chat.RaiseEscalationRequest
import com.adrosonic.craftexchange.ui.modules.chat.adapter.EscalationRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import io.realm.RealmResults

fun Context.chatEscalationIntent(enquiryId:Long): Intent {
    val intent = Intent(this, ChatEscalationActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent
}

class ChatEscalationActivity : AppCompatActivity(),
    ChatListViewModel.EscalationActionInterface,
    ChatListViewModel.EscalationListInterface,
    EscalationRecyclerAdapter.EscalationMarkResolvedListener{

    private var mBinding : ActivityChatEscalationBinding ?= null
    var escalationList = ArrayList<String>()
    var escSelectedCat : Int?= 0
    var loadingDialog : Dialog ?= null
    var escRequest = RaiseEscalationRequest()
    var enqID : Long ?= 0L
    var chatUserDetails: ChatUser?=null
    var escalationDetails: Escalations ?= null
    var escRealmList : RealmResults<Escalations> ?= null
    var mAdapater : EscalationRecyclerAdapter ?= null


    val mChatVM : ChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatEscalationBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mBinding?.swipeChats?.isEnabled = false
        mChatVM.actionEscalationListener = this
        mChatVM.escalationListListener = this
        if (intent.extras != null) {
            enqID = intent.getLongExtra("enquiryId",0)
        }

        setEscRecyclerList()

        //Chat Data
        enqID?.let {
            chatUserDetails= ChatUserPredicates.getChatDetailsByEnquiryId(it)
        }
        if(chatUserDetails!=null){
            setChatDetails()
        }

        //Set Escalation list
        if(Utility.checkIfInternetConnected(this)){
            enqID?.let { mChatVM?.getEscalationsList(it) }
            mBinding?.swipeChats?.isRefreshing = true
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),this)
        }
        enqID?.let {
            mChatVM?.getEscalationList(it).observe(this, Observer<RealmResults<Escalations>> {
                escRealmList = it
                mAdapater?.updateEscalationList(escRealmList)
            })
        }

        //Escalation Categories
        var escCat = Utility?.getEscalationData()?.data
        escCat?.forEach {
            escalationList?.add(it.category)
        }

        loadingDialog = Utility?.loadingDialog(this)

        //Action Buttons
        mBinding?.iconEscalation?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.btnBack?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.btnSendEscalation?.setOnClickListener {
            if(mBinding?.txtDescription?.nonEmpty() == true){
                view?.clearFocus() //to close the keyboard before displaying the dialogbox
                showEscCatDialog(this)
            }else{
                mBinding?.txtDescription?.nonEmpty{ mBinding?.txtDescription?.error = it }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        finish()
    }

    fun setChatDetails(){
        mBinding?.enquiryCode?.text = chatUserDetails?.enquiryNumber
        mBinding?.txtNumEscalations?.text = "${enqID?.let { ChatUserPredicates?.getPendingEscalationEnquiry(it)?.size ?: 0}} Escalations"
    }

    private fun setEscRecyclerList(){
        mBinding?.escalationRecylerview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapater = enqID?.let { mChatVM?.getEscalationList(it).value }?.let { EscalationRecyclerAdapter(this, it) }
        mBinding?.escalationRecylerview?.adapter = mAdapater
        mAdapater?.escListener = this
    }

    fun showEscCatDialog(context: Context) {
        var rDialog = Dialog(context)
        rDialog?.setContentView(R.layout.dialog_raise_escalation)
        rDialog?.show()
        val raiseEsc = rDialog?.findViewById(R.id.btn_dRaise_escalation) as Button
        val cancel = rDialog?.findViewById(R.id.btn_cancel_escalation) as TextView
        var radioGroup = rDialog?.findViewById(R.id.escalation_radiogrp) as RadioGroup

        escalationList?.forEach {
            var rB = RadioButton(context)
            val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            rB.layoutParams = params
            rB.setPadding(5, 5, 20, 5)
            rB.text = it
            radioGroup.addView(rB)
        }

        escSelectedCat = 0
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb = (group.findViewById(checkedId) as RadioButton).text
            Log.e("EscalationCatSel","$rb")
            escSelectedCat = checkedId%5  //to counter adding ov multiple radiobuttons
        }

        cancel.setOnClickListener {
            rDialog.cancel()
        }
        raiseEsc.setOnClickListener {
            if(escSelectedCat != 0){
                rDialog.cancel()
                showConfirmEscalaltionDialog()
            }else{
                Utility?.displayMessage(this.getString(R.string.select_one_issue),this)
            }
        }
    }

    fun showConfirmEscalaltionDialog() {
        var cDialog = Dialog(this)
        cDialog?.setContentView(R.layout.dialog_escalation_confirm)
        cDialog?.show()
        val ok = cDialog?.findViewById(R.id.btn_ok_escalation) as Button
        val goBack = cDialog?.findViewById(R.id.btn_goback_escalation) as TextView

        goBack.setOnClickListener {
            cDialog.cancel()
        }

        ok.setOnClickListener {
            cDialog.cancel()
            if(Utility.checkIfInternetConnected(this)){
                loadingDialog?.show()

                escRequest?.category = escSelectedCat?.toLong()
                escRequest?.enquiryId = enqID
                escRequest?.escalationFrom = Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong()
                escRequest?.escalationTo = chatUserDetails?.buyerId
                escRequest?.text = mBinding?.txtDescription?.text.toString()

                mChatVM.raiseEscalation(escRequest)
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),this)
            }
        }
    }

    override fun onESCActionSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("EscalationAction","onSuccess")
                mBinding?.txtDescription?.text?.clear()
                if(Utility.checkIfInternetConnected(this)){
                    enqID?.let { mChatVM?.getEscalationsList(it) }
                }else{
                    Utility.displayMessage(getString(R.string.esc_updated_shortly),this)
                }
            })
        } catch (e: Exception){
            Log.e("EscalationAction", "Exception onFailure " + e.message)
        }
    }

    override fun onESCActionFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("EscalationAction","onFailure")
                loadingDialog?.cancel()
                Utility.displayMessage(getString(R.string.error),this)
            })
        } catch (e: Exception){
            Log.e("EscalationAction", "Exception onFailure " + e.message)
        }
    }

    override fun onGetEscalationListSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("EscalationList","onSuccess")
                loadingDialog?.cancel()
                mBinding?.swipeChats?.isRefreshing = false
                enqID?.let { mChatVM.getEscalationList(it) }
                setChatDetails()
            })
        } catch (e: Exception){
            Log.e("EscalationList", "Exception onSuccess " + e.message)
        }
    }

    override fun onGetEscalationListFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("EscalationList","onFailure")
                mBinding?.swipeChats?.isRefreshing = false
                enqID?.let { mChatVM.getEscalationList(it) }
                setChatDetails()
            })
        } catch (e: Exception){
            Log.e("EscalationList", "Exception onFailure " + e.message)
        }
    }

    override fun onMarkResolved(escalationId: Long?) {
        if(Utility.checkIfInternetConnected(this)){
            loadingDialog?.show()
            escalationId?.let { mChatVM?.markResolveEscalation(it) }
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),this)
        }
    }

}