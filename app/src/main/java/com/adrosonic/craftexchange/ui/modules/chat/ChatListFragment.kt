package com.adrosonic.craftexchange.ui.modules.chat

import android.graphics.ColorSpace
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.databinding.FragmentChatListBinding
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchange.ui.modules.buyer.search.BuyerSearchResultFragment
import com.adrosonic.craftexchange.ui.modules.buyer.search.BuyerSuggestionFragment
import com.adrosonic.craftexchange.ui.modules.chat.adapter.InitiatedChatRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.chat.adapter.UninitiatedChatRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.search.SuggestionAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel
import com.bumptech.glide.load.model.Model
import com.wajahatkarim3.easyvalidation.core.view_ktx.textEqualTo
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChatListFragment: Fragment(), ChatListViewModel.ChatListInterface, ChatListViewModel.InitiateChatInterface,UninitiatedChatRecyclerAdapter.initiateChatInterface, InitiatedChatRecyclerAdapter.openChatLogInterface,
    ChatListViewModel.OpenChatLogInterface, SearchViewModel.FetchBuyerSuggestions {

    private var param1: String? = null
    private var param2: String? = null

    val mChatVM : ChatListViewModel by viewModels()

    var mInitiatedChatListAdapter : InitiatedChatRecyclerAdapter?= null
    var mUninitiatedChatListAdapter : UninitiatedChatRecyclerAdapter? = null

    var mChatList : RealmResults<ChatUser>?= null
    var mFilteredList : RealmResults<ChatUser>?= null
    var mSearchedList : RealmResults<ChatUser>?= null

    var mBinding : FragmentChatListBinding?= null

    var filterList : Array<String> ?= null
    var productID : Long ?= 0L

    val mViewModel : SearchViewModel by viewModels()
    var adapter : SuggestionAdapter?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(com.adrosonic.craftexchange.ui.modules.chat.ARG_PARAM1)
            param2 = it.getString(com.adrosonic.craftexchange.ui.modules.chat.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false)
        return mBinding?.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         mChatVM?.chatListner = this
         mChatVM?.initiateChatListner = this
         mChatVM?.openChatLogListner = this

         setInitiatedChatRecyclerList()
         setUnInitiatedChatRecyclerList()

         if (!Utility.checkIfInternetConnected(requireContext())) {
             Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
         } else {
             mChatVM.getInitiatedChatList()
             mChatVM.getUninitiatedChatList()
         }

        mChatVM.getInitiatedChatListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<ChatUser>> {
                mChatList = it
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatList)
            })

        mChatVM.getUninitiatedChatListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<ChatUser>> {
                mChatList = it
                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatList)
            })

         mBinding?.initiateChat?.setOnClickListener {
             if(mBinding?.chatInitiatedLayout?.visibility == View.GONE){
                 mBinding?.chatInitiatedLayout?.visibility = View.VISIBLE
                 mBinding?.chatUninitiatedLayout?.visibility = View.GONE
             }
             else{
                 mBinding?.chatUninitiatedLayout?.visibility = View.VISIBLE
                 mBinding?.chatInitiatedLayout?.visibility = View.GONE
             }
         }




//         mBinding?.searchOngoChat?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//             override fun onQueryTextSubmit(query:String):Boolean {
//                 mSearchedList = ChatUserPredicates.getChatByEnquiryId(query.toLong())
//                 if( mBinding?.chatInitiatedLayout?.visibility == View.VISIBLE){
//                     mInitiatedChatListAdapter?.updateInitiatedChatList(mSearchedList)
//                 }
//                 else{
//                     mUninitiatedChatListAdapter?.updateUninitiatedChatList(mSearchedList)
//                 }
//
//                 return false
//             }
//             override fun onQueryTextChange(newText:String):Boolean {
////                mSearchedList = TransactionPredicates.getTransactionByEnquiryId(newText.toLong())
////                mTranListAdapter?.updateTransactionList(mSearchedList)
//                 return false
//             }
//         })

//         mViewModel.buySugListener = this
//
//         var search = activity?.findViewById<SearchView>(R.id.search_ongo_chat)
//         search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//             override fun onQueryTextSubmit(query:String):Boolean {
//                 return false
//             }
//             override fun onQueryTextChange(newText:String):Boolean {
//                 if(Utility?.checkIfInternetConnected(requireContext())){
//                     activity?.supportFragmentManager?.beginTransaction()
//                         ?.replace(R.id.search_container, ChatListFragment.newInstance())
//                         ?.addToBackStack(null)
//                         ?.commit()
//                 }else{
//                     Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
//                 }
//                 return false
//             }
//         })

