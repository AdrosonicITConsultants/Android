package com.adrosonic.craftexchange.ui.modules.chat

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ActivityChatEscalationBinding
import com.adrosonic.craftexchange.databinding.ActivityChatLogBinding
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.utils.Utility
import com.github.bassaer.chatmessageview.model.Message
import com.github.bassaer.chatmessageview.view.MessageView

fun Context.chatEscalationIntent(): Intent {
    return Intent(this, ChatEscalationActivity::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
}


class ChatEscalationActivity : AppCompatActivity() {

    private var mBinding : ActivityChatEscalationBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatEscalationBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mBinding?.swipeChats?.isEnabled = false
        mBinding?.iconEscalation?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.btnBack?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.btnSendEscalation?.setOnClickListener {

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
        finish()
    }

    fun showEscCatDialog() {
        var rDialog = Dialog(this)
        rDialog?.setContentView(R.layout.dialog_raise_escalation)
        rDialog?.show()
        val raiseEsc = rDialog?.findViewById(R.id.btn_dRaise_escalation) as Button
        val cancel = rDialog?.findViewById(R.id.btn_cancel_escalation) as TextView

        cancel.setOnClickListener {
            rDialog.cancel()
        }
        raiseEsc.setOnClickListener {
            rDialog.cancel()
            showConfirmEscalaltionDialog()
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
        }
    }

}