package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.entities.realmEntities.Escalations
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.database.predicates.SearchPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.ImageDownloadRepository
import com.adrosonic.craftexchange.repository.data.request.chat.RaiseEscalationRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.chat.ChatData
import com.adrosonic.craftexchange.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.chat.ChatLogListData
import com.adrosonic.craftexchange.repository.data.response.chat.escalations.EscalationSummResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class ChatListViewModel (application: Application) : AndroidViewModel(application){

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"

    var chatListner: ChatListInterface? = null

    val initiatedChatList : MutableLiveData<RealmResults<ChatUser>> by lazy { MutableLiveData<RealmResults<ChatUser>>() }
    val escalationList : MutableLiveData<RealmResults<Escalations>> by lazy { MutableLiveData<RealmResults<Escalations>>() }


    var chatListener : ChatListInterface?= null
    var sendChatListener : SendChatInterface?= null
    var chatMediaInterface : GetChatMediaInterface?= null

    var initiateChatListner :ChatListViewModel.InitiateChatInterface?=null

    var openChatLogListner : OpenChatLogInterface? = null

    var escalationListListener : EscalationListInterface ?= null
    var actionEscalationListener : EscalationActionInterface ?= null

    interface EscalationListInterface{
        fun onGetEscalationListSuccess()
        fun onGetEscalationListFailure()
    }

    interface EscalationActionInterface{
        fun onESCActionSuccess()
        fun onESCActionFailure()
    }

    interface ChatListInterface{
        fun onGetChatListSuccess()
        fun onGetChatListFailure()
    }
    interface SendChatInterface{
        fun onChatSentSuccess()
        fun  onChatSentFailure()
    }
    interface GetChatMediaInterface{
        fun onChatMediaSuccess(imageName:String)
        fun  onChatMediaFailure()
    }
    interface InitiateChatInterface{
        fun onInitiateChatSuccess()
        fun onInitiateChatFailure()
    }

    interface OpenChatLogInterface{
        fun onOpenChatLogSuccess()
        fun onOpenChatLogFailure()
    }

    fun loadInitiatedChatList(isInitiated:Long,searchString:String): RealmResults<ChatUser>?{
        var initiatedChatList = ChatUserPredicates.getInitiatedChatList(isInitiated,searchString)
        return initiatedChatList
    }

    fun getEscalationList(enquiryId : Long): MutableLiveData<RealmResults<Escalations>> {
        escalationList.value= loadEscalationList(enquiryId)
        return escalationList
    }

    fun loadEscalationList(enquiryId : Long): RealmResults<Escalations>?{
        var list = ChatUserPredicates.getEscalationEnquiry(enquiryId)
        return list
    }

    fun getInitiatedChatListMutableData(isInitiated:Long,searchString:String): MutableLiveData<RealmResults<ChatUser>> {
        initiatedChatList.value= loadInitiatedChatList(isInitiated,searchString)
        return initiatedChatList
    }

    fun getInitiatedChatList(){
        Log.e("Chat","Start")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"
        CraftExchangeRepository
            .getChatService()
            .getInitiatedChatList(token,null).enqueue(object : Callback, retrofit2.Callback<ChatListResponse> {
                override fun onFailure(call: Call<ChatListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Chat","onFailure")
                }
                override fun onResponse(
                    call: Call<ChatListResponse>,
                    response: Response<ChatListResponse>

                ) {
                    if(response.body()?.valid == true){
                        Log.e("Chat","Size: ${response?.body()?.data?.size}")
                        chatListner?.onGetChatListSuccess()
                        ChatUserPredicates.insertChat(response.body()!!, 1L)
                    }else
                    {
                        Log.e("Chat","${response.body()?.valid}")
                        chatListner?.onGetChatListFailure()
                    }
                }
            })
    }


    fun getUninitiatedChatList() {
        Log.e("Chat","Start")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"
        CraftExchangeRepository
            .getChatService()
            .getUninitiatedChatList(token,null).enqueue(object : Callback, retrofit2.Callback<ChatListResponse> {
                override fun onFailure(call: Call<ChatListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Chat","onFailure")
                }
                override fun onResponse(
                    call: Call<ChatListResponse>,
                    response: Response<ChatListResponse>

                ) {
                    if(response.body()?.valid == true){
                        Log.e("Chat","Size: ${response?.body()?.data?.size}")

                        chatListener?.onGetChatListSuccess()
                        ChatUserPredicates.insertChat(response.body()!!, 0L)
                    }else
                    {
                        Log.e("Chat","${response.body()?.valid}")
                        chatListener?.onGetChatListFailure()
                    }
                }
            })
    }

    fun initiateChat(enquiryId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getChatService()
            .initiateChat(token,enquiryId)
            .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    initiateChatListner?.onInitiateChatFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        initiateChatListner?.onInitiateChatSuccess()
                    }else{
                        initiateChatListner?.onInitiateChatFailure()
                    }
                }
            })
    }

    fun openChatLog(enquiryId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getChatService()
            .openChatLog(token,enquiryId)
            .enqueue(object : Callback, retrofit2.Callback<ChatLogListData> {
                override fun onFailure(call: Call<ChatLogListData>, t: Throwable) {
                    t.printStackTrace()
                    openChatLogListner?.onOpenChatLogFailure()
                }
                override fun onResponse(
                    call: Call<ChatLogListData>,
                    response: Response<ChatLogListData>
                ) {
                    if(response?.isSuccessful){
                        openChatLogListner?.onOpenChatLogSuccess()
                        ChatUserPredicates.insertOpenChatLog(response.body()!! ,1L)
                    }else{
                        openChatLogListner?.onOpenChatLogFailure()
                    }
                }
            })
    }

    fun sendChatboxMessage( messageJson : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository.getChatService()
            .sendChatboxMessage(token, messageJson)
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(
                    call: Call<NotificationReadResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    Log.e("SendChat", "getProductUploadData onFailure: " + t.localizedMessage)
                    sendChatListener?.onChatSentFailure()
                }

                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("SendChat", "onResponse : ${response.body()?.valid} and ${response.body()?.data}")
                    if (response.body()?.valid==true) {
                        sendChatListener?.onChatSentSuccess()
                    }else  sendChatListener?.onChatSentFailure()
                }
            })

    }
    fun sendChatboxMessageWithMedia( messageJson : String,fileList:String?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        var dataLength=File(fileList?:"").length()
        var boundary= UUID.randomUUID().toString()
        var headerBoundary="multipart/form-data;boundary="+boundary
        val byteData=prepareMultiPartBody(boundary,dataLength,fileList)
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData.capacity())
        val bodyMultipart = MultipartBody.Builder().addPart(body).build()
        CraftExchangeRepository.getChatService()
            .sendChatboxMessageWithMedia(token, headerBoundary,dataLength,messageJson,bodyMultipart)
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(
                    call: Call<NotificationReadResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    Log.e("SendChat", "getProductUploadData onFailure: " + t.localizedMessage)
                    sendChatListener?.onChatSentFailure()
                }

                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("SendChat", "onResponse : ${response.body()?.valid} and ${response.body()?.data}")
                    if (response.body()?.valid==true) {
                        sendChatListener?.onChatSentSuccess()
                    }else  sendChatListener?.onChatSentFailure()
                }
            })

    }
    private fun prepareMultiPartBody(
        boundary:String,
        dataLength:Long,
        imageList:String?
    ): ByteBuffer? {
        var ctr=0
        var body = ByteBuffer.allocate(dataLength.toInt()+5000)
        Log.e("SendChat","prepareMultiPartBody 1111111")
        var boundaryPrefix = "--$boundary\n"

//        imageList?.forEach {
            ctr++
            body.put(boundaryPrefix.toByteArray(StandardCharsets.UTF_8))
            val file = File(imageList)
        Log.e("SendChat","prepareMultiPartBody 2222222: ${file.name}")
//            Content-Disposition: form-data; name="file2"; filename="Screenshot_20200801-115847.png"
//            Content-Type: image/png
            var contentDisposition = "Content-Disposition: form-data; name=file; filename=${file.name}\r\n"
            body.put(contentDisposition.toByteArray(StandardCharsets.UTF_8))
            var mimetype=MediaType.parse("image/*")
            var mimeType = "Content-Type: $mimetype\r\n\r\n"
            body.put(mimeType.toByteArray(StandardCharsets.UTF_8))
            Log.e("SendChat","prepareMultiPartBody 3333 "+file.name)
            Log.e("SendChat","prepareMultiPartBody 3333 "+file.length())
            body.put(file.readBytes())
            Log.e("SendChat","prepareMultiPartBody 4444 "+ctr)
            body.put("\r\n".toByteArray(StandardCharsets.UTF_8))
            Log.e("Offline","prepareMultiPartBody 5555")
//            if(ctr==imageList.size){
                var bottomBoundaryStr = "--$boundary--"
                body.put(bottomBoundaryStr.toByteArray(StandardCharsets.UTF_8))
//            }
//        }

        return body
    }

    fun downLoadChatMedia(enquiryId:Long,imageList:String){

            Log.e("ChatMedia", "111111 : $imageList productId: $enquiryId" )
            ImageDownloadRepository
                .getChatListService()
                .getChatMedia(enquiryId, imageList)
                .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        Log.e("ChatMedia", "onFailure: " + t.localizedMessage)
                        chatMediaInterface?.onChatMediaFailure()
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.e("ChatMedia", "222222222222 : " + imageList)
                        val body = response?.body()
                        if (body != null) {
                            writeResponseBodyToDisk( body,imageList, getApplication())
                        }else chatMediaInterface?.onChatMediaFailure()
                    }

                })
    }
    fun writeResponseBodyToDisk(
        body: ResponseBody,
        imageName: String,
        context: Context
    ): Boolean {
        try {
            if (!File(context.cacheDir,  ConstantsDirectory.CHAT_MEDIA).exists()) File(context.cacheDir, ConstantsDirectory.CHAT_MEDIA).mkdir()
            Log.e("ChatMedia", "00000000 $imageName ")
            var imageName1=""
            if(imageName.endsWith("jfif",true)){
                imageName1=imageName.replace("jfif","jpg")
                Log.e("ChatMedia", "111111  $imageName1 ")
            }else imageName1=imageName
            Log.e("ChatMedia", "2222222 $imageName1 ")
            val myDir = File(context.cacheDir, "/"+ ConstantsDirectory.CHAT_MEDIA+"/$imageName1")

            var inputStream: InputStream = body.byteStream()
            var outputStream: OutputStream = FileOutputStream(myDir)
            try {
                var fileReader = ByteArray(4096)

                var fileSize = body.contentLength()
                var fileSizeDownloaded = 0
                while (true) {
                    var read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read
                }
                outputStream.flush()
                Log.e("ChatMedia", "4444444444444 ")
                chatMediaInterface?.onChatMediaSuccess(imageName1)
                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
                chatMediaInterface?.onChatMediaFailure()
            }
        } catch (e: IOException) {
            return false
        }
    }

    fun getEscalationsList(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"
        CraftExchangeRepository
            .getChatService()
            .getEscalationSummary(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<EscalationSummResponse> {
                override fun onFailure(call: Call<EscalationSummResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Escalation List","onFailure")
                    escalationListListener?.onGetEscalationListFailure()
                }
                override fun onResponse(
                    call: Call<EscalationSummResponse>,
                    response: Response<EscalationSummResponse>
                ) {
                    if(response.body()?.valid == true){
                        ChatUserPredicates?.insertEscalation(response?.body()!!)
                        escalationListListener?.onGetEscalationListSuccess()
                    }else
                    {
                        escalationListListener?.onGetEscalationListFailure()
                    }
                }
            })
    }

    fun markResolveEscalation(escalationId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"
        CraftExchangeRepository
            .getChatService()
            .resolveEscalation(token,escalationId).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Escalation","onFailure")
                    actionEscalationListener?.onESCActionFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        actionEscalationListener?.onESCActionSuccess()
                    }else
                    {
                        actionEscalationListener?.onESCActionFailure()
                    }
                }
            })
    }

    fun raiseEscalation(escRequest : RaiseEscalationRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"
        CraftExchangeRepository
            .getChatService()
            .raiseEscalation(token,escRequest).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Escalation","onFailure")
                    actionEscalationListener?.onESCActionFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        actionEscalationListener?.onESCActionSuccess()
                    }else
                    {
                        actionEscalationListener?.onESCActionFailure()
                    }
                }
            })
    }

}