//         var search = activity?.findViewById<SearchView>(R.id.search_ongo_chat)
//         search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//             override fun onQueryTextSubmit(query:String):Boolean {
//                 return false
//             }
//             override fun onQueryTextChange(newText:String):Boolean {
//                 if(Utility?.checkIfInternetConnected(requireContext())){
//                     activity?.supportFragmentManager?.beginTransaction()
//                         ?.replace(R.id.search_container, BuyerSuggestionFragment.newInstance())
//                         ?.addToBackStack(null)
//                         ?.commit()
//                 }else{
//                     Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
//                 }
//                 return false
//             }
//         })


         setVisiblities()

         mBinding?.swipeOngoingChats?.isRefreshing = true
         mBinding?.swipeOngoingChats?.setOnRefreshListener {
             if (!Utility.checkIfInternetConnected(requireContext())) {
                 Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
             } else {
                 mChatVM.getInitiatedChatList()
                 mChatVM.getUninitiatedChatList()
             }
         }

}


    private fun setInitiatedChatRecyclerList(){


        mBinding?.initiatedChatListView?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        mInitiatedChatListAdapter = InitiatedChatRecyclerAdapter(
            requireContext(),
            mChatVM.getInitiatedChatListMutableData().value!!
        )

        mBinding?.initiatedChatListView?.adapter = mInitiatedChatListAdapter
        //mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener


    }

    private fun setUnInitiatedChatRecyclerList(){
        mBinding?.uninitiatedChatListView?.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        mUninitiatedChatListAdapter = UninitiatedChatRecyclerAdapter(
            requireContext(),
            mChatVM.getUninitiatedChatListMutableData().value!!
        )

        mBinding?.uninitiatedChatListView?.adapter = mUninitiatedChatListAdapter

        mUninitiatedChatListAdapter?.initiateChatListner = this //Fragment on Click communication
    }

   fun setVisiblities() {
        if (mChatVM.getInitiatedChatListMutableData().value?.size!! > 0) {
            mBinding?.initiatedChatListView?.visibility = View.VISIBLE
           // mBinding?.emptyView?.visibility = View.GONE
        } else {
            mBinding?.initiatedChatListView?.visibility = View.GONE
          //  mBinding?.emptyView?.visibility = View.VISIBLE
        }
    }

    override fun onGetChatListSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeOngoingChats?.isRefreshing = false

                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData().value)
                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatVM.getUninitiatedChatListMutableData().value)
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onGetChatListFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeOngoingChats?.isRefreshing = false
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData().value)
                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatVM.getUninitiatedChatListMutableData().value)
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onInitiateChat(enquiryId: Long) {
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mChatVM.initiateChat(enquiryId)
        }

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mBinding?.chatInitiatedLayout?.visibility = View.VISIBLE
            mBinding?.chatUninitiatedLayout?.visibility = View.GONE

            mChatVM.getInitiatedChatList()
            mChatVM.getUninitiatedChatList()
        }


    }

    override fun openChatLog(enquiryId: Long) {
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mChatVM.openChatLog(enquiryId)
        }

    }


    override fun onInitiateChatSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeOngoingChats?.isRefreshing = false

                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData().value)
                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatVM.getUninitiatedChatListMutableData().value)

                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OnGoingInitiateChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onInitiateChatFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingChatList", "onFailure")
                mBinding?.swipeOngoingChats?.isRefreshing = false
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData().value)
                mUninitiatedChatListAdapter?.updateUninitiatedChatList(mChatVM.getUninitiatedChatListMutableData().value)
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OnGoingInitiateChat", "Exception onFailure " + e.message)
        }
    }

    override fun onOpenChatLogSuccess() {
        TODO("Not yet implemented")
    }

    override fun onOpenChatLogFailure() {
        TODO("Not yet implemented")
    }

    override fun onSuccessSugg(sug: SuggestionResponse) {
        TODO("Not yet implemented")
    }

    override fun onFailureSugg() {
        TODO("Not yet implemented")
    }

    companion object {
        fun newInstance() = ChatListFragment()

//        @JvmStatic
//        fun newInstance(param1: String,param2: Long) =
//            ChatListFragment().apply {
//                arguments = Bundle().apply {
//                    putString(com.adrosonic.craftexchange.ui.modules.chat.ARG_PARAM1, param1)
//                    putLong(com.adrosonic.craftexchange.ui.modules.chat.ARG_PARAM2, param2)
//                }
//            }
    }




}