package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.qC

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Orders
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchangemarketing.database.predicates.QcPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentBuyerViewQcFormBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.ArtBuyQcResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.OrdersViewModel
import com.adrosonic.craftexchangemarketing.viewModels.QCViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerViewQcFormFragment : Fragment(),
    QCViewModel.GetQCResponseInterface {
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBuyerViewQcFormBinding? = null

    var enqID: String? = ""
    var orderStatus: String?=""

    var qcObj: QcDetails? = null
    var maxStageID: Long? = 0
    var maxStage: String? = ""

    var qcStages: InnerStageData? = null
    var qcQues: QCQuestionData? = null
    var mQcRespList: QcDetails? = null

    var weft: String? = ""
    var warp: String? = ""
    var extraweft: String? = ""
    var prodCategory: String? = ""
    var status: String? = ""

    private var orderDetails: Orders? = null
    private var url: String? = ""

    var mViewQcAdapter: ViewQcRecyclerAdapter? = null

    val mOrdVM: OrdersViewModel by viewModels()
    val mQcVM: QCViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_buyer_view_qc_form,
                container,
                false
            )
        if (param1 != null) {
            enqID = if (param1!!.isNotEmpty()) param1 else "0"
        }
        if (param2 != null) {
            orderStatus = if (param2!!.isNotEmpty()) param2 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.swipeForm?.isEnabled = false
        qcStages = Utility?.getQcStageData()
        qcQues = Utility?.getQcQuesData()
        mQcVM?.getQcListener = this
        Log.d("qccheck", "onViewCreated: "+qcStages)
        Log.d("qccheck", "onViewCreated: "+qcQues)



        orderDetails = enqID?.toLong()?.let { orderStatus?.toLong()?.let { it1 ->
            mOrdVM?.getSingleOnOrderData(it,
                it1
            )
        } }?.value
        Log.d("qccheck", "onViewCreated: "+orderDetails)

        if (orderDetails != null) {
            Log.d("qccheck", "onViewCreated: not null")
            setDetails()
        }

        if (Utility.checkIfInternetConnected(requireContext())) {
            enqID?.toLong()?.let { mQcVM?.getBuyerQCResponse(it) }
            viewLoader()
        } else {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
//            setQCStage()
            setSaveForms()
        }

        mQcVM.getQcResponsesbyEnq(enqID?.toLong()!!)
            .observe(viewLifecycleOwner, Observer<QcDetails> {
                mQcRespList = it
            })
    }

    fun setDetails() {
//        mBinding?.orderCode?.text = orderDetails?.orderCode
//        mBinding?.companyName?.text = orderDetails?.brandName
//        setProductImage()
//        setProductName()
    }

//    fun setProductImage() {
//        val image = orderDetails?.productImages?.split((",").toRegex())
//            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)
//
//        if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
//            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
//        } else {
//            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
//        }
//        mBinding?.productImage?.let {
//            ImageSetter.setImage(
//                requireActivity(),
//                url!!,
//                it,
//                R.drawable.artisan_logo_placeholder,
//                R.drawable.artisan_logo_placeholder,
//                R.drawable.artisan_logo_placeholder
//            )
//        }
//
//        mBinding?.btnBack?.setOnClickListener {
//            activity?.onBackPressed()
//        }
//    }

//    fun setProductName() {
//        //Product name or Product cloth details
//        if (orderDetails?.productName != "") {
//            mBinding?.productName?.text = orderDetails?.productName
//        } else {
//            //TODO : set text as prod cat / werft / warn / extraweft
//            var weaveList = Utility?.getWeaveType()
//            var catList = Utility?.getProductCategory()
//
//            weaveList?.forEach {
//                if (it.first == orderDetails?.weftYarnId) {
//                    weft = it.second
//                }
//                if (it.first == orderDetails?.warpYarnId) {
//                    warp = it.second
//                }
//                if (it.first == orderDetails?.extraWeftYarnId) {
//                    extraweft = it.second
//                }
//            }
//            catList?.forEach {
//                if (it.first == orderDetails?.productCategoryId) {
//                    prodCategory = it.second
//                }
//            }
//            var fp = SpannableString("${prodCategory} / ")
//            var sp = "${warp} X ${weft} X ${extraweft}"
//            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }
//                ?.let {
//                    ForegroundColorSpan(
//                        it
//                    )
//                }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            mBinding?.productName?.text = fp
//            mBinding?.productName?.append(sp)
//        }
//    }

    fun getFormTitle(stageId: Long?): String? {
        var stagename: String? = ""
        var itr = qcStages?.data?.iterator()
        if (itr != null) {
            while (itr.hasNext()) {
                var stage = itr.next()
                if (stageId == stage.id) {
                    stagename = stage.stage
                }
            }
        }
        return stagename
    }

    fun setViewFormRecycler(recyclerView: RecyclerView?, list: List<ArtBuyQcResponse>?) {
        recyclerView?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewQcAdapter = list?.let { ViewQcRecyclerAdapter(requireContext(), it) }
        recyclerView?.adapter = mViewQcAdapter
//        mViewQcAdapter?.notifyDataSetChanged()
    }

