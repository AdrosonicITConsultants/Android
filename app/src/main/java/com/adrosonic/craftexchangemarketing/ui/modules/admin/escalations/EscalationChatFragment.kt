package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.EscalationChatFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationData
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.userData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.chat.chatLogDetailsIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.ChatRecyclerAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EscalationViewModel

class EscalationChatFragment :Fragment(),
    EscalationViewModel.EscChat,
    EscalationViewModel.UserData,
    EscalationViewModel.EscalationCount{

    private var mBinding: EscalationChatFragmentBinding?= null
    var mChatListAdapter : ChatRecyclerAdapter?=null
    val mEVM : EscalationViewModel by viewModels()
    var pageNo : Long?=null
    var scrollcall = 0
    var enqSearch : String?=null
    var escalationList : ArrayList<EscalationData> = arrayListOf()
    var id:Long?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = EscalationChatFragmentBinding.inflate(layoutInflater)

//        mBinding = DataBindingUtil.inflate(inflater, R.layout.escalation_chat_fragment, container, false)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEVM.chatListener = this
        mEVM.userListener =this
        mEVM.countlistener =this

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            pageNo = 1L
            enqSearch = null
            mEVM?.chatUpdates(pageNo!!,enqSearch)
            mEVM?.escUpdatesCount("4,5",enqSearch)
            mBinding?.pbLoader?.visibility=View.VISIBLE


            hideKeyboard(requireView())
        }
        mBinding?.closeContactDetails?.setOnClickListener {
            mBinding?.updatesdialogLayout?.visibility = View.GONE
            mBinding?.updatesDialog?.visibility = View.GONE
            mBinding?.contactDetailsLayout?.visibility = View.GONE
        }
        mBinding?.closeUpdateDialog?.setOnClickListener {
            mBinding?.updatesdialogLayout?.visibility = View.GONE
            mBinding?.updatesDialog?.visibility = View.GONE
        }
        mBinding?.viewChat?.setOnClickListener {
            startActivity(id?.let { it1 -> context?.chatLogDetailsIntent(it1) })
        }
        mBinding?.viewContactUpdates?.setOnClickListener {
//            mBinding?.updatesdialogLayout?.visibility = View.GONE
            mBinding?.updatesDialog?.visibility = View.GONE
            Log.d("updatesClick", "onViewCreated: "+id!!)
            mEVM?.userDetails(id!!)

//            Utility.displayMessage("clicked 0n "+id.toString(), requireContext())

        }
        mBinding?.SearchBtn?.setOnClickListener {
            val enquiryId1 =  mBinding?.searchByEnq?.text
            mBinding?.SearchBtn?.clearFocus();
            hideKeyboard(requireView())
            if(enquiryId1?.equals("")!!)
            {
            }
            else{
                enqSearch = mBinding?.searchByEnq?.text.toString()
                pageNo = 1
                Log.d("searchEnq", "onCreate: " + enqSearch)
                mEVM?.chatUpdates(pageNo!!,enqSearch)
                mEVM?.escUpdatesCount("4,5",enqSearch)
                mBinding?.pbLoader?.visibility=View.VISIBLE



            }
        }

//        setRecyclerView()
        mBinding?.UpdatesRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
//                    Toast.makeText(this@YourActivity, "Last", Toast.LENGTH_LONG).show()
                    Log.d("-----", "end")
                    if(scrollcall == 0)
                    {
                        pageNo = pageNo?.plus(1)
                        scrollcall = 1

                        mEVM?.chatUpdates(pageNo!!,enqSearch)
                    }

                }
            }
        })
    }
    fun hideKeyboard(view: View) {
        val imm: InputMethodManager = view.context
            .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun setRecyclerView(ed: ArrayList<EscalationData>){
        mBinding?.UpdatesRecyclerView?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        mChatListAdapter = ChatRecyclerAdapter(requireContext() ,ed ){ esc ->
            Log.d("updatesClick", "layoutclicked: " + ed)
            mBinding?.updatesDialog?.visibility = View.VISIBLE
            mBinding?.updatesdialogLayout?.visibility = View.VISIBLE
            id = esc.enquiryId
        }
        mBinding?.UpdatesRecyclerView?.adapter = mChatListAdapter
    }

    override fun EscChatSuccess(ed: ArrayList<EscalationData>) {
        if(pageNo==1.toLong())
        {
            Log.e("EscalationVMC","fn recalled ")

            mBinding?.UpdatesRecyclerView?.smoothScrollToPosition(0)
            escalationList?.clear()
            ed?.forEach{
                escalationList?.add(it)
            }
            setRecyclerView(ed)
            mBinding?.UpdatesRecyclerView?.smoothScrollToPosition(0)

        }
        else{
            Log.e("EscalationVMC","scroll fn called ")

            ed?.forEach{
                escalationList.add(it)
            }
            mChatListAdapter?.updateProductList(escalationList)
        }
        mBinding?.pbLoader?.visibility=View.GONE
        scrollcall = 0

    }

    override fun updateCount(c: Long) {
        mBinding?.EscalationCount?.text = c.toString()
        if(c == 0L){
            mBinding?.noResuts?.text = "No Results Found"
        }
    }

    override fun contacdetails(cd: ArrayList<userData>) {
        if(cd.size !=0)
        {
            mBinding?.artMail?.text = cd[0].artisanMail
            mBinding?.Artmobile?.text = cd[0].artisanContact
            mBinding?.buyerAlt?.text = cd[0].buyerAlternateContact + "(alternete)"
            mBinding?.buyerMail?.text = cd[0].buyerMail
            mBinding?.buyerMobile?.text = cd[0].buyerContact
            mBinding?.pocmail?.text = cd[0].pocEmail
            mBinding?.pocmobile?.text = cd[0].pocContact
            mBinding?.Pocname?.text = cd[0].pocFirstName + cd[0].pocLastName
            Log.d("updatesClick", "contacdetails: "+cd[0])
            mBinding?.pbLoader?.visibility=View.GONE
            mBinding?.contactDetailsLayout?.visibility = View.VISIBLE

        }
    }

}