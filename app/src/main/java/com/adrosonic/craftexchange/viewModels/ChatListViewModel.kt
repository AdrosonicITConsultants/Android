package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.database.predicates.SearchPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.chat.ChatData
import com.adrosonic.craftexchange.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.chat.ChatLogListData
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ChatListViewModel (application: Application) : AndroidViewModel(application){

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,null)}"

    var chatListner: ChatListInterface? = null

    val initiatedChatList : MutableLiveData<RealmResults<ChatUser>> by lazy { MutableLiveData<RealmResults<ChatUser>>() }
    val unInitiatedChatList : MutableLiveData<RealmResults<ChatUser>> by lazy { MutableLiveData<RealmResults<ChatUser>>() }

    var chatListener : ChatListViewModel.ChatListInterface?= null

    var initiateChatListner :ChatListViewModel.InitiateChatInterface?=null

    var openChatLogListner : ChatListViewModel.OpenChatLogInterface? = null

    interface ChatListInterface{
        fun onGetChatListSuccess()
        fun onGetChatListFailure()
        fun onInitiateChatSuccess()
        fun onInitiateChatFailure()
    }

    interface InitiateChatInterface{
        fun onInitiateChatSuccess()
        fun onInitiateChatFailure()
    }

    interface OpenChatLogInterface{
        fun onOpenChatLogSuccess()
        fun onOpenChatLogFailure()
    }




    fun loadInitiatedChatList(): RealmResults<ChatUser>?{
        var initiatedChatList = ChatUserPredicates.getInitiatedChatList()
        return initiatedChatList
    }

    fun getInitiatedChatListMutableData(): MutableLiveData<RealmResults<ChatUser>> {
        initiatedChatList.value= loadInitiatedChatList()
        return initiatedChatList
    }

    fun loadUninitiatedChatList(): RealmResults<ChatUser>?{
        var uninitiatedChatList = ChatUserPredicates.getUninitiatedChatList()
        return uninitiatedChatList
    }

    fun getUninitiatedChatListMutableData(): MutableLiveData<RealmResults<ChatUser>> {
        unInitiatedChatList.value= loadUninitiatedChatList()
        return unInitiatedChatList
    }


    val searchChat : MutableLiveData<RealmResults<ChatUser>> by lazy { MutableLiveData<RealmResults<ChatUser>>() }
    fun getBuyerSearchData(searchFilter : String): MutableLiveData<RealmResults<ChatUser>> {
        searchChat.value=loadChatSearchResults(searchFilter)
        return searchChat
    }

    private fun loadChatSearchResults(searchFilter : String): RealmResults<ChatUser>? {
        return ChatUserPredicates.chatSearch(searchFilter)
    }


   /* fun getCompChatListMutableData(): MutableLiveData<RealmResults<Transactions>> {
        completedChatList.value=loadCompChatList()
        return completedChatList
    }*/

   /* fun loadCompChatList(): RealmResults<Transactions>?{
        var completedChatList = ChatUserPredicates.getAllCompletedChats()
        return completedChatList
    }*/


   /* fun getUserChatList(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getChatService()
            .getChatList(token).enqueue(object : Callback, retrofit2.Callback<ResponseBody> { //dao
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    chatListner?.onGetChatListFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    chatListner?.onGetChatListSuccess()
//                    if(response.body()?.valid == true){
//                       // transactionListener?.onGetTransactionsSuccess()
//                       chatListner?.onGetChatListSuccess()
//                      // ChatUserPredicates.insertChat(response?.body()!!)
//                    }else{
//                      //  transactionListener?.onGetTransactionsFailure()
//                          chatListner?.onGetChatListFailure()
//                    }
                }
            })
    }*/

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


 /*   fun ifNewEnqMsgChatListExists(productId : Long,isCustom : Boolean){

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .ifEnquiryExists(token,productId,isCustom)
            .enqueue(object: Callback, retrofit2.Callback<IfExistEnquiryResponse> {
                override fun onFailure(call: Call<IfExistEnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailedEnquiryGeneration()
                    Log.e("Enquiry Generation","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<IfExistEnquiryResponse>,
                    response: retrofit2.Response<IfExistEnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Enquiry Generation","Success: "+response.body()?.errorMessage)
                        if(response.body()?.data?.ifExists == true){
                            response.body()?.data?.enquiryId?.let {
//                            TODO : save all the enquiries after login ..into DB
                                listener?.onExistingEnquiryGeneration(
                                    response.body()?.data?.productName.toString(),
                                    response.body()?.data?.enquiryId.toString(),
                                    response.body()?.data?.code.toString())
                            }

                        }else{
                            generateEnquiry(productId,isCustom,"Android")
                        }
                    }else{
                        listener?.onFailedEnquiryGeneration()
                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }*/



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


}