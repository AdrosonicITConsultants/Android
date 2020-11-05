package com.adrosonic.craftexchange.ui.modules.chat

//import com.github.bassaer.chatmessageview.model.Message
//import com.github.bassaer.chatmessageview.model.Message.Companion.STATUS_ICON_RIGHT_ONLY
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.databinding.ActivityChatLogBinding
import com.adrosonic.craftexchange.repository.data.request.chat.SendChatRequest
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import com.github.bassaer.chatmessageview.model.Message
import com.github.bassaer.chatmessageview.model.Message.OnIconClickListener
import com.github.bassaer.chatmessageview.view.MessageView
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.io.IOException


fun Context.chatLogDetailsIntent(): Intent {
    return Intent(this, ChatLogDetailsActivity::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
}

class  ChatLogDetailsActivity : AppCompatActivity(),
    ChatListViewModel.OpenChatLogInterface,
    ChatListViewModel.GetChatMediaInterface,
    ChatListViewModel.SendChatInterface {
    var enquiryId : Long ?= 0
    var artisanId : Long ?= 0
    var fromId : Long = 0
    var toId : Long = 0
    var buyerName : String = ""
    val mChatVM: ChatListViewModel by viewModels()
    private var mBinding : ActivityChatLogBinding ?= null
    private var mUsers: ArrayList<User>? = null
    private var chatUserDetails:ChatUser?=null
    private var mediaType=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatLogBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        this!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        mChatVM.openChatLogListner = this
        mChatVM.chatMediaInterface = this
        mChatVM.sendChatListener = this
        if (intent.extras != null) {
            enquiryId = intent.getLongExtra(ConstantsDirectory.ENQUIRY_ID, 0)
        }
        enquiryId?.let {
            chatUserDetails=ChatUserPredicates.getChatDetailsByEnquiryId(it)
        }
        if(Utility.checkIfInternetConnected(this)) {
            mBinding?.swipeChats?.isRefreshing=true
            mChatVM?.openChatLog(enquiryId!!)
        }
        setChatHeaderDetails(enquiryId)
        initUsers()
        loadMessages()
        setChatView()

        mBinding?.btnBack?.setOnClickListener {
            this.onBackPressed()
        }
        mBinding?.swipeChats?.setOnRefreshListener {
            mChatVM?.openChatLog(enquiryId!!)
        }
        mBinding?.txtGotoEnq?.setOnClickListener {
            Log.e("ViewEnquiry","11111111111")
            val intent = Intent(this?.enquiryDetails())
            var bundle = Bundle()
            Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId?.toString()) //TODO change later
            bundle.putString(ConstantsDirectory.ENQUIRY_ID, enquiryId?.toString())
            bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, "2")
            intent.putExtras(bundle)
            this?.startActivity(intent)
        }
        mBinding?.txtSwipeClose?.setOnClickListener {
            val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
            val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

            if(mBinding?.chatDetailsLayer?.visibility== View.VISIBLE){
                mBinding?.chatDetailsLayer?.visibility=View.GONE
                mBinding?.chatDetailsLayer?.animation = slideUp
            }
            else {
                mBinding?.chatDetailsLayer?.visibility=View.VISIBLE
                mBinding?.chatDetailsLayer?.animation = slideDown
            }
        }
        mBinding?.viewDetailsText?.setOnClickListener {
            val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
            val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

             if(mBinding?.chatDetailsLayer?.visibility== View.VISIBLE){
                 mBinding?.chatDetailsLayer?.visibility=View.GONE
                 mBinding?.chatDetailsLayer?.animation = slideUp
             }
            else {
                 mBinding?.chatDetailsLayer?.visibility=View.VISIBLE
                 mBinding?.chatDetailsLayer?.animation = slideDown
             }
        }
        mBinding?.chatView?.setOnClickSendButtonListener(View.OnClickListener {
            var chatMessage=mBinding?.chatView?.inputText?:""
            if(chatMessage.isNotEmpty()) {
                if (Utility.checkIfInternetConnected(this)) {
                    mChatVM?.sendChatListener = this
                    mBinding?.swipeChats?.isRefreshing = true
                    var sendChatRequest = SendChatRequest()
                    sendChatRequest.enquiryId = enquiryId ?: 0
                    sendChatRequest.mediaType = 1//todo
                    sendChatRequest.messageFrom = fromId
                    sendChatRequest.messageTo = toId
                    sendChatRequest.messageString = chatMessage
                    mChatVM?.sendChatboxMessage(sendChatRequest.toString())
                } else Utility.displayMessage(getString(R.string.no_internet_connection), this)
                mBinding?.chatView?.inputText = ""
            }
        })
        mBinding?.chatView?.setOnClickOptionButtonListener(View.OnClickListener {
            showDialog()
        })
        chat_view?.setOnBubbleClickListener(object: Message.OnBubbleClickListener{
            override fun onClick(message: Message) {
                Log.e("ViewEnquiry","OnBubbleClickListener")
//                Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
                if(message.type!=Message.Type.TEXT){
                if(Utility.checkIfInternetConnected(applicationContext)) {
                    mBinding?.swipeChats?.isRefreshing = true
                    val filename = message.text?.replace("File: ","")
                    mChatVM.downLoadChatMedia(enquiryId ?: 0, filename ?: "")
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
                }
                }
            }
        })
    }

    fun setChatHeaderDetails(enquiryId : Long?){
        var chatHeader = ChatUserPredicates.getChatHeaderDetailsFromId(enquiryId)
        var profile = chatHeader?.buyerLogo.toString()
        var companyName = chatHeader?.buyerCompanyName.toString()

        mBinding?.brandName?.text = companyName
        mBinding?.txtEnquiryNo?.text = chatHeader?.enquiryNumber.toString()
        mBinding?.enquiryStatusText?.text = chatHeader?.orderStatus?:""
        chatUserDetails?.let {
            mBinding?.txtDateStarted?.text="Date started: "+try{chatUserDetails?.enquiryGeneratedOn?.split("T")?.get(0)}catch (e:java.lang.Exception){"NA"}
            mBinding?.txtDateConvertedToOrder?.text="Converted to order on: "+try{chatUserDetails?.convertedToOrderDate?.split("T")?.get(0)}catch (e:java.lang.Exception){"NA"}
            mBinding?.txtDateLastUpdated?.text="Last updated on: "+try{chatUserDetails?.lastUpdatedOn?.split("T")?.get(0)?:"NA"}catch (e:java.lang.Exception){"NA"}
            mBinding?.txtProdType?.text=chatUserDetails?.productTypeId
            mBinding?.txtOrderAmt?.text="Order amount: ₹ "+chatUserDetails?.orderAmouunt?.toString()
        }
        fromId=UserConfig.shared.userId?.toLong()?:0
        toId=chatUserDetails?.buyerId?:0
        buyerName=chatUserDetails?.buyerCompanyName?:""

        var url = Utility.getBrandLogoUrl(toId,chatUserDetails?.buyerLogo)
        mBinding?.chatProfileImage?.let {
            ImageSetter.setImage(applicationContext,url,it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }
    }

    fun setChatView(){
        mBinding?.chatView?.getMessageView()?.setRightBubbleColor( R.color.chat_right_bubble_color)
//        mBinding?.chatView?.setRightBubbleColor( R.color.chat_right_bubble_color)
//        mBinding?.chatView?.setLeftBubbleColor(R.color.white_text)
//        mBinding?.chatView?.setBackgroundColor( ContextCompat.getColor(this, com.github.bassaer.example.MessengerActivity.BACKGROUND_COLOR ))
        mBinding?.chatView?.setSendButtonColor(ContextCompat.getColor( this,  R.color.darker_gray))
//        mBinding?.chatView?.setSendIcon(com.github.bassaer.example.MessengerActivity.SEND_ICON)
        mBinding?.chatView?.setOptionIcon(R.drawable.ic_attach_files)
        mBinding?.chatView?.setOptionButtonColor(R.color.teal500)
        mBinding?.chatView?.setRightMessageTextColor(ContextCompat.getColor( this,  R.color.black_text))
        mBinding?.chatView?.setLeftMessageTextColor(ContextCompat.getColor( this,  R.color.black_text))
        mBinding?.chatView?.setUsernameTextColor(R.color.clickable_text_color)
        mBinding?.chatView?.setSendTimeTextColor(R.color.gray200)
        mBinding?.chatView?.setDateSeparatorColor(R.color.white_text)
        mBinding?.chatView?.setMessageStatusTextColor(R.color.white_text)
        mBinding?.chatView?.setInputTextHint("Type your message here")
        mBinding?.chatView?.setMessageMarginTop(4)
        mBinding?.chatView?.setMessageMarginBottom(8)
        mBinding?.chatView?.setMaxInputLine(4)
        mBinding?.chatView?.setUsernameFontSize(resources.getDimension(R.dimen.font_small))
        mBinding?.chatView?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        mBinding?.chatView?.inputTextColor = ContextCompat.getColor(this, R.color.black_text)
        mBinding?.chatView?.setInputTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    }

    override fun onOpenChatLogSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeChats?.isRefreshing=false
                loadMessages()
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onOpenChatLogFailure() {
    }

    private fun loadMessages() {
        val messages: MutableList<Message> =ArrayList()
        var mMessageList = GetMessageList.getMessageList(this,enquiryId?:0,buyerName)
        if (mMessageList == null) {
            mMessageList = ArrayList()
        } else {
            for (i in 0 until mMessageList.size) {
                val message = mMessageList[i]
                if (!message.isDateCell && message.isRight) {
                    message.hideIcon(true)
                }
                messages.add(message)
            }
        }
        val messageView: MessageView = mBinding?.chatView?.getMessageView()!!
        messageView.removeAll()
        messageView.init(messages)
        messageView.setSelection(messageView.count - 1)
    }

    override fun onChatSentSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SendChat", "onSuccess")
                mBinding?.swipeChats?.isRefreshing=false
                mChatVM?.openChatLog(enquiryId!!)
                loadMessages()
            })
        } catch (e: Exception) {
            Log.e("SendChat", "onChatSentSuccess Exception " + e.message)
        }
    }

    override fun onChatSentFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SendChat", "onChatSentFailure")
                mBinding?.swipeChats?.isRefreshing=false
            })
        } catch (e: Exception) {
            Log.e("SendChat", "onChatSentFailure onSuccess " + e.message)
        }
    }

    private fun showDialog() {
//        [ { "id": 1, "type": "Text" }, { "id": 2, "type": "Docs" }, { "id": 3, "type": "Image" },
//            { "id": 4, "type": "Audio" }, { "id": 5, "type": "Video" } ]
        val items = arrayOf("Share photo","Share video","Share audio","Share document")
        AlertDialog.Builder(this)
            .setTitle("Upload to share")
            .setItems(items) { dialogInterface, position ->
                when (position) {
                    0 ->{
                        mediaType=3
                        openGallery("image/*")
                    }
                    1 -> { mediaType=5
                        openGallery("video/*")
                    }
                    2 -> { mediaType=4
                        openGallery("audio/mpeg")
                    }
                    3 ->{ mediaType=2
                        openGallery("application/*")
                    }
                }
            }
            .show()
    }
    private fun openGallery(fileType: String) {
        val intent: Intent
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
        } else {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
        intent.type = fileType
        if( mediaType==2){
            intent.putExtra( Intent.EXTRA_MIME_TYPES,  arrayOf("application/pdf","application/doc","application/msword","application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/plain"))
        }
        startActivityForResult(   intent,  100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 100 || resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        val uri = data.data
        Log.e("ChatMedia","path: ${data.getStringExtra("path")}")
        try {
            var absolutePath = Utility.getRealPathFromFileUriForChat(applicationContext, uri!!)
            Log.e("ChatMedia","absolutePath: $absolutePath")
            if(Utility.checkIfInternetConnected(this)) {
                mChatVM?.sendChatListener=this
                mBinding?.swipeChats?.isRefreshing=true
                var sendChatRequest=SendChatRequest()
                sendChatRequest.enquiryId=enquiryId?:0
                sendChatRequest.mediaType=mediaType.toLong()
                sendChatRequest.messageFrom=fromId
                sendChatRequest.messageTo=toId
                sendChatRequest.messageString=""
                mChatVM?.sendChatboxMessageWithMedia(sendChatRequest.toString(),absolutePath)
                Utility.displayMessage("Please wait..",this)
            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }
    private fun initUsers() {
        mUsers = ArrayList()
        val yourIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.buyer_logo_placeholder)
         val me =  User(fromId.toInt(),UserConfig.shared?.firstname,yourIcon)
        val you = User(toId.toInt(),buyerName,yourIcon)
        mUsers!!.add(me)
        mUsers!!.add(you)
    }

    override fun onChatMediaSuccess(imageName:String) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SendChat", "onChatMediaSuccess")
                mBinding?.swipeChats?.isRefreshing=false
                Utility.openChatMedia(this,enquiryId?:0,imageName)
            })
        } catch (e: Exception) {
        }
    }

    override fun onChatMediaFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SendChat", "onChatMediaFailure")
                mBinding?.swipeChats?.isRefreshing=false
            })
        } catch (e: Exception) {
        }
    }



}
