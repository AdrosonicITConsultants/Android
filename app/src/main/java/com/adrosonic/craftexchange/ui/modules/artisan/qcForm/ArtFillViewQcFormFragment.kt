package com.adrosonic.craftexchange.ui.modules.artisan.qcForm

import android.app.Dialog
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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchange.database.predicates.QcPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtFillViewQcFormBinding
import com.adrosonic.craftexchange.enums.ActionForm
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchange.repository.data.response.qc.ArtBuyQcResponse
import com.adrosonic.craftexchange.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchange.repository.data.response.qc.QuestionListData
import com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter.FillQcRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter.ViewQcRecyclerAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.QCViewModel
import java.util.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtFillViewQcFormFragment : Fragment(),
    QCViewModel.GetQCResponseInterface,
    FillQcRecyclerAdapter.UpdateQuesAnsInterface {
    private var param1: String? = null
    private var param2: Long? = null

    private var mBinding: FragmentArtFillViewQcFormBinding? = null

    var enqID: String? = ""
    var orderStatus: Long?=0L
    var qcObj: QcDetails? = null
    var maxStageID: Long? = 0
    var maxStage: String? = ""

    var qcStages: InnerStageData? = null
    var qcQues: QCQuestionData? = null

    private var orderDetails: Orders? = null
    private var url: String? = ""

    var weft: String? = ""
    var warp: String? = ""
    var extraweft: String? = ""
    var prodCategory: String? = ""
    var status: String? = ""

    var dialog: Dialog? = null
    var dialogMsg: String? = ""

    var mFillQcAdapter: FillQcRecyclerAdapter? = null
    var mViewQcAdapter: ViewQcRecyclerAdapter? = null


    var mQcRespList: QcDetails? = null
    var mQcQuesList = ArrayList<QuestionListData>()
    var mQuesAnsList = ArrayList<QuestionAnswer>()
    var mQuesAnsFullList = ArrayList<QuestionAnswer>()

    var mSavedQuesAnsList = ArrayList<ArtBuyQcResponse>()


    val mOrdVM: OrdersViewModel by viewModels()
    val mQcVM: QCViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getLong(ARG_PARAM2)
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
                R.layout.fragment_art_fill_view_qc_form,
                container,
                false
            )
        if (param1 != null) {
            enqID = if (param1!!.isNotEmpty()) param1 else "0"
        }
        if (param2 != null) {
            orderStatus = if (param2!=null) param2 else 0L
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.swipeForm?.isEnabled = false
        qcStages = Utility?.getQcStageData()
        qcQues = Utility?.getQcQuesData()
        mQcVM?.getQcListener = this
        setFillFormRecycler()


        dialog = Utility.multiLoadingDialog(requireContext(), dialogMsg.toString())
        orderDetails = enqID?.toLong()?.let { orderStatus?.toLong()?.let { it1 ->
            mOrdVM?.getSingleOnOrderData(it,
                it1
            )
        } }?.value
        if (orderDetails != null) {
            setDetails()
        }

        if (Utility.checkIfInternetConnected(requireContext())) {
            enqID?.toLong()?.let { mQcVM?.getArtisanQCResponse(it) }
            viewLoader()
        } else {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            setQCStage()
            setSaveForms()
        }

//        mBinding?.swipeForm?.setOnRefreshListener {
//            if (Utility.checkIfInternetConnected(requireContext())) {
//                enqID?.toLong()?.let { mQcVM?.getArtisanQCResponse(it) }
//                viewLoader()
//            } else {
//                hideLoader()
//                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
//                setQCStage()
//                setSaveForms()
//            }
//        }

        mQcVM.getQcResponsesbyEnq(enqID?.toLong()!!)
            .observe(viewLifecycleOwner, Observer<QcDetails> {
                mQcRespList = it
                maxStageID?.let { it1 -> mFillQcAdapter?.updateQcForm(mQcQuesList, it1) }
            })

        mBinding?.btnSaveQc?.setOnClickListener {
            setQuesAnsList()
            if (Utility.checkIfInternetConnected(requireContext())) {
                Log.e("QCFA", "Saved List : $mQuesAnsFullList")
                mQcVM?.saveOrSendQcForm(
                    enqID?.toLong(),
                    SaveOrSendQcRequest(
                        enqID?.toLong(),
                        mQuesAnsFullList,
                        ActionForm.SAVE.getId(),
                        maxStageID
                    )
                )
                dialogMsg = "Saving"
                dialog = Utility.multiLoadingDialog(requireContext(), dialogMsg!!)
                dialog?.show()
                mQuesAnsList?.clear()
                mQuesAnsFullList?.clear()

            } else {
                maxStageID?.let { it1 ->
                    QcPredicates.insertQcForOffline(
                        enqID?.toLong()!!,
                        it1,
                        ActionForm.SAVE.getId(),
                        SaveOrSendQcRequest(
                            enqID?.toLong(),
                            mQuesAnsFullList,
                            ActionForm.SAVE.getId(),
                            maxStageID
                        )
                    )
                }
                Utility.displayMessage(
                    "Qc will be saved once internet connectivity is regained.",
                    requireContext()
                )
                mQuesAnsList?.clear()
                mQuesAnsFullList?.clear()
            }
        }

        mBinding?.btnSendQc?.setOnClickListener {
            setQuesAnsList()
            if (Utility.checkIfInternetConnected(requireContext())) {
                Log.e("QCFA", "Sent List : $mQuesAnsFullList")
                mQcVM?.saveOrSendQcForm(
                    enqID?.toLong(),
                    SaveOrSendQcRequest(
                        enqID?.toLong(),
                        mQuesAnsFullList,
                        ActionForm.SEND.getId(),
                        maxStageID
                    )
                )
                dialogMsg = "Sending"
                dialog = Utility.multiLoadingDialog(requireContext(), dialogMsg!!)
                dialog?.show()
                mQuesAnsList?.clear()
                mQuesAnsFullList?.clear()
            } else {
                maxStageID?.let { it1 ->
                    QcPredicates.insertQcForOffline(
                        enqID?.toLong()!!,
                        it1,
                        ActionForm.SEND.getId(),
                        SaveOrSendQcRequest(
                            enqID?.toLong(),
                            mQuesAnsFullList,
                            ActionForm.SEND.getId(),
                            maxStageID
                        )
                    )
                }
                Utility.displayMessage(
                    "Qc will be sent once internet connectivity is regained.",
                    requireContext()
                )
                mQuesAnsList?.clear()
                mQuesAnsFullList?.clear()

            }
        }
    }

    fun setQuesAnsList() {
        var itr1 = mQuesAnsFullList.iterator()
        if (itr1 != null) {
            while (itr1.hasNext()) {
                var list1 = itr1.next()
                mQuesAnsList?.forEach {
                    if (it.questionId == list1?.questionId) {
                        list1?.answer = it.answer
                    }
                }
            }
        }
    }

    fun setDetails() {
        mBinding?.orderCode?.text = orderDetails?.orderCode
        mBinding?.companyName?.text = orderDetails?.brandName
        setProductImage()
        setProductName()
    }

    fun setProductImage() {
        val image = orderDetails?.productImages?.split((",").toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
        } else {
            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
        }
        mBinding?.productImage?.let {
            ImageSetter.setImage(
                requireActivity(),
                url!!,
                it,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder
            )
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setProductName() {
        //Product name or Product cloth details
        if (orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
        } else {
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if (it.first == orderDetails?.weftYarnId) {
                    weft = it.second
                }
                if (it.first == orderDetails?.warpYarnId) {
                    warp = it.second
                }
                if (it.first == orderDetails?.extraWeftYarnId) {
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if (it.first == orderDetails?.productCategoryId) {
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft}"
            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }
                ?.let {
                    ForegroundColorSpan(
                        it
                    )
                }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)
        }
    }

    fun setQCStage() {
        mQcQuesList?.clear()
        mQuesAnsList.clear()
        mQuesAnsFullList.clear()

        when(orderStatus){

            0L -> {
                mBinding?.qcStageFormLayout?.visibility = View.VISIBLE
                qcObj = enqID?.toLong()?.let { QcPredicates.getStageIdOfEnquiry(it) }
                Log.e("QCD", "$qcObj")
                if (qcObj?.stageID == 0L) {
                    maxStageID = 1L
                } else {
                    if (qcObj?.isSend == 0L) {
                        maxStageID = qcObj?.stageID
                    } else {
                        maxStageID = qcObj?.stageID?.plus(1)
                    }
                }
                if (8 > maxStageID!!) {
                    mBinding?.qcStageFormLayout?.visibility = View.VISIBLE
                    mBinding?.enquiryStatusDot?.visibility = View.VISIBLE

                    context?.let { ContextCompat.getColor(it, R.color.dark_green) }
                        ?.let { mBinding?.enquiryStatusDot?.setColorFilter(it) }

                    var itr = qcStages?.data?.iterator()
                    if (itr != null) {
                        while (itr.hasNext()) {
                            var stage = itr.next()
                            if (maxStageID == stage.id) {
                                maxStage = stage.stage
                            }
                        }
                    }

                    var itr2 = qcQues?.data?.iterator()
                    if (itr2 != null) {
                        while (itr2.hasNext()) {
                            var itr3 = itr2.next().iterator()
                            if (itr3 != null) {
                                while (itr3.hasNext()) {
                                    var qD = itr3.next()
                                    if (qD.stageId == maxStageID) {
                                        mQcQuesList?.add(qD)
                                        mQuesAnsFullList?.add(QuestionAnswer(qD?.questionNo, ""))
                                    }
                                }
                            }
                        }
                    }

                    maxStageID?.let { mFillQcAdapter?.updateQcForm(mQcQuesList, it) }
                    mBinding?.enquiryStatusText?.text = maxStage
                    mBinding?.stageName?.text = "Quality check for $maxStage"
                } else {
                    mBinding?.qcStageFormLayout?.visibility = View.GONE
                    mBinding?.enquiryStatusDot?.visibility = View.GONE
                }
            }

            1L -> {
                mBinding?.qcStageFormLayout?.visibility = View.GONE
            }

            else -> {
                mBinding?.qcStageFormLayout?.visibility = View.GONE
            }
        }

    }

    fun setFillFormRecycler() {
        mBinding?.formRecycler?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mFillQcAdapter = maxStageID?.let {
            enqID?.toLong()?.let { it1 ->
                FillQcRecyclerAdapter(
                    requireContext(), mQcQuesList, it,
                    it1
                )
            }
        }
        mBinding?.formRecycler?.adapter = mFillQcAdapter
        mFillQcAdapter?.qcAdapterListener =
            this  //important to set adapter first and then call listener
    }

    fun setSaveForms() {
        var qcObj = enqID?.toLong()?.let { QcPredicates.getQcResponsesByEnq(it) }
        if (qcObj != null) {
            mBinding?.viewQcFormLayout?.removeAllViews()
            var formData = qcObj?.qcResponseString?.let { Utility.getArtisanQcResponse(it) }
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

    fun viewLoader() {
        mBinding?.swipeForm?.isRefreshing = true
    }

    fun hideLoader() {
        mBinding?.swipeForm?.isRefreshing = false
    }

    override fun addQuesAns(listElement: QuestionAnswer) {
        try {
            Handler(Looper.getMainLooper()).post {
                var newObj: QuestionAnswer? = null
                var oldObj: QuestionAnswer? = null

                if (mQuesAnsList?.isNotEmpty()) {
                    var itr = mQuesAnsList.iterator()
                    if (itr != null) {
                        while (itr.hasNext()) {
                            var data = itr.next()
                            if (data.questionId == listElement.questionId) {
                                newObj = listElement
                                oldObj = data
                            }
                        }
                    }
                    if (newObj == null) {
                        mQuesAnsList.add(listElement)
                        Log.e("QCFA", "ItemAdded : ($listElement))")

                    } else {
                        mQuesAnsList.remove(oldObj)
                        mQuesAnsList.add(newObj)
                        Log.e("QCFA", "ItemReplaced : ($oldObj) to ($newObj))")
                    }
                } else {
                    mQuesAnsList.add(listElement)
                    Log.e("QCFA", "ItemAdded : ($listElement))")
                }
                Log.e("QCFA", "Q&A list updated : $mQuesAnsList....size : ${mQuesAnsList.size}")
            }
        } catch (e: Exception) {
            Log.e("QCFA", "Exception onFailure " + e.message)
        }
    }

    override fun onGetQcFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("QCResponse", "OnFailure")
                hideLoader()
                dialog?.cancel()
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
                mQuesAnsFullList?.clear()
                mQuesAnsList?.clear()
                mFillQcAdapter?.refreshAdapter()
                hideLoader()
                setQCStage()
                setSaveForms()
                dialog?.cancel()

//                mQcRespList = maxStageID?.let { QcPredicates.getArtQcResponsesbyEnq(enqID?.toLong()!!, it) }
            }
            )
        } catch (e: Exception) {
            Log.e("QCResponse", "Exception onFailure " + e.message)
        }
    }

    companion object {
        fun newInstance(param1: String,param2: Long) =
            ArtFillViewQcFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putLong(ARG_PARAM2, param2)
                }
            }
    }
}