package com.adrosonic.craftexchange.ui.modules.chat

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatLogUserData
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.databinding.ActivityChatLogBinding
import com.adrosonic.craftexchange.ui.modules.chat.adapter.InitiatedChatRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import io.realm.RealmResults


fun Context.chatLogDetailsIntent(): Intent {
    return Intent(this, ChatLogDetailsActivity::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
}

private var mBinding : ActivityChatLogBinding ?= null
var enquiryId : Long ?= 0
var artisanId : Long ?= 0
var chatDetails : ChatLogUserData?= null
var chatHeaderDetails : ChatUser?= null


class  ChatLogDetailsActivity : AppCompatActivity(), ChatListViewModel.OpenChatLogInterface
{


    val mChatVM: ChatListViewModel by viewModels()
    private var mBinding : ActivityChatLogBinding ?= null
    val mViewModel: ChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mChatVM.openChatLogListner = this

        mBinding = ActivityChatLogBinding.inflate(layoutInflater)

        val view = mBinding?.root
        setContentView(view)

        enquiryId = intent.getStringExtra(ConstantsDirectory.ENQUIRY_ID).toLong()

        setChatHeaderDetails(enquiryId)

        mViewModel?.openChatLog(enquiryId!!)
        //getChatDetails(enquiryId)
    }


    fun getChatDetails(enquiryId : Long?){
        chatDetails = ChatUserPredicates.getChatDetails(enquiryId)

    }

    fun setChatHeaderDetails(enquiryId : Long?){
        var chatHeader = ChatUserPredicates.getChatHeaderDetailsFromId(enquiryId)
        var profile = chatHeader?.buyerLogo.toString()
        var companyName = chatHeader?.buyerCompanyName.toString()
        var url : String ?=""

        if(profile !=null) {
            url = Utility.getBrandLogoUrl(enquiryId,profile)
        }else{
            url = Utility.getProfilePhotoUrl(enquiryId,profile)
        }
        mBinding?.chatProfileImage?.let {
            ImageSetter.setImage(applicationContext,url,
                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        }
        mBinding?.brandName?.text = companyName
        mBinding?.txtEnquiryNo?.text = chatHeader?.enquiryNumber.toString()
    }



    override fun onOpenChatLogSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                Toast.makeText(applicationContext,"this is toast message",Toast.LENGTH_SHORT).show()
//                mBinding?.swipeOngoingChats?.isRefreshing = false
//
//                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData().value)
//                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatVM.getUninitiatedChatListMutableData().value)
//                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onOpenChatLogFailure() {
        TODO("Not yet implemented")
    }

}
