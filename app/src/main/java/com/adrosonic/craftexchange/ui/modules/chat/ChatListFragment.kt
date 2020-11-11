package com.adrosonic.craftexchange.ui.modules.chat

import android.graphics.ColorSpace
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.databinding.FragmentChatListBinding
import com.adrosonic.craftexchange.ui.modules.chat.adapter.InitiatedChatRecyclerAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChatListFragment: Fragment(),
    ChatListViewModel.ChatListInterface,
    ChatListViewModel.OpenChatLogInterface,
    InitiatedChatRecyclerAdapter.ChatUpdateInterface {

    private var param1: String? = null
    private var param2: String? = null

    val mChatVM : ChatListViewModel by viewModels()
    var mInitiatedChatListAdapter : InitiatedChatRecyclerAdapter?= null
    var mChatList : RealmResults<ChatUser>?= null
    var mBinding : FragmentChatListBinding?= null
    var productID : Long ?= 0L
    var initiatedChat : Long = 1L

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
         mChatVM?.openChatLogListner = this

         setInitiatedChatRecyclerList(initiatedChat)

         if (!Utility.checkIfInternetConnected(requireContext())) {
             Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
         } else {
             mChatVM.getInitiatedChatList()
             mChatVM.getUninitiatedChatList()
         }

        mChatVM.getInitiatedChatListMutableData(initiatedChat,mBinding?.searchChat?.text.toString()).observe(viewLifecycleOwner, Observer<RealmResults<ChatUser>> {
                mChatList = it
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatList)
            })

         mBinding?.initiateChat?.setOnClickListener {
             if(initiatedChat==1L){

                 initiatedChat=0

             }else initiatedChat=1
             setInitiatedChatRecyclerList(initiatedChat)
         }

         mBinding?.swipeRefreshLayout?.isRefreshing = true
         mBinding?.swipeRefreshLayout?.setOnRefreshListener {
             if (!Utility.checkIfInternetConnected(requireContext())) {
                 Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
             } else {
                 mChatVM.getInitiatedChatList()
                 mChatVM.getUninitiatedChatList()
             }
         }
         mBinding?.searchChat?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                var searchExpression = p0?.toString() ?: ""
//                if(searchExpression.length>0)  {
                    mChatVM.getInitiatedChatListMutableData(initiatedChat,searchExpression)
//                }
//                else manageSearchAndSync(false)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
}


    private fun setInitiatedChatRecyclerList(isInitiated:Long){

        mBinding?.initiatedChatListView?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false )
        if(mChatVM.getInitiatedChatListMutableData(isInitiated,mBinding?.searchChat?.text.toString()).value!=null){
        mInitiatedChatListAdapter = InitiatedChatRecyclerAdapter(requireContext(),mChatVM.getInitiatedChatListMutableData(isInitiated,mBinding?.searchChat?.text.toString()).value!!,initiatedChat )
        mBinding?.initiatedChatListView?.adapter = mInitiatedChatListAdapter
        mInitiatedChatListAdapter?.onChatReadListener=this
        }
        //mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
    }

    override fun onGetChatListSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData(initiatedChat,mBinding?.searchChat?.text.toString()).value)
                setInitiatedChatRecyclerList(initiatedChat)
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onGetChatListFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData(initiatedChat,mBinding?.searchChat?.text.toString()).value)
                setInitiatedChatRecyclerList(initiatedChat)
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onResume() {
        mBinding?.swipeRefreshLayout?.isRefreshing = true
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {

            mChatVM.getInitiatedChatList()
            mChatVM.getUninitiatedChatList()
        }
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

    override fun onChatRead(enquiryId: Long) {
        if(Utility.checkIfInternetConnected(requireContext())) {
            mBinding?.swipeRefreshLayout?.isRefreshing = true
            mChatVM?.openChatLog(enquiryId)
        }else Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
    }

    override fun onOpenChatLogSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                mChatVM.getInitiatedChatList()
                mInitiatedChatListAdapter?.updateInitiatedChatList(mChatVM.getInitiatedChatListMutableData(initiatedChat,mBinding?.searchChat?.text.toString()).value)
                setInitiatedChatRecyclerList(initiatedChat)
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }
    }

    override fun onOpenChatLogFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OnGoingTChatList", "onSuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                Utility.displayMessage(getString(R.string.unable_to_mark_read),requireContext())
            })
        } catch (e: Exception) {
            Log.e("OnGoingChatList", "Exception onSuccess " + e.message)
        }

    }


}