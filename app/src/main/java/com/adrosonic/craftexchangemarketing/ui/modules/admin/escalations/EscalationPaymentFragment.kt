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
import com.adrosonic.craftexchangemarketing.databinding.EscalationFaultyFragmentBinding
import com.adrosonic.craftexchangemarketing.databinding.EscalationPaymentFragmentBinding
import com.adrosonic.craftexchangemarketing.databinding.EscalationUpdatesFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.ChatRecyclerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.PaymentRecyclerAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EscalationViewModel

class EscalationPaymentFragment:Fragment(),
    EscalationViewModel.EscPayment{

    private var mBinding: EscalationPaymentFragmentBinding?= null
    var mChatListAdapter : PaymentRecyclerAdapter?=null
    val mEVM : EscalationViewModel by viewModels()
    var pageNo : Long?=null
    var scrollcall = 0
    var enqSearch : String?=null
    var escalationList : ArrayList<EscalationData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = EscalationPaymentFragmentBinding.inflate(layoutInflater)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEVM.paymentListener = this

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            pageNo = 1L
            enqSearch = null
            mEVM?.PaymentUpdates(pageNo!!,enqSearch)
            hideKeyboard(requireView())
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
                mEVM?.PaymentUpdates(pageNo!!,enqSearch)

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

                        mEVM?.PaymentUpdates(pageNo!!,enqSearch)
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
        mChatListAdapter = PaymentRecyclerAdapter(requireContext() ,ed )
        mBinding?.UpdatesRecyclerView?.adapter = mChatListAdapter
    }

    override fun EscPaymentSuccess(ed: ArrayList<EscalationData>) {
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
//        mBinding?.pbLoader?.visibility=View.GONE
        scrollcall = 0

    }
}