//    fun setSaveForms() {
//        var qcObj = enqID?.toLong()?.let { QcPredicates.getQcResponsesByEnq(it) }
//        if (qcObj != null) {
//            mBinding?.viewQcFormLayout?.removeAllViews()
//            var oldStageID:Long?=1L
//            var newStageID:Long?=0L
//            var respList = ArrayList<ArtBuyQcResponse>()
//            var arrRespList = ArrayList<List<ArtBuyQcResponse>>()
//
//            var formData = qcObj?.qcResponseString?.let { Utility.getBuyerQcResponse(it) }
//                var itr1 = formData?.data?.iterator()
//                if (itr1 != null) {
//                    while (itr1.hasNext()) {
//                        var data1 = itr1.next()
//                        newStageID = data1?.stageId
//
//                        if(newStageID == oldStageID){
//                            respList.add(data1)
//                        }else{
//                            //set Linear Layout
//                            oldStageID = newStageID
//                            var addList = respList.toList()
//                            arrRespList.add(addList)
//                            respList?.clear()
//                            Log.e("FormList","list : $respList")
//                        }
//                    }
//
//                    Log.e("FormList","list : $arrRespList")
//                }
//
//            var itr2 = arrRespList.iterator()
//            if(itr2!=null){
//                while (itr2.hasNext()){
//                    var data2 = itr2.next()
//                    var itr3 = data2.iterator()
//                    var stageTitle: String? = ""
//
//                    //set Linear Layout
//                    var newView = activity?.layoutInflater?.inflate(
//                        R.layout.item_qc_form_view_header,
//                        null
//                    ) as LinearLayout
//                    var title = newView.findViewById(R.id.qc_stage) as TextView
//                    var itemLayout = newView.findViewById(R.id.saved_form_top_part) as LinearLayout
//
//                    var itemRecyclerView =
//                        newView?.findViewById(R.id.saved_recyler_list) as RecyclerView
//
//
//                    if (data2 != null) {
//                        setViewFormRecycler(itemRecyclerView, data2)
//                    }
//
//                    itemLayout?.setOnClickListener {
//                        if (itemRecyclerView.visibility == View.GONE) {
//                            itemRecyclerView.visibility = View.VISIBLE
//                        } else {
//                            itemRecyclerView.visibility = View.GONE
//                        }
//                    }
//
//                    mBinding?.viewQcFormLayout?.addView(newView)
//
//                    if (itr3 != null) {
//                        while (itr3.hasNext()) {
//                            var form = itr3.next()
//                            stageTitle = getFormTitle(form.stageId)
//                        }
//                    }
//                    mViewQcAdapter?.updateQcForm(data2)
//                    title.text = stageTitle
//                }
//            }
//        }
//    }
    fun setSaveForms() {
    var qcObj = enqID?.toLong()?.let { QcPredicates.getQcResponsesByEnq(it) }
    if (qcObj != null) {
        mBinding?.viewQcFormLayout?.removeAllViews()
        var formData = qcObj?.qcResponseString?.let {
            Utility.getArtisanQcResponse(it)
        }
        var itr1 = formData?.artisanQcResponses?.iterator()
        if (itr1 != null) {
            while (itr1.hasNext()) {
                var data1 = itr1.next()
                var itr2 = data1.iterator()
                var stageTitle: String? = ""

                //set Linear Layout
                var newView = activity?.layoutInflater?.inflate(
                    R.layout.item_qc_form_view_header,
                    null
                ) as LinearLayout
                var title = newView.findViewById(R.id.qc_stage) as TextView
                var itemLayout = newView.findViewById(R.id.saved_form_top_part) as LinearLayout

                var itemRecyclerView =
                    newView?.findViewById(R.id.saved_recyler_list) as RecyclerView


                if (data1 != null) {
                    setViewFormRecycler(itemRecyclerView, data1)
                }

                itemLayout?.setOnClickListener {
                    if (itemRecyclerView.visibility == View.GONE) {
                        itemRecyclerView.visibility = View.VISIBLE
                    } else {
                        itemRecyclerView.visibility = View.GONE
                    }
                }

//                    newView.layoutParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mBinding?.viewQcFormLayout?.addView(newView)

                if (itr2 != null) {
                    while (itr2.hasNext()) {
                        var form = itr2.next()
                        stageTitle = getFormTitle(form.stageId)
                    }
                }
                mViewQcAdapter?.updateQcForm(data1)
                title.text = stageTitle
            }
        }
    }
}



    fun viewLoader() {
        mBinding?.swipeForm?.isRefreshing = true
    }

    fun hideLoader() {
        mBinding?.swipeForm?.isRefreshing = false
    }


    override fun onGetQcFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("QCResponse", "OnFailure")
                hideLoader()
            }
            )
        } catch (e: Exception) {
            Log.e("QCResponse", "Exception onFailure " + e.message)
        }
    }

    override fun onGetQcSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("QCResponse", "OnFailure")
                hideLoader()
                setSaveForms()
//                mQcRespList = maxStageID?.let { QcPredicates.getArtQcResponsesbyEnq(enqID?.toLong()!!, it) }
            }
            )
        } catch (e: Exception) {
            Log.e("QCResponse", "Exception onFailure " + e.message)
        }
    }

    companion object {
        fun newInstance(param1: String,param2: String) =
            BuyerViewQcFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}