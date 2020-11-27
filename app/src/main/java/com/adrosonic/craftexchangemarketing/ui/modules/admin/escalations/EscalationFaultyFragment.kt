package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.EscalationFaultyFragmentBinding
import com.adrosonic.craftexchangemarketing.databinding.EscalationPaymentFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationData
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.GenerateEnqResponse
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.chat.chatLogDetailsIntent
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.FaultyRecyclerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.PaymentRecyclerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter.UpdatesRecyclerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.selectLessThan8ArtisanActivityIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EscalationViewModel
import kotlinx.android.synthetic.main.fragment_notifcation.*

class EscalationFaultyFragment :Fragment(),
    EscalationViewModel.EscFaulty,
    EscalationViewModel.EscalationCount,
    EscalationViewModel.GenEnqInterface,
EscalationViewModel.EscalationResolve{
    private var mBinding: EscalationFaultyFragmentBinding?= null
    var mChatListAdapter : FaultyRecyclerAdapter?=null
    val mEVM : EscalationViewModel by viewModels()
    var pageNo : Long?=null
    var scrollcall = 0
    var enqSearch : String?=null
    var escalationList : ArrayList<EscalationData> = arrayListOf()
    var id:Long?=null
    var escId : Long?=null
    var price=0L
    var artistBrand : String?=""
    var buyerBrand : String?=""
    var enquiryCode : String?=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = EscalationFaultyFragmentBinding.inflate(layoutInflater)

//        mBinding = DataBindingUtil.inflate(inflater, R.layout.escalation_faulty_fragment, container, false)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEVM.faultyListener = this
        mEVM.countlistener =this
        mEVM.resolvedListener =this


        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            pageNo = 1L
            enqSearch = null
            mEVM?.FaultyUpdates(pageNo!!,enqSearch)
            mEVM?.escUpdatesCount("2,3,7",enqSearch)
            mBinding?.pbLoader?.visibility=View.VISIBLE


            hideKeyboard(requireView())
        }
        mBinding?.closeUpdateDialog?.setOnClickListener {
            mBinding?.updatesdialogLayout?.visibility = View.GONE
            mBinding?.updatesDialog?.visibility = View.GONE
        }
        mBinding?.markResoved?.setOnClickListener {
            escId?.let { it1 -> mEVM.markResolved(it1) }
            mBinding?.pbLoader?.visibility=View.VISIBLE

        }
        mBinding?.viewChat?.setOnClickListener {
            Log.d("chatEsc", "onViewCreated: "+id)
            startActivity(id?.let { it1 -> context?.chatLogDetailsIntent(it1) })
        }
        
        mBinding?.generateNew?.setOnClickListener {
            showRedirectDialog()
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
                mEVM?.FaultyUpdates(pageNo!!,enqSearch)
                mEVM?.escUpdatesCount("2,3,7",enqSearch)

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

                        mEVM?.FaultyUpdates(pageNo!!,enqSearch)
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
        mChatListAdapter = FaultyRecyclerAdapter(requireContext() ,ed ){ esc->
            Log.d("updatesClick", "layoutclicked: " + ed)
            mBinding?.updatesDialog?.visibility = View.VISIBLE
            mBinding?.updatesdialogLayout?.visibility = View.VISIBLE
            id = esc.enquiryId
            escId = esc.escalationId
            enquiryCode=esc.enquiryCode
            price=esc.price
            artistBrand=esc.artistBrand
            buyerBrand=esc.buyerBrand
        };
        mBinding?.UpdatesRecyclerView?.adapter = mChatListAdapter
    }

    override fun EscFaultySuccess(ed: ArrayList<EscalationData>) {
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
        }    }

    override fun escalationResolved(m: String) {

        Utility.displayMessage(m, requireContext())
        mBinding?.updatesdialogLayout?.visibility = View.GONE
        mBinding?.updatesDialog?.visibility = View.GONE
        pageNo = 1L
        enqSearch = null
        mEVM?.FaultyUpdates(pageNo!!,enqSearch)
        mEVM?.escUpdatesCount("2,3,7",enqSearch)
    }

    override fun genEnqSuccess(ed: GenerateEnqResponse) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SendCutEnq","genEnqSuccess")
                showNewEnqDetDialog()
                mBinding?.pbLoader?.visibility=View.GONE
            }
            )
        } catch (e: Exception) {
        }
    }

    override fun genEnqFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                mBinding?.pbLoader?.visibility=View.GONE
             Utility.displayMessage("Unable to generate enquiry",requireContext())
            }
            )
        } catch (e: Exception) {
        }
    }

    fun showRedirectDialog() {
        var dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.dialog_unresolved_redirect_enq)
        dialog?.show()
        dialog.setCanceledOnTouchOutside(true)
        val txt_enq_code = dialog?.findViewById(R.id.txt_enq_code) as TextView
        val txt_art_details = dialog?.findViewById(R.id.txt_art_details) as TextView
        val txt_buyer_details = dialog?.findViewById(R.id.txt_buyer_details) as TextView
        val txt_gen_new_enq = dialog?.findViewById(R.id.txt_gen_new_enq) as TextView
        txt_enq_code.text=enquiryCode
        txt_art_details.text="₹ $price\n $artistBrand"
        txt_buyer_details.text=buyerBrand
        txt_gen_new_enq.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireContext())) {
                mEVM?.genEnqListener = this
                mBinding?.pbLoader?.visibility = View.VISIBLE
                mEVM?.createNewEnquiry(id ?: 0)
                dialog.dismiss()
            }else Utility.displayMessage(requireContext().getString(R.string.no_internet_connection),requireContext())
        }
    }

    fun showNewEnqDetDialog() {
        var dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.dialog_new_enq_details)
        dialog?.show()
        dialog.setCanceledOnTouchOutside(true)
        val txt_enq_code = dialog?.findViewById(R.id.txt_enq_code) as TextView
        val txt_click_here = dialog?.findViewById(R.id.txt_click_here) as TextView
        val txt_choose_artisans = dialog?.findViewById(R.id.txt_choose_artisans) as TextView
        txt_enq_code.text=enquiryCode
        txt_click_here.setOnClickListener {
//            if(enquiryCode!!.contains("c"))
//            else
        }
        txt_choose_artisans.setOnClickListener {
            dialog.cancel()
//            context?.startActivity(context?.SelectArtisanForEnqActivity(id?:0))
            startActivityForResult(requireContext().SelectArtisanForEnqActivity(id?:0), ConstantsDirectory.RESULT_FAULTY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsDirectory.RESULT_FAULTY) { // Please, use a final int instead of hardcoded int value
            if (resultCode == Activity.RESULT_OK) {
                try {
                    mBinding?.updatesdialogLayout?.visibility = View.GONE
                    mBinding?.updatesDialog?.visibility = View.GONE
                    pageNo = 1L
                    enqSearch = null
                    mEVM?.FaultyUpdates(pageNo!!,enqSearch)
                    mEVM?.escUpdatesCount("2,3,7",enqSearch)
                }catch (e:Exception){}
            }
        }


    }
}