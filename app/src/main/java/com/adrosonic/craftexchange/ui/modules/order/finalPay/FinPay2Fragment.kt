package com.adrosonic.craftexchange.ui.modules.order.finalPay

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentFinPay2Binding
import com.adrosonic.craftexchange.enums.DocumentType
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.transaction.transactionIntent
import com.adrosonic.craftexchange.ui.modules.transaction.viewDocument
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FinPay2Fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    var enqID : String?= ""

    private var mBinding: FragmentFinPay2Binding?= null

    val mOrdVM : OrdersViewModel by viewModels()

    private var orderDetails : Orders?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""

    var amountPaid : String?=""

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fin_pay2, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
        if(param2!=null){
            amountPaid = if(param2!!.isNotEmpty())param2 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderDetails = enqID?.toLong()?.let { mOrdVM?.loadSingleOrderDetails(it,0) }

        if(orderDetails != null){
            setDetails()
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnViewTransac?.setOnClickListener {
//            requireActivity()?.startActivity(requireActivity()?.transactionIntent())
            val intent = Intent(enqID.let { it1 -> requireContext().viewDocument(it1!!.toLong(),DocumentType.FINALPAY.getId()) })
            startActivity(intent)
        }
        mBinding?.btnChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }
    }

    fun setDetails(){
        mBinding?.orderCode?.text = orderDetails?.orderCode ?: "N.A"
        mBinding?.amountPaid?.text = "₹ $amountPaid"

        setProductImage()
        setProductName()
    }

    fun setProductImage(){
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
    }

    fun setProductName(){
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

    companion object {
        fun newInstance(param1: String, param2: String) =
            FinPay2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}